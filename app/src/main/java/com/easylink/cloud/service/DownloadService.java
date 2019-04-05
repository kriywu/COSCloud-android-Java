package com.easylink.cloud.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.easylink.cloud.modle.Task;
import com.easylink.cloud.network.DownloadTask;

import java.util.LinkedList;
import java.util.List;

public class DownloadService extends Service {
    private final List<Task> tasks = new LinkedList<>(); // 任务

    private static MyBinder binder;

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new MyBinder();
        }
        return binder;
    }

    public class MyBinder extends Binder {

        public void addTask(String key, String path) {
            Task task;
            synchronized (tasks) {
                task = new Task(key, path);
                tasks.add(task);

            }
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.task = task;
            downloadTask.execute();
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
                }
            }
            synchronized (tasks) {
                tasks.remove(new Task(key));
            }
        }

        public List<Task> findTask() {
            return tasks;
        }
    }
}
