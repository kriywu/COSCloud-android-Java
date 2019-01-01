package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.control.adapter.MultiViewAdapter;
import com.easylink.cloud.control.adapter.ProgressAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.FetchTask;

import java.util.LinkedList;
import java.util.List;

@SuppressLint("ValidFragment")
public class UploadFragment extends Fragment {
    private Context context;
    private RecyclerView rvUploadType;
    //private RecyclerView rvHistory;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private UploadProgressReceiver receiver;
    private LocalBroadcastManager broadcastManager;
    private List<FetchTask> unFiniTask = new LinkedList<>();
    private List<FetchTask> finiTask = new LinkedList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        tabLayout = view.findViewById(R.id.tl_task);
        viewPager = view.findViewById(R.id.vp_task);

        rvUploadType = view.findViewById(R.id.rv_upload_option);
        rvUploadType.setAdapter(new MultiViewAdapter(getContext()));
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvUploadType.setLayoutManager(manager);

//        rvHistory = view.findViewById(R.id.rv_upload_history);
//        rvHistory.setAdapter(new ProgressAdapter(context, unFiniTask));
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        rvHistory.setLayoutManager(layoutManager);
//        rvHistory.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        broadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_UPLOAD_PROGRESS);
        receiver = new UploadProgressReceiver();
        broadcastManager.registerReceiver(receiver, intentFilter);

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setAdapter(new ProgressAdapter(context, unFiniTask));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addView(recyclerView);

        RecyclerView recyclerView2 = new RecyclerView(context);
        recyclerView.setAdapter(new ProgressAdapter(context, unFiniTask));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addView(recyclerView);

        viewPager.addView(recyclerView);
        viewPager.addView(recyclerView2);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return false;
            }
        });

        return view;
    }

    public static UploadFragment newInstance(Context context) {

        Bundle args = new Bundle();

        UploadFragment fragment = new UploadFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    private UploadFragment(Context context) {
        this.context = context;
    }


    class UploadProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            FetchTask task = intent.getParcelableExtra(Constant.EXTRA_FETCH_TASK);

            // 如果列表中存在，更新
            // 如果列表中不存在，添加
            if (unFiniTask.contains(task)) {
                unFiniTask.get(unFiniTask.indexOf(task)).updata(task); // 更新进度和状态
            } else {
                unFiniTask.add(0, task);
            }

            //rvHistory.getAdapter().notifyDataSetChanged();
        }
    }
}
