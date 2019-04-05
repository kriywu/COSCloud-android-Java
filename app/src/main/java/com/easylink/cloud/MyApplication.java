package com.easylink.cloud;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.easylink.cloud.service.DownloadService;

public class MyApplication extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getString("DOWNLOAD_PATH", null) == null){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("DOWNLOAD_PATH","/storage/emulated/0");
            editor.apply();

        }
        context.startService(new Intent(context, DownloadService.class));

    }

    public static Context getContext() {
        return context;
    }


}
