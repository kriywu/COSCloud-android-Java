package com.easylink.cloud.control;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.CommonActivity;
import com.easylink.cloud.absolute.iPickPhoto;
import com.easylink.cloud.absolute.iSetUploadPath;
import com.easylink.cloud.control.adapter.FilePickAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.LocalFile;
import com.easylink.cloud.service.UploadBindService;
import com.easylink.cloud.util.MediaFileClient;
import com.easylink.cloud.view.PathPopWindow;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
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
import butterknife.BindView;
import butterknife.OnClick;

public class FilePickActivity extends CommonActivity implements iPickPhoto, iSetUploadPath {
    private ArrayList<LocalFile> files = new ArrayList<>();
    private Set<LocalFile> pickFile = new HashSet<>();

    @BindView(R.id.rv_pick)
    RecyclerView recyclerView;
    @BindView(R.id.srl_flash)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_size)
    TextView tvSize;

    private String prefix = "";
    private String flag;
    private UploadBindService.MyBinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UploadBindService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(FilePickActivity.this, UploadBindService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        flag = getIntent().getStringExtra(Constant.UPLOAD_TYPE); // 上传数据类型

        swipeRefreshLayout.setOnRefreshListener(() -> new QueryDatabase(this, flag).execute());
        recyclerView.setAdapter(new FilePickAdapter(this, this, files, flag));

        if (flag.equals(Constant.EXTRA_VIDEO) || flag.equals(Constant.EXTRA_PHOTO))
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        else
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            swipeRefreshLayout.setRefreshing(true); // 进入时候刷新
            new QueryDatabase(this, flag).execute();
        }

    }

    @OnClick(R.id.tv_path)
    void setTvPath() {
        int width = getWindow().getAttributes().width;
        int height = getWindow().getAttributes().height;
        PathPopWindow popWindow = new PathPopWindow(
                new PathPopWindow.Builder(FilePickActivity.this).
                        setheight(height).setwidth(width).setContentView(R.layout.popwindow_select_path));
        popWindow.showAtLocation(FilePickActivity.this.findViewById(R.id.activity), Gravity.BOTTOM, 0, 0);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_pick;
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
                for (LocalFile file : pickFile) {
                    String key = prefix + file.path.substring(file.path.lastIndexOf(File.separator));
                    binder.addTask(key, file.path);
                }
                Snackbar.make(recyclerView, "开始上传", BaseTransientBottomBar.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    private ArrayList<String> convert2String(Collection<LocalFile> collection) {
        ArrayList<String> list = new ArrayList<>();
        for (LocalFile file : collection) {
            list.add(file.path);
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

    class QueryDatabase extends AsyncTask<Void, Void, Void> {
        WeakReference<FilePickActivity> activity;

        String flag;

        QueryDatabase(FilePickActivity activity, String flag) {
            this.activity = new WeakReference<>(activity);
            this.flag = flag;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            switch (flag) {
                case Constant.EXTRA_PHOTO:
                    files.addAll(MediaFileClient.getInstance(activity.get()).getPhotos());
                    break;
                case Constant.EXTRA_VIDEO:
                    files.addAll(MediaFileClient.getInstance(activity.get()).getVideo());
                    break;
                case Constant.EXTRA_MUSIC:
                    files.addAll(MediaFileClient.getInstance(activity.get()).getMusics());
                    break;
                default:
                    files.addAll(MediaFileClient.getInstance(activity.get()).getFilesByType(flag));
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

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
            sum += file.size;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
