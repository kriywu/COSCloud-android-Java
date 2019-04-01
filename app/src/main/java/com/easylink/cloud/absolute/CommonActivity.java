package com.easylink.cloud.absolute;

import android.os.Bundle;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;

public abstract class CommonActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
    }

    protected abstract int getLayout();
}
