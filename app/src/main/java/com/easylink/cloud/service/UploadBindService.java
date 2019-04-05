package com.easylink.cloud.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.easylink.cloud.absolute.iUploadListener;
import com.easylink.cloud.modle.Task;
import com.easylink.cloud.util.TableUploadTaskCRUD;
import com.easylink.cloud.web.Client;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class UploadBindService extends Service implements iUploadListener {
    private Executor executor = Executors.newFixedThreadPool(4);
    private Deque<Task> tasks = new LinkedList<>();
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
        for (Task task : tasks) {
            if (task.key.equals(key)) {
                task.progress = progress;
            }
        }
    }

    @Override
    public void onSuccess(String key) {
        for (Task task : tasks) {
            if (task.key.equals(key)) {
                task.isSuccess = true;
                TableUploadTaskCRUD.getInstant().insertUploadTask(task);
            }
        }
        tasks.remove(new Task(key));
    }

    @Override
    public void onFailed(String key) {
        for (Task task : tasks) {
            if (task.key.equals(key)) {
                task.isFailed = true;
                TableUploadTaskCRUD.getInstant().insertUploadTask(task);
            }
        }
        tasks.remove(new Task(key));
    }

    public class MyBinder extends Binder {

        public void addTask(String key, String path) {
            Task task = new Task(key, path);
            tasks.offer(task);
            executor.execute(() -> Client.getClient().upload(UploadBindService.this, task));
        }

        public void pauseTask(String key) {

            for (Task task : tasks) {
                if (task.key.equals(key)) {
                    task.isPause = true;
                    task.isResume = false;
                }
            }
        }

        public void resumeTask(String key) {
            for (Task task : tasks) {
                if (task.key.equals(key)) {
                    task.isPause = false;
                    task.isResume = true;
                }
            }
        }

        public void canceledTask(String key) {
            for (Task task : tasks) {
                if (task.key.equals(key)) {
                    task.isCanceled = true;
                    TableUploadTaskCRUD.getInstant().insertUploadTask(task);
                }
            }
            tasks.remove(new Task(key));
        }


        public Deque<Task> findTask() {
            return tasks;
        }
    }
}
