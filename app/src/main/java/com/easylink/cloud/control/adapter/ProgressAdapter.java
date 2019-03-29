package com.easylink.cloud.control.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.modle.FetchTask;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<BindHolder> {
    private Context context;
    private List<FetchTask> list;

    public ProgressAdapter(Context context, List<FetchTask> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_progress_item, viewGroup, false);
        return new ProgressHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder viewHolder, int i) {
        viewHolder.bind(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ProgressHolder extends BindHolder {
        private ImageView ivIcon;
        private TextView tvName;
        private ProgressBar pbProgress;
        private TextView tvProgress;
        private ImageView ivAction;

        public ProgressHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivAction = itemView.findViewById(R.id.iv_action);
            tvProgress = itemView.findViewById(R.id.tv_progress);
        }

        @Override
        public void bind(Object index) {
            FetchTask task = (FetchTask) index;
            tvName.setText(task.name);
            pbProgress.setProgress((int) task.progress);
            tvProgress.setText(task.progress + "");
        }
    }
}
