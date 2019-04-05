package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.absolute.iQueryList;
import com.easylink.cloud.control.FeedbackActivity;
import com.easylink.cloud.modle.CloudFile;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class MeFragment extends BaseFragment implements iQueryList {
    private Context context;
    @BindView(R.id.sc_dayNight)
    SwitchCompat scDayNight;
    @BindView(R.id.tv_feedback)
    TextView tvFeedback;
    @BindView(R.id.tv_download_path)
    TextView tvDownloadPath;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        context = getActivity();
        scDayNight.setOnCheckedChangeListener((buttonView, isChecked) -> {

        });
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String path = sp.getString("DOWNLOAD_PATH", "/storage/emulated/0");

        tvDownloadPath.setText(path);

        return view;
    }

    @OnClick(R.id.tv_feedback)
    public void setTvFeedback() {
        Intent intent = new Intent(context, FeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_download_path)
    public void setTvDownloadPath() {

    }

    @Override
    public void updateList(List<CloudFile> files) {

    }

    @Override
    public void updatePath(String path) {

    }

    public static MeFragment newInstance() {

        Bundle args = new Bundle();

        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_me;
    }

}
