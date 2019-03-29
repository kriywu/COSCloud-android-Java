package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.easylink.cloud.R;
import com.easylink.cloud.control.FeedbackActivity;

@SuppressLint("ValidFragment")
public class MeFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private SwitchCompat scDayNight;
    private TextView tvShare;
    private TextView tvFeedback;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        tvFeedback = view.findViewById(R.id.tv_feedback);
        tvFeedback.setOnClickListener(this);

        scDayNight = view.findViewById(R.id.sc_dayNight);
        scDayNight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        return view;
    }

    private MeFragment(Context context) {
        this.context = context;
    }

    public static MeFragment newInstance(Context context) {

        Bundle args = new Bundle();

        MeFragment fragment = new MeFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_feedback:
                Intent intent = new Intent(context,FeedbackActivity.class);
                startActivity(intent);
                break;
        }
    }
}
