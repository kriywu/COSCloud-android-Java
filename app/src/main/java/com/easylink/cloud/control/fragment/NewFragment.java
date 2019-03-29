package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.easylink.cloud.R;
import com.easylink.cloud.control.ContentProTestActivity;
import com.easylink.cloud.control.test.AActivity;
import com.easylink.cloud.control.test.DownloadActivity;
import com.easylink.cloud.control.test.TestBindService;
import com.easylink.cloud.control.test.TestGlideActivity;
import com.easylink.cloud.control.test.ViewActivity;
import com.easylink.cloud.demos.RemoteViewsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class NewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BIND B";

    private Context context;
    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button btnRemoteViews;

    static int process = 0;
    static TestBindService.MyBinder binder;

    static Handler handler = new MyHandler();

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            process = binder.getProgress();
            Log.d(TAG, process + "");
            handler.sendEmptyMessageDelayed(1, 1000);
        }
    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (TestBindService.MyBinder) service;

            handler.sendEmptyMessageDelayed(1, 1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);
        button = view.findViewById(R.id.btn_jump);
        button.setOnClickListener(this);
        button2 = view.findViewById(R.id.btn_test_permission);
        button2.setOnClickListener(this);
        button3 = view.findViewById(R.id.btn_download);
        button3.setOnClickListener(this);
        button4 = view.findViewById(R.id.btn_View);
        button4.setOnClickListener(this);
        btnRemoteViews = view.findViewById(R.id.btn_remote_view);
        btnRemoteViews.setOnClickListener(this);
        return view;
    }

    private NewFragment(Context context) {
        this.context = context;
    }

    public static NewFragment newInstance(Context context) {

        Bundle args = new Bundle();

        NewFragment fragment = new NewFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_jump) {
            context.startActivity(new Intent(context, TestGlideActivity.class));
        } else if (v.getId() == R.id.btn_test_permission) {
            context.startActivity(new Intent(context, AActivity.class));
        } else if (v.getId() == R.id.btn_download) {
            startActivity(new Intent(context, DownloadActivity.class));
        } else if (v.getId() == R.id.btn_View) {
            startActivity(new Intent(context, ViewActivity.class));
        } else if (v.getId() == R.id.btn_remote_view) {
            startActivity(new Intent(context, RemoteViewsActivity.class));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(context, ContentProTestActivity.class);
                    startActivity(intent);
                }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
