package com.easylink.cloud.control.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easylink.cloud.R;
import com.easylink.cloud.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestGlideActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager = null;
    private List<String> list = new ArrayList<>(1000);
    private Executor executor = Executors.newFixedThreadPool(5);
    private int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private int width = 0;
    private int minSlop;
    private int state;
    private LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        recyclerView = findViewById(R.id.rv_test);
        minSlop = 8 * ViewConfiguration.get(this).getScaledTouchSlop();

        //handler = new Handler();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 0 没有滚动  1用户正在使用 2自然滑动
                if (newState == 0) {
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > minSlop) state = 1; // 慢速滑动
                else state = 0; // 快速滑动
            }
        });

        recyclerView.setAdapter(new RecyclerView.Adapter<IvHolder>() {
            @NonNull
            @Override
            public IvHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(TestGlideActivity.this).inflate(R.layout.view_photo_pick, viewGroup, false);
                return new IvHolder(view);
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
                    else if (state == 0) cacheBitmap(imageView, path, width);
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
        layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);

        checkPermissionForReadStorage();
    }

    public void measureSize(final ImageView imageView) {
        imageView.post(() -> {
                width = imageView.getWidth();
                Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged(); });
    }

    public void cacheBitmap(final ImageView imageView, final String path, final int size) {
        executor.execute(() -> {
            lruCache.put(path, BitmapUtil.decodeBitmapFromFile(path, size, size));
            if (imageView.getTag() == path)
                imageView.post(() -> imageView.setImageBitmap(lruCache.get(path)));
        });
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
            new QueryDBTask().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    new QueryDBTask().execute();
                } else {
                    Toast.makeText(TestGlideActivity.this, "你没有授权", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class IvHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public IvHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            textView = itemView.findViewById(R.id.tv_photo);
        }
    }

    class QueryDBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc"
            );

            int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            while (cursor.moveToNext()) list.add(cursor.getString(index));
            return null;
        }
    }
}
