package com.easylink.cloud.control.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class TestBindService extends Service {
    private static final String TAG = "UploadBindService";
    private int progress;
    // 这里要使用静态的，不然每次开启都会产生一个新的Binder对象
    private static MyBinder binder = null;

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new MyBinder();
            Log.d(TAG, "onBind: ");
        }
        return binder; // 这里不要直接返回new Binder()
    }

    public class MyBinder extends Binder {
        void startDownload() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        //...... 假设这里有一个耗时操作
                        progress += 1; //更新进度
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        public int getProgress() { return progress; } //用来回去进度
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

}
