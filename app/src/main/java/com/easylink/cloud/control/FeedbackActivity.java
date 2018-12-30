package com.easylink.cloud.control;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easylink.cloud.R;
import com.easylink.cloud.absolute.BaseActivity;

public class FeedbackActivity extends BaseActivity {
    private Button btnAck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        btnAck = findViewById(R.id.btn_ack);
        btnAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(btnAck, "拒绝了您的反馈，手动滑稽", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("当然是原谅我了", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
            }
        });
    }
}
