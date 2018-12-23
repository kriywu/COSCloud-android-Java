package com.easylink.cloud.control.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iPickPhoto;
import com.easylink.cloud.modle.LocalFile;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotosHolder> {
    private Context context;
    private iPickPhoto callback;
    List<LocalFile> photos;

    public PhotoAdapter(Context context, iPickPhoto callback, List<LocalFile> photos) {
        this.context = context;
        this.callback = callback;
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotosHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_photo_pick, viewGroup, false);
        return new PhotosHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosHolder viewHolder, int i) {
        viewHolder.bind(photos.get(i));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PhotosHolder extends BindHolder {
        private ImageView imageView;
        public CheckBox checkBox;

        public PhotosHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            checkBox = itemView.findViewById(R.id.cb_photo);
        }

        @Override
        public void bind(Object index) {
            final LocalFile file = (LocalFile) index;
            Glide.with(context).load(file.getPath()).into(imageView);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) callback.pick(file);
                    else callback.unPick(file);
                }
            });
            // 一定需要放在设置监听器后面，不过放在设置监听器前面，会自动回调之前的监听器，而之前的监听器的index是final类型的，所以会出现显示混乱
            checkBox.setChecked(callback.isPick(file));
        }


    }
}
