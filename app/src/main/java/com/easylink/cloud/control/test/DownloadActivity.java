package com.easylink.cloud.control.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylink.cloud.R;

import androidx.appcompat.app.AppCompatActivity;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {

    Button btBegin;
    Button btPause;
    Button btCancel;
    TextView textView;
    ProgressBar pb;
    //String url = "https://iso.mirrors.ustc.edu.cn/manjaro-cd/gnome/18.0.4/manjaro-gnome-18.0.4-stable-x86_64.iso";
    String url = "http://fastsoft.onlinedown.net/down/eclipseinstwin64.exe";
    private static final String TAG = "DownloadActivity";
    DownloadService.DownloadBinder downloadBinder;
    iDownloadListener listener = new iDownloadListener() {
        int lastProgress = 0;

        @Override
        public void onProgress(int progress) {
            Log.d(TAG, "onProgress: ");
            String s = "\non Progress " + progress;
            pb.setProgress(progress);
            if (progress >= lastProgress + 5) {
                textView.append(s);
                lastProgress = progress;
            }
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess: ");
            String s = "\non Success " + 100 + "%";
            pb.setProgress(100);
            textView.append(s);
        }

        @Override
        public void onFailed() {
            Log.d(TAG, "onFailed: ");
            String s = "\non Failed ";
            textView.append(s);
        }

        @Override
        public void onPaused() {
            Log.d(TAG, "onPaused: ");
            String s = "\non Paused ";
            textView.append(s);
        }

        @Override
        public void onCanceled() {
            Log.d(TAG, "onCanceled: ");
            String s = "\non onCanceled ";
            textView.append(s);
        }
    };

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        btBegin = findViewById(R.id.btn_begin_download);
        btPause = findViewById(R.id.btn_pause_download);
        btCancel = findViewById(R.id.btn_cancel_download);
        textView = findViewById(R.id.tv_detail);
        pb = findViewById(R.id.pb_progress);
        btBegin.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btCancel.setOnClickListener(this);

        Intent intent = new Intent(this, DownloadService.class);

        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onClick(View v) {

        if (downloadBinder == null) return;
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.btn_begin_download:
                downloadBinder.startDownload(url, listener);
                break;
            case R.id.btn_pause_download:
                downloadBinder.pauseDownload();
                break;
            case R.id.btn_cancel_download:
                downloadBinder.cancelDownload();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }


}
