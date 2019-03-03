package com.easylink.cloud.control.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.easylink.cloud.R;
import com.easylink.cloud.control.TestActivity;
import com.jaygoo.widget.RangeSeekBar;

@SuppressLint("ValidFragment")
public class NewFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new,container,false);
        button = view.findViewById(R.id.btn_jump);
        button.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_jump){
            context.startActivity(new Intent(context,TestActivity.class));
        }
    }
}
