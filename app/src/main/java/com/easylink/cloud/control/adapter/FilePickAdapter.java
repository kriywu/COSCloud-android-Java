package com.easylink.cloud.control.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iPickPhoto;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.LocalFile;
import com.easylink.cloud.modle.Music;

import java.io.File;
import java.util.List;

public class FilePickAdapter extends RecyclerView.Adapter<BindHolder> {
    private Context context;
    private iPickPhoto callback;
    private List<LocalFile> localFIle;
    private String flag;   //

    public FilePickAdapter(Context context, iPickPhoto callback, List localFIle, String flag) {
        this.context = context;
        this.callback = callback;
        this.localFIle = localFIle;
        this.flag = flag;
    }


    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        switch (flag) {
            case Constant.EXTRA_PHOTO:
                view = LayoutInflater.from(context).inflate(R.layout.view_photo_pick, viewGroup, false);
                return new PhotosHolder(view);
            case Constant.EXTRA_VIDEO:
                view = LayoutInflater.from(context).inflate(R.layout.view_photo_pick, viewGroup, false);
                return new PhotosHolder(view);
            case Constant.EXTRA_MUSIC:
                view = LayoutInflater.from(context).inflate(R.layout.view_song_picked, viewGroup, false);
                return new MusicHolder(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.view_file_picked, viewGroup, false);
                return new FileHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder viewHolder, int i) {
        viewHolder.bind(localFIle.get(i));
    }

    @Override
    public int getItemCount() {
        return localFIle.size();
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

    public class MusicHolder extends BindHolder {
        TextView tvName;
        TextView tvArtist;
        CheckBox cbPick;

        public MusicHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            cbPick = itemView.findViewById(R.id.cb_pick);
        }

        @Override
        public void bind(Object index) {
            final Music music = (Music) index;
            tvName.setText(music.getName());
            tvArtist.setText(music.getArtist());
            cbPick.setText(music.getSizeFormat() + " MB");
            cbPick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) callback.pick(music);
                    else callback.unPick(music);
                }
            });
            cbPick.setChecked(callback.isPick(music));
        }
    }

    public class FileHolder extends BindHolder {
        TextView tvName;
        CheckBox cbPick;

        public FileHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            cbPick = itemView.findViewById(R.id.cb_pick);
        }

        @Override
        public void bind(Object index) {
            final LocalFile file = (LocalFile) index;
            tvName.setText(new File(file.getPath()).getName());
            cbPick.setText(file.getSizeFormat() + " MB");
            cbPick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) callback.pick(file);
                    else callback.unPick(file);
                }
            });
            cbPick.setChecked(callback.isPick(file));
        }
    }

}
