package com.easylink.cloud.absolute;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BindHolder extends RecyclerView.ViewHolder {
    public BindHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(Object index);
}
