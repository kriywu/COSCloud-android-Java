package com.easylink.cloud.control.fragment;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.R2;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.absolute.iFlashData;
import com.easylink.cloud.broadcast.UploadProgressReceiver;
import com.easylink.cloud.control.adapter.ProgressAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.FetchTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class DownloadFragment extends BaseFragment implements iFlashData {
    private Context context;
    private List<FetchTask> list = new ArrayList<>(); //上传列表

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private LocalBroadcastManager broadcastManager;

    public static DownloadFragment newInstance(String flag) {

        Bundle args = new Bundle();
        args.putString("FLAG", flag);

        DownloadFragment fragment = new DownloadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        recyclerView.setAdapter(new ProgressAdapter(context, list));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        broadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_UPLOAD_PROGRESS);
        broadcastManager.registerReceiver(UploadProgressReceiver.getReceiver(this, list), intentFilter);

        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.recyclerview_single;
    }

    @Override
    public void flash() {
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }
}
