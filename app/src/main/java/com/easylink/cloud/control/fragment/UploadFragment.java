package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.control.adapter.MultiViewAdapter;
import com.easylink.cloud.modle.Constant;

@SuppressLint("ValidFragment")
public class UploadFragment extends Fragment {
    private Context context;
    private RecyclerView rvUploadType;
    private RecyclerView rvHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        rvUploadType = view.findViewById(R.id.rv_upload_option);
        rvUploadType.setAdapter(new MultiViewAdapter(getContext()));
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvUploadType.setLayoutManager(manager);

        rvHistory = view.findViewById(R.id.rv_upload_history);

        return view;
    }

    public static UploadFragment newInstance(Context context) {

        Bundle args = new Bundle();

        UploadFragment fragment = new UploadFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    private UploadFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.IMAGE_REQUEST_CODE:
                break;
            case Constant.VIDEO_REQUEST_CODE:
                break;
        }
    }

}
