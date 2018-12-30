package com.easylink.cloud.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.easylink.cloud.web.Client;
import com.easylink.cloud.modle.Constant;

import java.io.File;
import java.util.ArrayList;

public class UploadService extends Service {
    private int sumSize = 0;
    private ArrayList<String> paths;
    private String prefix;
    private UploadBinder binder = new UploadBinder();
    private Context context;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
        paths = intent.getStringArrayListExtra(Constant.EXTRA_PATHS);
        prefix = intent.getStringExtra(Constant.EXTRA_PREFIX);

        for (String p : paths) {
            Client.getClient(this).upload(Constant.bucket, prefix + new File(p).getName(), p); // bucket key path
        }


        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class UploadBinder extends Binder {
        public void startUpload() {
            Toast.makeText(getApplicationContext(), "Start service", Toast.LENGTH_LONG).show();
        }

        public void stopUpload() {
            Toast.makeText(getApplicationContext(), "Stop service", Toast.LENGTH_LONG).show();
        }
    }
}
