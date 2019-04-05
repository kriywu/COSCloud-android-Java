package com.easylink.cloud.control.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.Task;
import com.easylink.cloud.service.DownloadService;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

public class NewFragment extends BaseFragment {
    @BindView(R.id.abl_download)
    AppBarLayout appBarLayout;
    @BindView(R.id.tl_download)
    Toolbar toolbar;
    @BindView(R.id.sv_download)
    SearchView searchView;
    @BindView(R.id.sr_download)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rv_download)
    RecyclerView recyclerView;

    private Context context;
    private static List<Task> tasks;
    private static DownloadService.MyBinder binder;
    private static MyHandler handler = new MyHandler();

    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tasks = binder.findTask();
            handler.sendEmptyMessageDelayed(1,500);
        }
    }
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.MyBinder) service;
            handler.sendEmptyMessageDelayed(1,500);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
        return view;
    }

    class NewViewHolder extends RecyclerView.ViewHolder{

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        context.bindService(new Intent(context, DownloadService.class), connection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_new;
    }

    public static NewFragment newInstance() {

        Bundle args = new Bundle();

        NewFragment fragment = new NewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context.unbindService(connection);
    }
}
