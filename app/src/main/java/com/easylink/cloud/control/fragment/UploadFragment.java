package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.easylink.cloud.control.adapter.MultiViewAdapter;

@SuppressLint("ValidFragment")
public class UploadFragment extends Fragment {
    private Context context;
    private RecyclerView rvUploadType;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    private DownloadFragment fragment1;
    private DownloadFragment fragment2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        fragment1 = DownloadFragment.newInstance("0");
        fragment2 = DownloadFragment.newInstance("1");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        tabLayout = view.findViewById(R.id.tl_task);
        viewPager = view.findViewById(R.id.vp_task);

        rvUploadType = view.findViewById(R.id.rv_upload_option);
        rvUploadType.setAdapter(new MultiViewAdapter(getContext()));
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvUploadType.setLayoutManager(manager);


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

    public static UploadFragment newInstance(Context context) {

        Bundle args = new Bundle();

        UploadFragment fragment = new UploadFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    private UploadFragment(Context context) {
        this.context = context;
    }

}
