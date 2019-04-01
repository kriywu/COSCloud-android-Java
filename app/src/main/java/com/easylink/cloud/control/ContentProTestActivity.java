package com.easylink.cloud.control;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.CommonActivity;
import com.easylink.cloud.absolute.iPickPhoto;
import com.easylink.cloud.control.adapter.FilePickAdapter;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.modle.LocalFile;

import java.util.ArrayList;
import java.util.List;

public class ContentProTestActivity extends CommonActivity implements iPickPhoto{

    @BindView(R.id.rv_pick)
    RecyclerView recyclerView;

    private List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPhoto();
        recyclerView.setAdapter(new FilePickAdapter(this,this,list,Constant.EXTRA_PHOTO));
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));

        Handler.Callback callback  = msg -> false;
        new Handler(callback);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_pick;
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
