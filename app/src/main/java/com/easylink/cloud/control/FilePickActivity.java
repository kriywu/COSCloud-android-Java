package com.easylink.cloud.control;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.easylink.cloud.modle.LocalFile;
import com.easylink.cloud.modle.Music;
import com.easylink.cloud.service.UploadService;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.util.FileUtils;
import com.easylink.cloud.view.PathPopWindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilePickActivity extends BaseActivity implements View.OnClickListener, iPickPhoto, iSetUploadPath {
    private ArrayList<LocalFile> files = new ArrayList<>();
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
        swipeRefreshLayout.setRefreshing(true);

        recyclerView = findViewById(R.id.rv_pick);
        recyclerView.setAdapter(new FilePickAdapter(this, this, files, flag));

        if (flag.equals(Constant.EXTRA_VIDEO) || flag.equals(Constant.EXTRA_PHOTO)) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            if (flag.equals(Constant.EXTRA_PHOTO))
                files.addAll(getPhotos());
            else if (flag.equals(Constant.EXTRA_VIDEO))
                files.addAll(getVideo());
            else if (flag.equals(Constant.EXTRA_MUSIC))
                files.addAll(getMusics());
            else
                files.addAll(getFilesByType(flag));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pick, menu);
        return true;
    }

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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getPhotos();
                } else {
                    Toast.makeText(FilePickActivity.this, "你没有授权", Toast.LENGTH_LONG).show();
                }
        }
    }


    private List<Music> getMusics() {
        List<Music> musics = new ArrayList<>();
        Cursor c = null;

        c = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        while (c.moveToNext()) {
            String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径
            String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
            //String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
            String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
            float size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
            //int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
            //int time = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
            musics.add(new Music(name, path, artist, size));
        }

        c.close();
        return musics;
    }

    private List<LocalFile> getPhotos() {
        List<LocalFile> photos = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc ");

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            float size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));// 大小
            photos.add(new LocalFile(path, size));
        }
        cursor.close();
        return photos;
    }

    public List<LocalFile> getVideo() {
        List<LocalFile> videos = new ArrayList<>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Video.Media.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            float size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
            videos.add(new LocalFile(path, size));
        }
        cursor.close();
        return videos;
    }

    public List<LocalFile> getFilesByType(String type) {
        List<LocalFile> files = new ArrayList<LocalFile>();
        // 扫描files文件库
        Cursor c = getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size"}, null, null, null);
        int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

        while (c.moveToNext()) {
            String path = c.getString(dataindex);
            if (FileUtils.getFileType(path).equals(type)) {
                long size = c.getLong(sizeindex);
                files.add(new LocalFile(path, size));
            }
        }

        return files;
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
        tvPath.setText("上传到：" + prefix);
    }
}
