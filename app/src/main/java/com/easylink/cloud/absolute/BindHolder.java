package com.easylink.cloud.absolute;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BindHolder extends RecyclerView.ViewHolder {
    public BindHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(Object index);
}
