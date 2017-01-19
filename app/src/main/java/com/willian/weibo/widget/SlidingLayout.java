package com.willian.weibo.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

/**
 * Created by willian on 2016/5/12.
 */
public class SlidingLayout extends LinearLayout implements View.OnTouchListener {

    // 滚动显示和隐藏左侧布局时，手指滑动需要达到的速度
    public static final int SNAP_VELOCITY = 200;
    // 左侧布局完全显示时，留给右侧布局的宽度值
    private int leftLayoutPadding = 80;
    // 记录手指按下时的横坐标
    private float downX;
    // 记录手指移动时的横坐标
    private float moveX;
    // 记录手指抬起时的横坐标
    private float upX;
    // 左侧布局最多可以滑动到的左边缘
    private int leftEdge;
    // 左侧布局最多可以滑动到的右边缘
    private int rightEdge = 0;
    // 屏幕宽度
    private int screenWidth;
    // 左侧布局
    private View leftLayout;
    // 右侧布局
    private View rightLayout;
    // 左侧布局的参数，通过此参数来重新确定左侧布局的宽度，以及更改leftMargin的值
    private MarginLayoutParams leftLayoutParams;
    // 右侧布局的参数，通过此参数来重新确定右侧布局的宽度
    private MarginLayoutParams rightLayoutParams;
    // 手指滑动速度
    private VelocityTracker mVelocityTracker;

    private boolean isLeftLayoutVisible;
    // 用于监听侧滑事件的View
    private View mBindView;

    public SlidingLayout(Context context) {
        super(context);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 左侧布局是否完全显示出来，或完全隐藏，滑动过程中此值无效
     *
     * @return 左侧布局完全显示返回true，完全隐藏返回false
     */
    public boolean isLeftLayoutVisible() {
        return isLeftLayoutVisible;
    }


    /**
     * 绑定监听侧滑事件的View，即在绑定的View进行滑动才可以显示和隐藏左侧布局
     *
     * @param bindView
     */
    public void setScrollEvent(View bindView) {
        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    /**
     * 将屏幕滚动到左侧布局界面，滚动速度设定为30
     */
    public void scrollToLeftLayout() {
        new ScrollTask().execute(30);
    }

    /**
     * 将屏幕滚动到右侧布局界面，滚动速度设定为-30
     */
    public void scrollToRightLayout() {
        new ScrollTask().execute(-30);
    }

    /**
     * 在onLayout中重新设定左侧布局和右侧布局的参数
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // 获取左侧布局
            leftLayout = getChildAt(0);
            leftLayoutParams = (MarginLayoutParams) leftLayout.getLayoutParams();
            // 重置左侧布局对象的宽度
            leftLayoutParams.width = screenWidth - leftLayoutPadding;
            // 设置最左边距为负的左侧布局的宽度
            leftEdge = -leftLayoutParams.width;
            leftLayoutParams.leftMargin = leftEdge;
            leftLayout.setLayoutParams(leftLayoutParams);
            // 获取右侧布局对象
            rightLayout = getChildAt(1);
            rightLayoutParams = (MarginLayoutParams) rightLayout.getLayoutParams();
            rightLayoutParams.width = screenWidth;
            rightLayout.setLayoutParams(rightLayoutParams);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getRawX();
                // 计算手指滑动的距离
                int distanceX = (int) (moveX - downX);
                if (isLeftLayoutVisible) {
                    leftLayoutParams.leftMargin = distanceX;
                } else {
                    leftLayoutParams.leftMargin = leftEdge + distanceX;
                }
                if (leftLayoutParams.leftMargin < leftEdge) {
                    leftLayoutParams.leftMargin = leftEdge;
                } else if (leftLayoutParams.leftMargin > rightEdge) {
                    leftLayoutParams.leftMargin = rightEdge;
                }
                leftLayout.setLayoutParams(leftLayoutParams);
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getRawX();
                if (wantToShowLeftLayout()) {
                    if (shouldScrollToLeftLayout()) {
                        scrollToLeftLayout();
                    } else {
                        scrollToRightLayout();
                    }
                } else if (wantToShowRightLayout()) {
                    if (shouldScrollToContent()) {
                        scrollToRightLayout();
                    } else {
                        scrollToLeftLayout();
                    }
                }
                recycleVelocityTracker();
                break;
        }
        return isBindBasicLayout();
    }

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中
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
     * 回收VelocityTracker对象
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 如果手指移动的距离是正数，且当前左侧布局是不可见的，则认为当前手势是想要显示左侧布局
     *
     * @return 当前手势想显示左侧布局返回true，否则返回false
     */
    private boolean wantToShowLeftLayout() {
        return upX - downX > 0 && !isLeftLayoutVisible;
    }

    /**
     * 如果手指移动的距离是负数，且当前左侧布局是可见的，则认为当前手势是想要显示右侧布局
     *
     * @return 当前手势想显示右侧布局返回true，否则返回false
     */
    private boolean wantToShowRightLayout() {
        return upX - downX < 0 && isLeftLayoutVisible;
    }

    /**
     * 判断是否应该滚动将左侧布局展示出来
     * 如果手指移动距离大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY
     *
     * @return
     */
    private boolean shouldScrollToLeftLayout() {
        return upX - downX > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将右侧布局展示出来
     * 如果手指移动距离加上leftLayoutPadding大于屏幕的1/2，或者手指移动速度大于SNAP_VELOCITY
     *
     * @return
     */
    private boolean shouldScrollToContent() {
        return downX - upX + leftLayoutPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 判断绑定滑动事件的View是不是一个基础layout，不支持自定义layout，只支持四种基本layout
     *
     * @return
     */
    private boolean isBindBasicLayout() {
        if (mBindView == null) {
            return false;
        }
        String viewName = mBindView.getClass().getName();
        return viewName.equals(LinearLayout.class.getName()) ||
                viewName.equals(RelativeLayout.class.getName()) ||
                viewName.equals(FrameLayout.class.getName()) ||
                viewName.equals(TableLayout.class.getName());
    }

    private class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = leftLayoutParams.leftMargin;
            // 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin > rightEdge) {
                    leftMargin = rightEdge;
                    break;
                }
                if (leftMargin < leftEdge) {
                    leftMargin = leftEdge;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (speed[0] > 0) {
                isLeftLayoutVisible = true;
            } else {
                isLeftLayoutVisible = false;
            }

            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            leftLayoutParams.leftMargin = leftMargin[0];
            leftLayout.setLayoutParams(leftLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            leftLayoutParams.leftMargin = leftMargin;
            leftLayout.setLayoutParams(leftLayoutParams);
        }
    }
}
