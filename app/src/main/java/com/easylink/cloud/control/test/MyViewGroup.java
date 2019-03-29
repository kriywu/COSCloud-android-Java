package com.easylink.cloud.control.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyViewGroup extends ViewGroup {
    private Scroller scroller;
    private VelocityTracker tracker;
    int currentIndex = 0;
    int childWidth = 0;
    int lastX = 0;
    int lastY = 0;
    int lastInterceptX = 0;
    int lastInterceptY = 0;


    public MyViewGroup(Context context) {
        super(context);
        init();
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        scroller = new Scroller(getContext());
        tracker = VelocityTracker.obtain();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // 测量子元素
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() == 0) setMeasuredDimension(0, 0);
        else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            View childOne = getChildAt(0);
            int childWidth = childOne.getMeasuredWidth();
            int childHeight = childOne.getMeasuredHeight();
            setMeasuredDimension(childWidth * getChildCount(), childHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            int childWidth = getChildAt(0).getMeasuredWidth();
            setMeasuredDimension(childWidth * getChildCount(), height); // 宽是wrap content
        } else if (heightMode == MeasureSpec.AT_MOST) {
            int childHeight = getChildAt(0).getMeasuredHeight();// 高是wrap content
            setMeasuredDimension(width, childHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount(); // 一个数组
        int left = 0;
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                childWidth = width;
                child.layout(left, 0, left + width, child.getMeasuredHeight());
                left += width;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    /**
     * @param event 点击事件
     * @return 如果x方向大于y方向就拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d("hhhhhhh", "2222");
        boolean intercept = false;
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                if (!scroller.isFinished()) scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - lastInterceptX;
                int dy = y - lastInterceptY;
                intercept = Math.abs(dx) - Math.abs(dy) > 0;
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        lastX = x;
        lastY = y;
        lastInterceptX = x;
        lastInterceptY = y;
        return intercept;
    }

    /**
     * @param event
     * @return move 的时候滑动
     * up 弹性滑动
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        tracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) scroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - lastX;
                scrollBy(-dx, 0);
                break;
            case MotionEvent.ACTION_UP:
                int distance = getScrollX() - currentIndex * childWidth;// 判断滑动的宽度是否大于1/2，如果是切换到其他界面
                if (Math.abs(distance) > childWidth / 2) {
                    if (distance > 0) currentIndex++;
                    else currentIndex--;
                } else {
                    tracker.computeCurrentVelocity(1000);
                    float vx = tracker.getXVelocity();
                    if (Math.abs(vx) > 50) {
                        if (vx > 0) currentIndex--;
                        else currentIndex++;
                    }
                }

                currentIndex = currentIndex < 0 ? 0 : (currentIndex > getChildCount() - 1 ? getChildCount() - 1 : currentIndex);
                smoothScrollTo(currentIndex * childWidth, 0);
                tracker.clear();
                break;
        }
        lastX = x;
        lastY = y;
        return true;
    }

    public void smoothScrollTo(int dx, int dy) {
        scroller.startScroll(getScrollX(), getScrollY(), dx - getScrollX(), dy - getScrollY(), 1000);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }
}
