package com.easylink.cloud.control.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.control.adapter.FileViewAdapter;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.web.Client;
import com.easylink.cloud.web.QueryList;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;

public class FileFragment extends BaseFragment implements iQueryList {

    @BindView(R.id.rv_files)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flash)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fa_back)
    FloatingActionButton fbBack;

    private FileViewAdapter adapter;
    private List<CloudFile> files = new ArrayList<>();
    private Stack<String> stack = new Stack<>();

    public static FileFragment newInstance() {
        Bundle args = new Bundle();
        FileFragment fragment = new FileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stack.push(""); // 根目录
        new QueryList.Builder(FileFragment.this).setPrefix(stack.peek()).build().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        adapter = new FileViewAdapter(getActivity(), this, (List) files);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(() ->
                new QueryList.Builder(FileFragment.this).setPrefix(stack.peek()).build().execute()
        );

        updateUI();
        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_file;
    }

    private void updateUI() {
        if (stack.size() == 1) fbBack.setVisibility(View.INVISIBLE);
        else fbBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateList(List<CloudFile> fs) {
        updateUI();

        files.clear();
        files.addAll(fs);
        adapter.notifyDataSetChanged();

        // 防止网速太快，导致刷新动画太短
        if (swipeRefreshLayout.isRefreshing()) {
            Handler handler = new Handler(msg -> {
                swipeRefreshLayout.setRefreshing(false);
                return true;
            });
            handler.sendEmptyMessageDelayed(1, 500);
        }
    }

    @Override
    public void updatePath(String path) {
        stack.push(path);
    }


    @OnClick(R.id.fa_back)
    void backParentPath() {
        if (stack.size() == 1) return;
        swipeRefreshLayout.setRefreshing(true);
        stack.pop();

        new QueryList.Builder(FileFragment.this)
                .setPrefix(stack.peek())
                .build()
                .execute();
    }
}

