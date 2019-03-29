package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.control.adapter.FileViewAdapter;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.web.QueryList;
import com.easylink.cloud.modle.Constant;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@SuppressLint("ValidFragment")
public class FileFragment extends Fragment implements iQueryList, View.OnClickListener {
    private Context context;
    private RecyclerView recyclerView;
    private FileViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fbBack;

    private List<CloudFile> files = new ArrayList<>();
    private Stack<String> stack = new Stack<>();

    private FileFragment(Context context) {
        this.context = context;
    }

    public static FileFragment newInstance(Context context) {

        Bundle args = new Bundle();
        FileFragment fragment = new FileFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stack.push(""); // 根目录
        new QueryList.Builder(context, FileFragment.this).setBucket(Constant.bucket).setPrefix(stack.peek()).setDelimiter('/').build().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);

        fbBack = view.findViewById(R.id.fa_back);
        fbBack.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.rv_files);
        adapter = new FileViewAdapter(context, this, (List) files);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        swipeRefreshLayout = view.findViewById(R.id.srl_flash);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new QueryList.Builder(context, FileFragment.this).setBucket(Constant.bucket).setPrefix(stack.peek()).setDelimiter('/').build().execute();
            }
        });

        updateUI();
        return view;
    }

    void updateUI() {
        if (stack.size() == 1) {
            fbBack.setVisibility(View.INVISIBLE);
        } else {
            fbBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateList(List<CloudFile> fs) {
        updateUI();

        files.clear();
        files.addAll(fs);
        adapter.notifyDataSetChanged();

        // 防止网速太快，导致刷新动画太短
        if (swipeRefreshLayout.isRefreshing()) {
            Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    swipeRefreshLayout.setRefreshing(false);
                    return true;
                }
            });
            handler.sendEmptyMessageDelayed(1, 500);
        }
    }

    @Override
    public void updatePath(String path) {
        Log.d("file fragment", path + "入栈");
        stack.push(path);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fa_back:
                if (stack.size() == 1) return;

                swipeRefreshLayout.setRefreshing(true);
                stack.pop();

                new QueryList.Builder(context, FileFragment.this).setBucket(Constant.bucket).setPrefix(stack.peek()).setDelimiter('/').build().execute();
                break;
        }
    }
}

