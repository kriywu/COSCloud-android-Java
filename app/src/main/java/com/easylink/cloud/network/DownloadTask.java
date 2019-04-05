package com.easylink.cloud.network;

import android.os.AsyncTask;
import android.os.Environment;

import com.easylink.cloud.absolute.iDownloadListener;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.Task;
import com.easylink.cloud.web.Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "DownloadActivity";
    private boolean isCanceled;
    private boolean isPaused;
    private int lastProgress;

    public Task task;
    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream inputStream = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            long downloadLength = 0; // 已经被下载的长度
            //url = strings[0];
            String url = Client.getClient().generateUrl(Constant.bucket, task.key);
            String fileName = url.substring(url.lastIndexOf('/'));
            // 下载存放地址
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);

            if (file.exists()) {
                downloadLength = file.length();
            }


            long totalLength = getTotalLength(url);
            if (totalLength == 0) return TYPE_FAILED; // 没找到
            else if (totalLength == downloadLength) return TYPE_SUCCESS; // 已经下载完了

            // 没有下载完毕，断点续传
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadLength + "-") // 告诉从哪儿开始下载
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute(); // 线程池中执行，为什么要开启线程呢？
            if (response != null && response.body() != null) {
                inputStream = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadLength);
                byte[] b = new byte[1024];
                long total = 0;
                int len;
                while ((len = inputStream.read(b)) != -1) {
                    if (task.isCanceled) return TYPE_CANCELED;
                    else if (task.isPause) return TYPE_PAUSED;
                    else total += len;
                    savedFile.write(b, 0, len);
                    int progress = (int) (100 * (total + downloadLength) / totalLength);
                    publishProgress(progress); // 更新进度
                }
                return TYPE_SUCCESS;
            }
            // failed
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (savedFile != null) {
                try {
                    savedFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isCanceled) Objects.requireNonNull(file).delete();
        }

        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] > lastProgress) {
            task.progress = values[0];
            lastProgress = values[0];
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case TYPE_SUCCESS:
                task.isSuccess = true;
                break;
            case TYPE_FAILED:
                task.isFailed = true;
                break;
            case TYPE_PAUSED:
                task.isPause = true;
                break;
            case TYPE_CANCELED:
                task.isCanceled = true;
                break;
            default:
                break;
        }

    }

    public long getTotalLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.body() != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}
