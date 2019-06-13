package com.easylink.cloud.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class DownloadService extends Service {
    private static final String TAG = "DownloadActivity";

    private DownloadBinder binder = new DownloadBinder();
    private DownloadTask downloadTask;
    private String url;


    public DownloadService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (downloadTask != null) downloadTask.clearListener();
        return super.onUnbind(intent);

    }

    class DownloadBinder extends Binder {
        public void startDownload(String u, iDownloadListener listener) {
            if (downloadTask == null) {
                url = u;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(url);
            } else {
                downloadTask.setListener(listener);

            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
                downloadTask = null;
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
                downloadTask = null;
            }

            if (url != null) {
                String name = url.substring(url.lastIndexOf('/'));
                String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(dir + name);
                if (file.exists()) file.delete();
            }
        }
    }
}
