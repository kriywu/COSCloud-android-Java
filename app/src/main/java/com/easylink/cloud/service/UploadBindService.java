package com.easylink.cloud.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.easylink.cloud.absolute.iDownloadListener;
import com.easylink.cloud.modle.UploadTask;
import com.easylink.cloud.util.DBHelper;
import com.easylink.cloud.util.TableUploadTaskCRUD;
import com.easylink.cloud.web.Client;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class UploadBindService extends Service implements iDownloadListener {
    private Executor executor = Executors.newFixedThreadPool(4);
    private static final String TAG = "UploadBindService";
    private Deque<UploadTask> tasks = new LinkedList<>();
    private static MyBinder binder = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new MyBinder();
        }
        return binder;
    }

    @Override
    public void onProgress(String key, int progress) {
        for (UploadTask task : tasks) {
            if (task.key.equals(key)) {
                task.progress = progress;
            }
        }
    }

    @Override
    public void onSuccess(String key) {
        for (UploadTask task : tasks) {
            if (task.key.equals(key)) {
                task.isSuccess = true;
                TableUploadTaskCRUD.getInstant().insertUploadTask(task);
            }
        }
        tasks.remove(new UploadTask(key));
    }

    @Override
    public void onFailed(String key) {
        for (UploadTask task : tasks) {
            if (task.key.equals(key)) {
                task.isFailed = true;
                TableUploadTaskCRUD.getInstant().insertUploadTask(task);
            }
        }
        tasks.remove(new UploadTask(key));
    }

    public class MyBinder extends Binder {

        public void addTask(String key, String path) {
            UploadTask task = new UploadTask(key, path);
            tasks.offer(task);
            executor.execute(() -> Client.getClient().upload2(UploadBindService.this, task));
        }

        public void pauseTask(String key) {

            for (UploadTask task : tasks) {
                if (task.key.equals(key)) {
                    task.isPause = true;
                    task.isResume = false;
                }
            }
        }

        public void resumeTask(String key) {
            for (UploadTask task : tasks) {
                if (task.key.equals(key)) {
                    task.isPause = false;
                    task.isResume = true;
                }
            }
        }

        public void canceledTask(String key) {
            for (UploadTask task : tasks) {
                if (task.key.equals(key)) {
                    task.isCanceled = true;
                    TableUploadTaskCRUD.getInstant().insertUploadTask(task);
                }
            }
            tasks.remove(new UploadTask(key));
        }


        public Deque<UploadTask> findTask() {
            return tasks;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
