package com.easylink.cloud.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class SwipView extends LinearLayout {
    int lastY = 0;

    public SwipView(Context context) {
        super(context);
    }

    public SwipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
        }
        return super.onInterceptTouchEvent(ev);
    }
}
