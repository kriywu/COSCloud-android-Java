package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easylink.cloud.R;
import com.jaygoo.widget.RangeSeekBar;

@SuppressLint("ValidFragment")
public class NewFragment extends Fragment {
    private Context context;
    private RangeSeekBar rangeSeekBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new,container,false);
        rangeSeekBar = view.findViewById(R.id.rsb_range);

        return view;
    }
    private NewFragment(Context context){
        this.context = context;
    }

    public static NewFragment newInstance(Context context) {

        Bundle args = new Bundle();

        NewFragment fragment = new NewFragment(context);
        fragment.setArguments(args);
        return fragment;
    }
}
