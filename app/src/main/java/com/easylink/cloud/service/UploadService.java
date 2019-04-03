package com.easylink.cloud.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.widget.Toast;

import com.easylink.cloud.modle.UploadTask;
import com.easylink.cloud.util.StaticHelper;
import com.easylink.cloud.web.Client;
import com.easylink.cloud.modle.Constant;

import java.util.ArrayList;
import java.util.Date;

public class UploadService extends Service {
    private int sumSize = 0;
    private ArrayList<String> paths;
    private String prefix;
    private UploadBinder binder = new UploadBinder();
    private Context context;
    private LocalBroadcastManager broadcastManager;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
        // 上传的位置
        paths = intent.getStringArrayListExtra(Constant.EXTRA_PATHS);
        // 上传的路径
        prefix = intent.getStringExtra(Constant.EXTRA_PREFIX);
        broadcastManager = LocalBroadcastManager.getInstance(this);

        for (String p : paths) {
            //UploadTask task = new UploadTask(StaticHelper.geneTaskID(Constant.bucket, prefix, p, new Date()));

            //Client.getClient().upload(broadcastManager,task); // bucket prefix path
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
