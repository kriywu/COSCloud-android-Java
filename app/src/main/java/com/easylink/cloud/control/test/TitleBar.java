package com.easylink.cloud.control.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylink.cloud.R;

/**
 * 自定义组合控件
 */

public class TitleBar extends LinearLayout {
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvTitle;
    private LinearLayout layout;
    private int bgColor;
    private int textColor;
    private String text;

    public TitleBar(Context context) {
        super(context);
        initView(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet set) {
        if(set != null){
            // init color
            TypedArray array = context.obtainStyledAttributes(set, R.styleable.TitleBar);
            bgColor = array.getColor(R.styleable.TitleBar_title_bg, Color.BLUE);
            textColor = array.getColor(R.styleable.TitleBar_title_text_color, Color.WHITE);
            text = array.getString(R.styleable.TitleBar_title_text);
            array.recycle();
        }

        // init view
        View view = LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, true);
        ivLeft = view.findViewById(R.id.iv_left);
        ivRight = view.findViewById(R.id.iv_more);
        tvTitle = view.findViewById(R.id.tv_title);
        layout = findViewById(R.id.ll_title);


    }

    public void setTitle(String titleName) {
        if (titleName != null) tvTitle.setText(titleName);
    }

    public void setLeftListener(OnClickListener listener) {
        ivLeft.setOnClickListener(listener);
    }

    public void setRightListener(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
    }
}
