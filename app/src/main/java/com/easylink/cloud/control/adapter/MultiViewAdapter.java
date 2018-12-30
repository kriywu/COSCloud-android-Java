package com.easylink.cloud.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.control.FilePickActivity;
import com.easylink.cloud.modle.Constant;

public class MultiViewAdapter extends RecyclerView.Adapter<BindHolder> {

    private String[] titles = {"照片", "视频", "音乐", "文档", "压缩包", "APK"};
    private int[] drawables = {R.drawable.icon_photo, R.drawable.icon_mv, R.drawable.icon_music, R.drawable.icon_file, R.drawable.icon_rar, R.drawable.icon_apk};
    private Context context;


    public MultiViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_image_text_v, viewGroup, false);
        return new ImageTextHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class ImageTextHolder extends BindHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView textView;
        private Integer index;

        public ImageTextHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivIcon);
            textView = itemView.findViewById(R.id.tvTitle);
        }

        @Override
        public void bind(Object i) {
            this.index = (Integer) i;
            itemView.setOnClickListener(this);
            imageView.setImageDrawable(context.getResources().getDrawable(drawables[index], null));
            textView.setText(titles[index]);
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, FilePickActivity.class);
            switch (index) {
                case 0:
                    intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_PHOTO);
                    break;
                case 1:
                    intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_VIDEO);
                    break;
                case 2:
                    intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_MUSIC);
                    break;
                case 3:
                    intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_DOC);
                    break;
                case 4:
                    intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_RAR);
                    break;
                case 5:
                    intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_APK);
                    break;
            }
            ((Activity) context).startActivity(intent);

        }


    }
}
