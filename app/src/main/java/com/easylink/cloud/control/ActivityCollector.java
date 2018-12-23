package com.easylink.cloud.control;

import android.app.Activity;

import java.util.ArrayList;
import java.util.LinkedList;

public class ActivityCollector {
    private static LinkedList<Activity> list = new LinkedList<>();
    public static void addActivity(Activity activity){
        list.add(activity);
    }
    public static void removeActivity(Activity activity){
        list.remove(activity);
    }
    public static void finishALl(){
        for(Activity activity: list){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        list.clear();
    }
}
