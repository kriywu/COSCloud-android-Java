package com.easylink.cloud.control.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.control.adapter.FileViewAdapter;
import com.easylink.cloud.modle.CloudFile;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;

public class FileFragment extends BaseFragment implements iQueryList {
    private static final String TAG = "FileFragment";
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("FileFragment", "fragment create");
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
            Log.d(TAG, "setOnSearchClickListener: ");
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

        if (queryCondition != null) {
            for (CloudFile file : fs) {
                if (file.getKey().contains(queryCondition)) files.add(file);
            }
            Log.d(TAG, "updatelist");
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
}

