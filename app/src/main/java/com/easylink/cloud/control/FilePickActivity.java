package com.easylink.cloud.control;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseActivity;
import com.easylink.cloud.absolute.iPickPhoto;
import com.easylink.cloud.absolute.iSetUploadPath;
import com.easylink.cloud.control.adapter.FilePickAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.LocalFile;
import com.easylink.cloud.service.UploadService;
import com.easylink.cloud.util.MediaFileClient;
import com.easylink.cloud.view.PathPopWindow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FilePickActivity extends BaseActivity implements View.OnClickListener, iPickPhoto, iSetUploadPath {
    private static ArrayList<LocalFile> files = new ArrayList<>();
    private Set<LocalFile> pickFile = new HashSet<>();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvPath;    //上传的位置
    private TextView tvSize;
    private String prefix = "";
    private String flag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        flag = getIntent().getStringExtra(Constant.UPLOAD_TYPE); // 上传数据类型

        tvPath = findViewById(R.id.tv_upload_path);
        tvPath.setOnClickListener(this);
        tvSize = findViewById(R.id.tv_size);

        swipeRefreshLayout = findViewById(R.id.srl_flash);
        swipeRefreshLayout.setOnRefreshListener(() -> new QueryDatabase(this, flag).execute());

        recyclerView = findViewById(R.id.rv_pick);
        recyclerView.setAdapter(new FilePickAdapter(this, this, files, flag));

        if (flag.equals(Constant.EXTRA_VIDEO) || flag.equals(Constant.EXTRA_PHOTO))
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        else recyclerView.setLayoutManager(new LinearLayoutManager(this));



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            swipeRefreshLayout.setRefreshing(true); // 进入时候刷新
            new QueryDatabase(this, flag).execute();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pick, menu);
        return true;
    }

    // 设置上传的文件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload:
                Intent intent = new Intent(FilePickActivity.this, UploadService.class);
                intent.putExtra(Constant.EXTRA_PREFIX, prefix);
                intent.putStringArrayListExtra(Constant.EXTRA_PATHS, convert2String(pickFile)); // 设置要上传的文件地址
                startService(intent);// onCreate
                return true;
        }
        return false;
    }

    private ArrayList<String> convert2String(Collection<LocalFile> collection) {
        ArrayList<String> list = new ArrayList<>();
        for (LocalFile file : collection) {
            list.add(file.getPath());
        }
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    new QueryDatabase(this, flag).execute();
                } else {
                    Toast.makeText(FilePickActivity.this, "你没有授权", Toast.LENGTH_LONG).show();
                }
        }
    }

    // 选择上传路径位置
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload_path:
                int width = getWindow().getAttributes().width;
                int height = getWindow().getAttributes().height;
                PathPopWindow popWindow = new PathPopWindow(
                        new PathPopWindow.Builder(FilePickActivity.this).
                                setheight(height).setwidth(width).setContentView(R.layout.popwindow_select_path));
                popWindow.showAtLocation(FilePickActivity.this.findViewById(R.id.activity), Gravity.BOTTOM, 0, 0);

                break;
        }
    }

    static class QueryDatabase extends AsyncTask<Void, Void, Void> {
        WeakReference<FilePickActivity> activity;

        String flag = null;

        QueryDatabase(FilePickActivity activity, String flag) {
            this.activity = new WeakReference<>(activity);
            this.flag = flag;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            if (flag.equals(Constant.EXTRA_PHOTO))
                files.addAll(MediaFileClient.getInstance(activity.get()).getPhotos());
            else if (flag.equals(Constant.EXTRA_VIDEO))
                files.addAll(MediaFileClient.getInstance(activity.get()).getVideo());
            else if (flag.equals(Constant.EXTRA_MUSIC))
                files.addAll(MediaFileClient.getInstance(activity.get()).getMusics());
            else
                files.addAll(MediaFileClient.getInstance(activity.get()).getFilesByType(flag));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (activity.get() != null) activity.get().updateUI();

        }
    }

    private void updateUI() {
        swipeRefreshLayout.setRefreshing(false);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }

    public float sumSize() {
        float sum = 0;
        for (LocalFile file : pickFile) {
            sum += file.getSize();
        }
        sum = sum / (1024 * 1024.0f);
        sum = ((int) sum * 1000) / 1000.0f;
        return sum;
    }


    @Override
    public void pick(LocalFile path) {
        pickFile.add(path);
        tvSize.setText(sumSize() + " MB");
    }

    @Override
    public void unPick(LocalFile path) {
        pickFile.remove(path);
        tvSize.setText(sumSize() + " MB");
    }

    @Override
    public boolean isPick(LocalFile path) {
        return pickFile.contains(path);
    }

    @Override
    public void setPath(String path) {
        prefix = path;
        if (path.equals("")) tvPath.setText("上传到：根目录");
        else tvPath.setText("上传到：" + prefix);
    }
}
