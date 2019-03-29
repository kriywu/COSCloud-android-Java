package com.easylink.cloud.control.fragment;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.iFlashData;
import com.easylink.cloud.broadcast.UploadProgressReceiver;
import com.easylink.cloud.control.adapter.ProgressAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.FetchTask;

import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment implements iFlashData {
    private Context context;
    private List<FetchTask> list = new ArrayList<>(); //上传列表
    private RecyclerView recyclerView;
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

        final View view = inflater.inflate(R.layout.recyclerview_single, container, false);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setAdapter(new ProgressAdapter(context, list));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        broadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_UPLOAD_PROGRESS);
        broadcastManager.registerReceiver(UploadProgressReceiver.getReceiver(this, list), intentFilter);

        return view;
    }

    @Override
    public void flash() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
