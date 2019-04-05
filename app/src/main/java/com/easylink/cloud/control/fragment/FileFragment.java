package com.easylink.cloud.control.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.absolute.iShowDialog;
import com.easylink.cloud.control.adapter.FileViewAdapter;
import com.easylink.cloud.modle.CloudFile;
import com.easylink.cloud.service.DownloadService;
import com.easylink.cloud.web.QueryList;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;

public class FileFragment extends BaseFragment implements iQueryList, iShowDialog {
    @BindView(R.id.rv_files)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flash)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fa_back)
    FloatingActionButton fbBack;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String queryCondition = null;
    private FileViewAdapter adapter;
    private List<CloudFile> files = new ArrayList<>();
    private Stack<String> stack = new Stack<>();

    public DownloadService.MyBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        adapter = new FileViewAdapter(getActivity(), this, (List) files, 0); // 表示
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setRefreshing(true);
        getActivity().bindService(new Intent(getContext(), DownloadService.class), connection, Context.BIND_AUTO_CREATE);


        swipeRefreshLayout.setOnRefreshListener(() ->
                new QueryList.Builder(FileFragment.this).setPrefix(stack.peek()).build().execute()
        );

        updateUI();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.getItem(0).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryCondition = query;
                new QueryList.Builder(FileFragment.this)
                        .setPrefix("")
                        .setDelimiter(null)
                        .build()
                        .execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queryCondition = newText;
                new QueryList.Builder(FileFragment.this)
                        .setPrefix("")
                        .setDelimiter(null)
                        .build()
                        .execute();
                return false;
            }
        });

        searchView.setOnSearchClickListener(v -> {
            new QueryList.Builder(FileFragment.this)
                    .setPrefix("")
                    .setDelimiter(null)
                    .build()
                    .execute();
        });

        searchView.setOnCloseListener(() -> {
            queryCondition = null;
            new QueryList.Builder(FileFragment.this).setPrefix(stack.peek()).build().execute();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;

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
        // 检索
        if (queryCondition != null) {
            for (CloudFile file : fs) {
                if (file.key.contains(queryCondition)) files.add(file);
            }
        } else {
            files.addAll(fs);
        }
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

    @Override
    public void show(DialogFragment dialogFragment, String tag) {
        assert getFragmentManager() != null;
        dialogFragment.show(getFragmentManager(), tag);
    }

    public static FileFragment newInstance() {
        Bundle args = new Bundle();
        FileFragment fragment = new FileFragment();
        fragment.setArguments(args);
        return fragment;
    }

}

