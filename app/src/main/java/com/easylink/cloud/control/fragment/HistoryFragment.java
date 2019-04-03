package com.easylink.cloud.control.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.control.adapter.ProgressAdapter;
import com.easylink.cloud.modle.UploadTask;
import com.easylink.cloud.util.DBHelper;
import com.easylink.cloud.util.TableUploadTaskCRUD;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class HistoryFragment extends BaseFragment {
    private static final String TAG = "HistoryFragment";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flash)
    SwipeRefreshLayout swipeRefreshLayout;

    private Context context;
    private Deque<UploadTask> tasks = new LinkedList<>();
    private DBHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        dbHelper = new DBHelper(context, "upload_history.db", null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setAdapter(new ProgressAdapter(context, null, tasks));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            TableUploadTaskCRUD.getInstant().queryUploadTask((List<UploadTask>) tasks);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            },500);


        });
        queryUploadTask();
        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.recyclerview_single;
    }

    public void queryUploadTask() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.UPLOAD_HISTORY, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex("ID"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                int progress = cursor.getInt(cursor.getColumnIndex("progress"));
                boolean isSucceed = cursor.getInt(cursor.getColumnIndex("isSucceed")) != 0;
                boolean isFailed = cursor.getInt(cursor.getColumnIndex("isFailed")) != 0;
                boolean isCanceled = cursor.getInt(cursor.getColumnIndex("isCanceled")) != 0;
                UploadTask task = new UploadTask(id, path);
                task.name = name;
                task.progress = progress;
                task.isCanceled = isCanceled;
                task.isFailed = isFailed;
                task.isSuccess = isSucceed;
                tasks.add(task);
            } while (cursor.moveToNext());
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            cursor.close();
        }
    }

    public static HistoryFragment newInstance() {

        Bundle args = new Bundle();

        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
