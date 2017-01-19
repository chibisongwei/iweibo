package com.willian.weibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.EventLog;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 自定义ScrollView，解决滑动冲突
 */
public class MyScrollView extends ScrollView {
    // Header布局
    private LinearLayout mHeaderLayout;

    private ListView mListView;

    private VelocityTracker mVelocityTracker;

    // 记录手指按下时的纵坐标
    private int downX;
    // 记录手指按下时的纵坐标
    private int downY;
    // Header是否拦截Touch事件
    private boolean mIsInterceptTouchEventOnHeader = false;
    // 滑动的最小距离
    private int mTouchSlop = 50;

    /**
     * 未使用自定义属性时，调用该构造方法
     *
     * @param context
     * @param attrs
     */
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHeaderLayout(LinearLayout headerLayout) {
        this.mHeaderLayout = headerLayout;
    }

    public void setListView(ListView listView) {
        this.mListView = listView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        createVelocityTracker(event);
        // 是否拦截
        boolean isIntercept = false;
        // 手指的位置
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                // 如果此处拦截，则后续事件无法传递给子View
                isIntercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - downX;
                int deltaY = y - downY;
                // 计算Header的高度
                int headerHeight = mHeaderLayout.getHeight();
                // View默认是消耗事件的
                mIsInterceptTouchEventOnHeader = mHeaderLayout.onTouchEvent(event);
                // 当事件落在Header上，ScrollView不会拦截事件
                if (mIsInterceptTouchEventOnHeader && y <= headerHeight) {
                    isIntercept = false;
                } else if (deltaY < deltaX) { // 竖直距离差小于水平距离差，ScrollView不会拦截事件
                    isIntercept = false;
                } else if (mHeaderLayout.getBottom() > 0 && deltaY <= -mTouchSlop) { // 当Header是展开状态且向上滑动时，ScrollView拦截事件
                    isIntercept = true;
                } else if (isScrollToTop() && deltaY > mTouchSlop) { // 当ListView滑动到了顶部且向下滑动时，ScrollView拦截事件
                    isIntercept = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                break;
        }

        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    /**
     * 创建VelocityTracker对象
     *
     * @param event
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 判断ListView是否滑动到了顶部
     *
     * @return
     */
    private boolean isScrollToTop() {
        if (mListView.getFirstVisiblePosition() == 0) {
            View childView = mListView.getChildAt(0);
            if (childView != null && childView.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }
}
