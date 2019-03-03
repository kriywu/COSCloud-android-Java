package com.easylink.cloud.control;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager = null;
    private List<String> list = new ArrayList<>(1000);
    private Context context;
    private Executor executor = Executors.newFixedThreadPool(5);
    private int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private Handler handler;
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
        minSlop = 8*ViewConfiguration.get(this).getScaledTouchSlop();

        context = this;
        handler = new Handler();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 0 没有滚动  1用户正在使用 2自然滑动
                if(newState == 0) {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > minSlop) state = 1; // 慢速滑动
                else state = 0; // 快速滑动
            }
        });

        recyclerView.setAdapter(new RecyclerView.Adapter<IvHolder>() {
            @NonNull
            @Override
            public IvHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(TestActivity.this).inflate(R.layout.view_photo_pick, viewGroup, false);
                return new IvHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull IvHolder ivHolder, int i) {
                final String path = list.get(i);
                final int index = i;
                Bitmap bitmap = lruCache.get(path);

                if (bitmap != null) {
                    ivHolder.imageView.setImageBitmap(bitmap);
                } else {
                    if (state == 0)
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                lruCache.put(path, BitmapUtil.decodeBitmapFromFile(path, 100, 100));
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.getAdapter().notifyItemChanged(index);
                                    }
                                });

                            }
                        });
                }
            }


            @Override
            public void onViewRecycled(@NonNull IvHolder holder) {
                super.onViewRecycled(holder);
                holder.imageView.setImageDrawable(getDrawable(R.drawable.blank));
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
                    Toast.makeText(TestActivity.this, "你没有授权", Toast.LENGTH_LONG).show();
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

    class QueryDBTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc"
            );

            int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

            for (int i = 0; cursor.moveToNext(); i++) {
                final String path = cursor.getString(index);
                list.add(path);
            }
            return null;
        }
    }
}
