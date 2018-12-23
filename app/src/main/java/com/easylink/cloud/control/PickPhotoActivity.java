package com.easylink.cloud.control;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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
import com.easylink.cloud.control.adapter.PhotoAdapter;
import com.easylink.cloud.modle.LocalFile;
import com.easylink.cloud.service.UploadService;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.absolute.CustomPopupWindow;
import com.easylink.cloud.view.PathPopWindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PickPhotoActivity extends BaseActivity implements View.OnClickListener, iPickPhoto, iSetUploadPath {
    private ArrayList<LocalFile> files = new ArrayList<>();
    private Set<LocalFile> picked = new HashSet<>();

    private RecyclerView recyclerView;
    private RecyclerView rvPath;
    private TextView tvPath;    //上传的位置
    private TextView tvNum;
    private String prefix = "";

    private String flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photo);
        flag = getIntent().getStringExtra(Constant.UPLOAD_TYPE);

        tvPath = findViewById(R.id.tv_upload_path);
        tvPath.setOnClickListener(this);
        tvNum = findViewById(R.id.tv_num);

        recyclerView = findViewById(R.id.rv_photo);
        recyclerView.setAdapter(new PhotoAdapter(this, this, files));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            if(flag.equals(Constant.EXTRA_PHOTO))
                files.addAll(getPhotos());
            else
                files.addAll(getVideo());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pick_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload:
                Intent intent = new Intent(PickPhotoActivity.this, UploadService.class);
                intent.putExtra(Constant.EXTRA_PREFIX, prefix);
                intent.putStringArrayListExtra(Constant.EXTRA_PATHS, convert2String(picked)); // 设置要上传的文件地址
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
                    Toast.makeText(PickPhotoActivity.this, "你没有授权", Toast.LENGTH_LONG).show();
                }
        }
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
            photos.add(new LocalFile(path));
        }
        return photos;
    }

    public List<LocalFile> getVideo() {
        List<LocalFile> videos = new ArrayList<>();
        String[] mediaColumns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns,
                null,
                null,
                MediaStore.Video.Media.DATE_MODIFIED);

        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            videos.add(new LocalFile(path));
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        return videos;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_upload_path:
                int width = getWindow().getAttributes().width;
                int height = getWindow().getAttributes().height;
                PathPopWindow popWindow = new PathPopWindow(
                        new PathPopWindow.Builder(PickPhotoActivity.this).
                                setheight(height).setwidth(width).setContentView(R.layout.popwindow_select_path));
                popWindow.showAtLocation(PickPhotoActivity.this.findViewById(R.id.activity), Gravity.BOTTOM, 0, 0);

                break;
        }
    }

    @Override
    public void pick(LocalFile path) {
        picked.add(path);
        Log.d("picked", picked.size() + "");
        tvNum.setText(picked.size() + "");
    }

    @Override
    public void unPick(LocalFile path) {
        picked.remove(path);
        tvNum.setText(picked.size() + "");
    }

    @Override
    public boolean isPick(LocalFile path) {
        return picked.contains(path);
    }

    @Override
    public void setPath(String path) {
        prefix = path;
        tvPath.setText("上传到：" + prefix);
    }
}
