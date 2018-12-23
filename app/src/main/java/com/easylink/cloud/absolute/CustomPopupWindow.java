package com.easylink.cloud.absolute;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 封装的PopupWindow
 * <p>
 * 作者： 周旭 on 2017/7/10/0010.
 * 邮箱：374952705@qq.com
 * 博客：http://www.jianshu.com/u/56db5d78044d
 */

public class CustomPopupWindow{
    protected PopupWindow mPopupWindow;
    protected View contentView;
    protected static Context mContext;

    public CustomPopupWindow(Builder builder) {
        contentView = LayoutInflater.from(mContext).inflate(builder.contentViewId, null);
        mPopupWindow = new PopupWindow(contentView, builder.width, builder.height);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(builder.animStyle); //设置pop显示的动画效果
    }

    /**
     * popup 消失
     */
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }


    /**
     * 相对于窗体的显示位置
     *
     * @return
     */
    public CustomPopupWindow showAtLocation(View view, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(view, gravity, x, y);
        }
        return this;
    }


    /**
     * 显示在anchor控件的正下方，或者相对这个控件的位置
     */
    public CustomPopupWindow showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xoff, yoff, gravity);
        }
        return this;
    }

    /**
     * 根据id获取view
     */
    public View getItemView(int viewId) {
        if (mPopupWindow != null) {
            return contentView.findViewById(viewId);
        }
        return null;
    }

    /**
     * 根据id设置pop内部的控件的点击事件的监听
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getItemView(viewId);
        view.setOnClickListener(listener);
    }
    /**
     * builder 类
     */
    public static class Builder {
        private int contentViewId; //pop的布局文件
        private int width; //pop的宽度
        private int height;  //pop的高度
        private int animStyle; //动画效果

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setContentView(int contentViewId) {
            this.contentViewId = contentViewId;
            return this;
        }

        public Builder setwidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setheight(int height) {
            this.height = height;
            return this;
        }


        public Builder setAnimationStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        public CustomPopupWindow build() {
            return new CustomPopupWindow(this);
        }
    }
}

