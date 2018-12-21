package com.easylink.cloud.control;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.easylink.cloud.MainActivity;
import com.easylink.cloud.R;
import com.easylink.cloud.absolute.FetchListCallBack;
import com.easylink.cloud.adapter.FileViewAdapter;
import com.easylink.cloud.modle.EFile;
import com.easylink.cloud.network.FetchList;
import com.easylink.cloud.util.Constant;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class FileFragment extends Fragment implements FetchListCallBack {
    private RecyclerView recyclerView;
    private FileViewAdapter adapter;
    private Context context;
    private List<EFile> files = new ArrayList<>();
    private Toolbar bar;
    private EditText etSearch;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        recyclerView = view.findViewById(R.id.rv_files);
        adapter = new FileViewAdapter(context, this, files);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        new FetchList(context, this).execute();

        initBar();
        return view;
    }


    @Override
    public void update(List<EFile> files) {
        this.files.clear();
        this.files.addAll(files);
        adapter.notifyDataSetChanged();
    }
    public void initBar(){

    }
}

