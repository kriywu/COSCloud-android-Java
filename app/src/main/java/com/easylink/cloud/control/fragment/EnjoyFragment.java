package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseFragment;
import com.easylink.cloud.test.TestGlideActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("ValidFragment")
public class EnjoyFragment extends BaseFragment {
    private Context context;
    @BindView(R.id.btn_test_glide)
    Button btnGlide;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_enjoy;
    }

    @OnClick(R.id.btn_test_glide)
    void setBtnGlide() {
        Intent intent = new Intent(getActivity(), TestGlideActivity.class);
        startActivity(intent);
    }

    public static EnjoyFragment newInstance() {

        Bundle args = new Bundle();

        EnjoyFragment fragment = new EnjoyFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
