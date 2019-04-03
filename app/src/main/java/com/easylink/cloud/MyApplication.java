package com.easylink.cloud;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.easylink.cloud.service.UploadService;
import com.easylink.cloud.util.DBHelper;

public class MyApplication extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();


    }

    public static Context getContext() {
        return context;
    }


}
