package com.easylink.cloud.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.control.FilePickActivity;
import com.easylink.cloud.modle.Constant;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

public class UploadFragment extends BaseFragment {
    private BaseFragment fragment1;
    private BaseFragment fragment2;

    @BindView(R.id.vp_task)
    ViewPager viewPager;
    @BindView(R.id.tl_task)
    TabLayout tabLayout;

    @BindView(R.id.iv_photo)
    ImageView iv_photo;
    @BindView(R.id.iv_video)
    ImageView iv_video;
    @BindView(R.id.iv_music)
    ImageView iv_music;
    @BindView(R.id.iv_file)
    ImageView iv_file;
    @BindView(R.id.iv_rar)
    ImageView iv_rar;
    @BindView(R.id.iv_apk)
    ImageView iv_apk;

    public static UploadFragment newInstance() {

        Bundle args = new Bundle();

        UploadFragment fragment = new UploadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment1 = OnGoingFragment.newInstance();
        fragment2 = HistoryFragment.newInstance();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return (i == 0 ? fragment1 : fragment2);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return (position == 0 ? "正在进行" : "历史记录");
            }
        });
        tabLayout.setupWithViewPager(viewPager, false);

        return view;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_upload;
    }


    @OnClick({R.id.iv_photo, R.id.iv_video, R.id.iv_music, R.id.iv_file, R.id.iv_rar, R.id.iv_apk})
    void onViewsClick(View v) {
        Intent intent = new Intent(getActivity(), FilePickActivity.class);
        switch (v.getId()) {
            case R.id.iv_photo:
                intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_PHOTO);
                break;
            case R.id.iv_video:
                intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_VIDEO);
                break;
            case R.id.iv_music:
                intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_MUSIC);
                break;
            case R.id.iv_file:
                intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_DOC);
                break;
            case R.id.iv_rar:
                intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_RAR);
                break;
            case R.id.iv_apk:
                intent.putExtra(Constant.UPLOAD_TYPE, Constant.EXTRA_APK);
                break;
        }
        if (getActivity() != null) startActivity(intent);
    }
}
