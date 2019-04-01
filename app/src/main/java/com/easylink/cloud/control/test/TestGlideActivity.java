package com.easylink.cloud.control.test;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.CommonActivity;
import com.easylink.cloud.modle.Constant;
import com.easylink.cloud.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;

public class TestGlideActivity extends CommonActivity {

    private static final String TAG = "TestGlideActivity";
    @BindView(R.id.rv_test)
    RecyclerView recyclerView;

    private List<String> list = new ArrayList<>(1000);
    private Executor executor = Executors.newFixedThreadPool(4);
    //private Executor executor = Executors.newCachedThreadPool();
    private int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private int width = 0;
    private int state = 1;
    private LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(@NonNull String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: " + newState);
                // 每次滑动会调用三次
                // 1->2->0
                // 1 滑动
                // 2 自然滑动
                // 0 静止
                if (newState == 0) {
                    state = 0;
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 滑动过程中，会被多次调用，每次TOUCH_EVENT作为间隔
                // 最后几次可能都会小于阈值
                Log.d(TAG, "onScrolled: " + dy);
                state = Math.abs(dy) > 100 ? 1 : 0;
            }
        });

        recyclerView.setAdapter(new RecyclerView.Adapter<IvHolder>() {
            @NonNull
            @Override
            public IvHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new IvHolder(LayoutInflater
                        .from(TestGlideActivity.this)
                        .inflate(R.layout.view_photo_pick, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(final IvHolder ivHolder, int i) {
                final String path = list.get(i);
                final ImageView imageView = ivHolder.imageView;
                imageView.setTag(path);

                if (width == 0) {
                    measureSize(imageView);
                } else {
                    Bitmap bitmap = lruCache.get(path);
                    if (bitmap != null) imageView.setImageBitmap(bitmap);
                    else if (state == 0) cacheBitmap(imageView, i, path, width);
                }
            }

            @Override
            public void onViewRecycled(@NonNull IvHolder holder) {
                super.onViewRecycled(holder);
                holder.imageView.setImageDrawable(getDrawable(R.drawable.blank));
                holder.imageView.postInvalidate();
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        // 动画效果导致的闪烁问题
        ((SimpleItemAnimator)Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        checkPermissionForReadStorage();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_test;
    }

    public void measureSize(final ImageView imageView) {
        imageView.post(() -> {
            width = imageView.getWidth();
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        });
    }

    public void cacheBitmap(final ImageView imageView, int index, final String path, final int size) {
        executor.execute(() -> {
            Bitmap bitmap = BitmapUtil.decodeBitmapFromFile(path, size, size);
            if (path == null || bitmap == null) return;
            lruCache.put(path, bitmap);

            if (imageView.getTag() == path)
                imageView.post(() -> {
                    imageView.setImageBitmap(lruCache.get(path));
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(index);
                });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class IvHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        IvHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            textView = itemView.findViewById(R.id.tv_photo);
        }
    }

    static class QueryDBTask extends AsyncTask<Void, Void, Void> {
        static ContentResolver contentResolver;
        List<String> list = null;

        QueryDBTask(ContentResolver resolver, List<String> list) {
            contentResolver = resolver;
            this.list = list;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc"
            );
            if (cursor == null) return null;

            int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            while (cursor.moveToNext()) list.add(cursor.getString(index));
            cursor.close();

            return null;
        }
    }

    public void checkPermissionForReadStorage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    1);
        } else {
            new QueryDBTask(getContentResolver(), list).execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    new QueryDBTask(getContentResolver(), list).execute();
                } else {
                    Toast.makeText(TestGlideActivity.this, "你没有授权", Toast.LENGTH_LONG).show();
                }
        }
    }

}
