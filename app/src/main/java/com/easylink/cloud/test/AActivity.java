package com.easylink.cloud.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easylink.cloud.R;

import java.lang.ref.WeakReference;

/**
 * 测试进度
 */

public class AActivity extends AppCompatActivity {
    private static final String TAG = "BIND A";

    WeakReference<Context> context = null;
    Handler handler = new MyHandler();
    TestBindService.MyBinder binder;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, binder.getProgress() + " " );
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (TestBindService.MyBinder) service;
            binder.startDownload();
            handler.sendEmptyMessageDelayed(1, 1000);
        }
        // 内存不足的时候调用
        @Override
        public void onServiceDisconnected(ComponentName name) { Log.d(TAG, "onServiceDisconnected: "); }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_null);
        context = new WeakReference<Context>(this);
        Intent intent = new Intent(context.get(), TestBindService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        //Glide.with(this).load("").into();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        handler.removeMessages(1);
    }
}
