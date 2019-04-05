package com.easylink.cloud.control.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BindHolder;
import com.easylink.cloud.absolute.iUploadBinderController;
import com.easylink.cloud.modle.Task;
import com.easylink.cloud.util.TableUploadTaskCRUD;

import java.util.Deque;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressAdapter extends RecyclerView.Adapter<BindHolder> {
    private static final String TAG = "ProgressAdapter";
    private Context context ;
    private iUploadBinderController binderController = null;
    private LinkedList<Task> list;

    public ProgressAdapter(Context context, iUploadBinderController callback, Deque<Task> list) {
        this.context = context;
        binderController = callback;
        this.list = (LinkedList<Task>) list;
    }

    public void setData(LinkedList<Task> tasks) {
        this.list = tasks;
    }

    @NonNull
    @Override
    public BindHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (binderController != null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_progress_item, viewGroup, false);
            return new ProgressHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.view_history_item, viewGroup, false);
            return new HistoryHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BindHolder viewHolder, int i) {
        viewHolder.bind(list.get(i));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + (list == null ? 0 : list.size()));
        return list == null ? 0 : list.size();
    }

    private class HistoryHolder extends BindHolder {
        ImageView ivIcon;
        TextView tvName;
        ImageView ivCanceled;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            ivCanceled = itemView.findViewById(R.id.iv_cancelled);
        }

        @Override
        public void bind(Object index) {
            Task task = (Task) index;
            tvName.setText(task.name);
            ivCanceled.setOnClickListener(v -> {
                list.remove(getAdapterPosition());
                TableUploadTaskCRUD.getInstant().removeUploadTask(task);
                notifyItemRemoved(getAdapterPosition());
            });
        }
    }

    private class ProgressHolder extends BindHolder {
        ImageView ivIcon;
        TextView tvName;
        ProgressBar pbProgress;
        TextView tvProgress;
        ImageView ivAction;
        ImageView ivCanceled;

        public ProgressHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivAction = itemView.findViewById(R.id.iv_action);
            tvProgress = itemView.findViewById(R.id.tv_progress);
            ivCanceled = itemView.findViewById(R.id.iv_cancelled);
        }

        @Override
        public void bind(Object index) {
            Task task = (Task) index;
            tvName.setText(task.name);
            pbProgress.setProgress(task.progress);
            tvProgress.setText(task.progress + "");
            // 开始暂定
            ivAction.setOnClickListener(v -> {
                if (task.isPause) {
                    binderController.resume(task.key);
                    ivAction.setImageResource(R.drawable.ic_resume);
                } else {
                    binderController.pause(task.key);
                    ivAction.setImageResource(R.drawable.icon_stop);
                }
            });
            //取消
            ivCanceled.setOnClickListener(v -> {
                list.remove(getAdapterPosition());
                binderController.canceled(task.key);
                TableUploadTaskCRUD.getInstant().insertUploadTask(task);
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
