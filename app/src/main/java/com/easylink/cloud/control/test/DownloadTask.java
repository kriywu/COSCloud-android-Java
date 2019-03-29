package com.easylink.cloud.control.test;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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

    private iDownloadListener listener;
    private boolean isCanceled;
    private boolean isPaused;
    private int lastProgress;

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    public DownloadTask(iDownloadListener listener) {
        this.listener = listener;
    }

    public void setListener(iDownloadListener listener) {
        this.listener = listener;
    }

    public void clearListener() {
        listener = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute: ");
    }

    @Override
    protected Integer doInBackground(String... strings) {
        InputStream inputStream = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            long downloadLength = 0; // 已经被下载的长度
            String url = strings[0];
            String fileName = url.substring(url.lastIndexOf('/'));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);

            Log.d(TAG, "doInBackground: " + file.getAbsolutePath());
            if (file.exists()) {
                downloadLength = file.length();
                Log.d(TAG, "doInBackground: exists" + file.length());
            }


            long totalLength = getTotalLength(url);
            if (totalLength == 0) return TYPE_FAILED; // 没找到
            else if (totalLength == downloadLength) return TYPE_SUCCESS; // 已经下载完了

            Log.d(TAG, "doInBackground: download length" + downloadLength);
            Log.d(TAG, "doInBackground: total length" + totalLength);

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
                    if (isCanceled) return TYPE_CANCELED;
                    else if (isPaused) return TYPE_PAUSED;
                    else total += len;
                    savedFile.write(b, 0, len);
                    int progress = (int) (100 * (total + downloadLength) / totalLength);
                    publishProgress(progress); // 更新进度
                    Log.d(TAG, "doInBackground: " + progress);

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
            if (listener != null) listener.onProgress(values[0]);
            lastProgress = values[0];
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (listener == null) return;
        switch (integer) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }

    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }


    public long getTotalLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.body() != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            Log.d(TAG, "getTotalLength: " + response.headers());
            response.body().close();
            return contentLength;
        }
        return 0;
    }
}
