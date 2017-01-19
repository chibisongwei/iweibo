package com.willian.weibo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 图片缩放
 */
public class ZoomImageView extends View {
    // 初始化状态常量
    private static final int STATUS_INIT = 1;
    // 图片放大状态常量
    public static final int STATUS_ZOOM_OUT = 2;
    // 图片缩小状态常量
    public static final int STATUS_ZOOM_IN = 3;
    // 图片拖动状态常量
    private static final int STATUS_MOVE = 4;

    // 记录当前操作的状态
    private int currentStatus;

    // 用于对图片进行移动和缩放变换的矩阵
    private Matrix mMatrix = new Matrix();
    // 待展示的Bitmap对象
    private Bitmap sourceBitmap;
    // ZoomImageView控件的宽度
    private int mWidth;
    // ZoomImageView控件的高度
    private int mHeight;
    // 记录上次两指之间的距离
    private double lastFingerDis;
    // 记录上次手指移动时的横坐标
    private float lastMoveX = -1;
    // 记录上次手指移动时的纵坐标
    private float lastMoveY = -1;
    // 记录手指在横坐标方向上的移动距离
    private float movedDistanceX;
    // 记录手指在纵坐标方向上的移动距离
    private float movedDistanceY;
    // 记录图片在矩阵上的横向偏移值
    private float totalTranslateX;
    // 记录图片在矩阵上的纵向偏移值
    private float totalTranslateY;
    // 记录当前图片的宽度，图片被缩放时，这个值会一起变动
    private float currentBitmapWidth;
    // 记录当前图片的高度，图片被缩放时，这个值会一起变动
    private float currentBitmapHeight;
    // 记录两指同时放在屏幕上时，中心点的横坐标值
    private float centerPointX;
    // 记录两指同时放在屏幕上时，中心点的纵坐标值
    private float centerPointY;
    // 记录图片初始化时的缩放比例
    private float initRatio;
    // 记录图片在矩阵上的总缩放比例
    private float totalRatio;
    // 记录手指移动的距离所造成的缩放比例
    private float scaledRatio;

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentStatus = STATUS_INIT;
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.sourceBitmap = bitmap;
        // 调用onDraw()方法
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // 分别获取到ZoomImageView的宽度和高度
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    // 当有两个手指按在屏幕上时，计算两指之间的距离
                    lastFingerDis = distanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    // 只有单指按在屏幕上移动时，为拖动状态
                    float moveX = event.getX();
                    float moveY = event.getY();
                    if (lastMoveX == -1 && lastMoveY == -1) {
                        lastMoveX = moveX;
                        lastMoveY = moveY;
                    }
                    currentStatus = STATUS_MOVE;
                    movedDistanceX = moveX - lastMoveX;
                    movedDistanceY = moveY - lastMoveY;
                    // 进行边界检查，不允许将图片拖出边界
                    if (totalTranslateX + movedDistanceX > 0) {
                        movedDistanceX = 0;
                    } else if (mWidth - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
                        movedDistanceX = 0;
                    }
                    if (totalTranslateY + movedDistanceY > 0) {
                        movedDistanceY = 0;
                    } else if (mHeight - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
                        movedDistanceY = 0;
                    }
                    invalidate();
                    lastMoveX = moveX;
                    lastMoveY = moveY;
                } else if (event.getPointerCount() == 2) {
                    // 有两个手指按在屏幕上移动时，为缩放状态
                    centerPointBetweenFingers(event);
                    double fingerDis = distanceBetweenFingers(event);
                    if (fingerDis > lastFingerDis) {
                        currentStatus = STATUS_ZOOM_OUT;
                    } else {
                        currentStatus = STATUS_ZOOM_IN;
                    }
                    // 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
                    if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 4 * initRatio) || (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
                        scaledRatio = (float) (fingerDis / lastFingerDis);
                        totalRatio = totalRatio * scaledRatio;
                        if (totalRatio > 4 * initRatio) {
                            totalRatio = 4 * initRatio;
                        } else if (totalRatio < initRatio) {
                            totalRatio = initRatio;
                        }
                        invalidate();
                        lastFingerDis = fingerDis;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    // 手指离开屏幕时将临时值还原
                    lastMoveX = -1;
                    lastMoveY = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕时将临时值还原
                lastMoveX = -1;
                lastMoveY = -1;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentStatus) {
            case STATUS_ZOOM_OUT:
            case STATUS_ZOOM_IN:
                zoomBitmap(canvas);
                break;
            case STATUS_MOVE:
                moveBitmap(canvas);
                break;
            case STATUS_INIT:
                initBitmap(canvas);
            default:
                canvas.drawBitmap(sourceBitmap, mMatrix, null);
                break;
        }
    }

    /**
     * 对图片进行平移处理
     *
     * @param canvas
     */
    private void moveBitmap(Canvas canvas) {
        mMatrix.reset();
        // 根据手指移动的距离计算出总偏移值
        float translateX = totalTranslateX + movedDistanceX;
        float translateY = totalTranslateY + movedDistanceY;
        // 先按照已有的缩放比例对图片进行缩放
        mMatrix.postScale(totalRatio, totalRatio);
        // 再根据移动距离进行偏移
        mMatrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        canvas.drawBitmap(sourceBitmap, mMatrix, null);
    }

    /**
     * 对图片进行缩放处理
     *
     * @param canvas
     */
    private void zoomBitmap(Canvas canvas) {
        mMatrix.reset();
        // 将图片按总缩放比例进行缩放
        mMatrix.postScale(totalRatio, totalRatio);
        float scaledWidth = sourceBitmap.getWidth() * totalRatio;
        float scaledHeight = sourceBitmap.getHeight() * totalRatio;
        float translateX = 0f;
        float translateY = 0f;
        // 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
        if (currentBitmapWidth < mWidth) {
            translateX = (mWidth - scaledWidth) / 2f;
        } else {
            translateX = totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
            // 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
            if (translateX > 0) {
                translateX = 0;
            } else if (mWidth - translateX > scaledWidth) {
                translateX = mWidth - scaledWidth;
            }
        }
        // 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
        if (currentBitmapHeight < mHeight) {
            translateY = (mHeight - scaledHeight) / 2f;
        } else {
            translateY = totalTranslateY * scaledRatio + centerPointY * (1 - scaledRatio);
            // 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
            if (translateY > 0) {
                translateY = 0;
            } else if (mHeight - translateY > scaledHeight) {
                translateY = mHeight - scaledHeight;
            }
        }
        // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
        mMatrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        currentBitmapWidth = scaledWidth;
        currentBitmapHeight = scaledHeight;
        canvas.drawBitmap(sourceBitmap, mMatrix, null);
    }

    /**
     * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩
     *
     * @param canvas
     */
    private void initBitmap(Canvas canvas) {
        if (sourceBitmap != null) {
            mMatrix.reset();
            int bitmapWidth = sourceBitmap.getWidth();
            int bitmapHeight = sourceBitmap.getHeight();
            if (bitmapWidth > mWidth || bitmapHeight > mHeight) {
                if (bitmapWidth - mWidth > bitmapHeight - mHeight) {
                    // 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = mWidth / (bitmapWidth * 1.0f);
                    mMatrix.postScale(ratio, ratio);
                    float translateY = (mHeight - (bitmapHeight * ratio)) / 2f;
                    // 在纵坐标方向上进行偏移，以保证图片居中显示
                    mMatrix.postTranslate(0, translateY);
                    totalTranslateY = translateY;
                    totalRatio = initRatio = ratio;
                } else {
                    // 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = mHeight / (bitmapHeight * 1.0f);
                    mMatrix.postScale(ratio, ratio);
                    float translateX = (mWidth - (bitmapWidth * ratio)) / 2f;
                    // 在横坐标方向上进行偏移，以保证图片居中显示
                    mMatrix.postTranslate(translateX, 0);
                    totalTranslateX = translateX;
                    totalRatio = initRatio = ratio;
                }
                currentBitmapWidth = bitmapWidth * initRatio;
                currentBitmapHeight = bitmapHeight * initRatio;
            } else {
                // 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
                float translateX = (mWidth - sourceBitmap.getWidth()) / 2f;
                float translateY = (mHeight - sourceBitmap.getHeight()) / 2f;
                mMatrix.postTranslate(translateX, translateY);
                totalTranslateX = translateX;
                totalTranslateY = translateY;
                totalRatio = initRatio = 1f;
                currentBitmapWidth = bitmapWidth;
                currentBitmapHeight = bitmapHeight;
            }
            canvas.drawBitmap(sourceBitmap, mMatrix, null);
        }
    }

    /**
     * 计算两个手指之间中心点的坐标
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float xPoint1 = event.getX(1);
        float yPoint0 = event.getY(0);
        float yPoint1 = event.getY(1);
        centerPointX = (xPoint0 + xPoint1) / 2;
        centerPointY = (yPoint0 + yPoint1) / 2;
    }

    /**
     * 计算两个手指之间的距离
     *
     * @param event
     * @return
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }
}
