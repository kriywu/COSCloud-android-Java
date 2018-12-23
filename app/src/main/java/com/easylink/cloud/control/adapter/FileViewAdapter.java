package com.easylink.cloud.control.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.control.holder.*;

import java.util.List;

public class FileViewAdapter extends RecyclerView.Adapter<BindHolder> {
    private Context context;
    private iQueryList callBack;
    private List datas;
    private int FLAG = 1; // viewHolder的类型 1 ,2,3,4


    public FileViewAdapter(Context context, iQueryList callBack, List<Object> datas) {
        this.context = context;
        this.callBack = callBack;
        this.datas = datas;
    }

    public FileViewAdapter(Context context, iQueryList callBack, List<Object> files, int FLAG) {
        this(context, callBack, files);
        this.FLAG = FLAG;
    }

    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_file, viewGroup, false);
        if (FLAG == 1) return new EFileHolder(context, view, callBack);
        else return new PathHolder(context, view, callBack);
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder holder, int i) {
        holder.bind(datas.get(i));
    }


    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }
}
