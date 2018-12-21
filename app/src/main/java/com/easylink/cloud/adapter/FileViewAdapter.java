package com.easylink.cloud.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.easylink.cloud.R;
import com.easylink.cloud.absolute.FetchListCallBack;
import com.easylink.cloud.modle.EFile;
import com.easylink.cloud.network.FetchList;
import com.easylink.cloud.util.Constant;

import java.util.List;

public class FileViewAdapter extends RecyclerView.Adapter<FileViewAdapter.FileViewHolder> {
    private Context context;
    private FetchListCallBack callBack;
    private List<EFile> files;

    public FileViewAdapter(Context context, FetchListCallBack callBack, List<EFile> files) {
        this.context = context;
        this.callBack = callBack;
        this.files = files;
    }

    public void setFiles(List<EFile> files) {
        if (this.files == null) this.files = files;
        else {
            this.files.clear();
            this.files.addAll(files);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_file, viewGroup, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder fileViewHolder, int i) {
        fileViewHolder.bind(files.get(i));
    }


    @Override
    public int getItemCount() {
        return files == null ? 1 : files.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private ImageView ivMore;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_filename);
            imageView = itemView.findViewById(R.id.iv_icon);
            ivMore = itemView.findViewById(R.id.iv_more);
        }

        public void bind(final EFile file) {
            textView.setText(file.getName());
            if (file.getState().equals(Constant.DIR)) {
                imageView.setImageResource(R.drawable.folder);
            } else {
                imageView.setImageResource(R.drawable.file);
            }
            textView.setText(file.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FetchList(context, callBack).setPrefix(file.getName()).execute();
                }
            });
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMore(file);
                }
            });
        }
        public void showMore(EFile file){
            AlertDialog dialog = new AlertDialog.Builder(context).
                    setView(R.layout.dialog_more_option).
                    setTitle(file.getName()).
                    create();
            dialog.show();
        }
    }
}
