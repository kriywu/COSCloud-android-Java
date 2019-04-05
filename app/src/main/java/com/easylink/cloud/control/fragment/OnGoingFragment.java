package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.absolute.iUploadBinderController;
import com.easylink.cloud.control.adapter.ProgressAdapter;
import com.easylink.cloud.modle.Task;
import com.easylink.cloud.service.UploadBindService;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class OnGoingFragment extends BaseFragment implements iUploadBinderController {
    private static final String TAG = "OnGoingFragment";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flash)
    SwipeRefreshLayout swipeRefreshLayout;

    private Context context;
    private UploadBindService.MyBinder binder = null;
    private ProgressAdapter adapter;
    private Deque<Task> tasks = null;

    @SuppressLint("HandlerLeak")
    private
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tasks = binder.findTask();
            LinkedList<Task> linkedList = (LinkedList<Task>) tasks;
            Log.d(TAG, "handleMessage: " + tasks.size());

            adapter.setData(linkedList);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(1, 500);
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UploadBindService.MyBinder) service;
            handler.sendEmptyMessageDelayed(1, 500);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        adapter = new ProgressAdapter(context, this, tasks);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        context.bindService(new Intent(context, UploadBindService.class), connection, Context.BIND_AUTO_CREATE);
        swipeRefreshLayout.setEnabled(false);
        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.recyclerview_single;
    }

    public static OnGoingFragment newInstance() {

        Bundle args = new Bundle();

        OnGoingFragment fragment = new OnGoingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void pause(String key) {
        binder.pauseTask(key);
    }

    @Override
    public void resume(String key) {
        binder.resumeTask(key);
    }

    @Override
    public void canceled(String key) {
        binder.canceledTask(key);
    }
}
