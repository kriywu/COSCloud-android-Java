package com.easylink.cloud.control;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.iPickPhoto;
import com.easylink.cloud.control.adapter.FilePickAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.LocalFile;

import java.util.ArrayList;
import java.util.List;

public class ContentProTestActivity extends AppCompatActivity implements iPickPhoto{
    private List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);
        readPhoto();
        RecyclerView recyclerView = findViewById(R.id.rv_pick);
        recyclerView.setAdapter(new FilePickAdapter(this,this,list,Constant.EXTRA_PHOTO));
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));

        Handler.Callback callback  = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return false;
            }
        };
        new Handler(callback);

    }
    // 使用ContentValue插入或者删除数据
    // URI是表的全称
    // * 表示任意长度字符
    // # 匹配任意长度数字
    private void readPhoto(){
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media.DATE_MODIFIED);

        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            list.add(cursor.getString(index));
        }
        cursor.close();
    }

    @Override
    public void pick(LocalFile path) {

    }

    @Override
    public void unPick(LocalFile path) {

    }

    @Override
    public boolean isPick(LocalFile path) {
        return false;
    }
}
