package com.easylink.cloud.control.adapter;

import com.easylink.cloud.absolute.BindHolder;

import androidx.recyclerview.widget.RecyclerView;


/**
 * adapter 的工程模式
 */
public class BindAdapterFactory {
    public static <T extends RecyclerView.Adapter<BindHolder>> T getInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
