package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class EnjoyFragment extends Fragment {
    private Context context;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(getClass().getName());

        return textView;
    }
    private EnjoyFragment(Context context){
        this.context = context;
    }

    public static EnjoyFragment newInstance(Context context) {

        Bundle args = new Bundle();

        EnjoyFragment fragment = new EnjoyFragment(context);
        fragment.setArguments(args);
        return fragment;
    }
}
