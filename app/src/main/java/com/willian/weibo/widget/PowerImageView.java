package com.willian.weibo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.willian.weibo.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * 既能支持ImageView控件原生的所有功能，同时还可以播放GIF图片
 */
public class PowerImageView extends ImageView implements View.OnClickListener {

    // 播放GIF动画的关键类
    private Movie mMovie;

    // 是否允许自动播放
    private boolean isAutoPlay;

    // 图片是否正在播放
    private boolean isPlaying;

    // GIF图片的宽度
    private int mImageWidth;

    // GIF图片的高度
    private int mImageHeight;

    private long startTime;

    public PowerImageView(Context context) {
        super(context);
    }

    public PowerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PowerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PowerImageView);
        int resId = getResourceId(mTypedArray);
        if (resId != 0) {
            InputStream is = getResources().openRawResource(resId);
            // 使用Movie类对流进行解码
            mMovie = Movie.decodeStream(is);
            if (mMovie != null) {
                // 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
                isAutoPlay = mTypedArray.getBoolean(R.styleable.PowerImageView_auto_play, false);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                bitmap.recycle();
                if (!isAutoPlay) {
                    // 点击图片，开始播放GIF动画
                    setOnClickListener(this);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == getId()) {
            isPlaying = true;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMovie != null) {
            // 如果是GIF图片则重新设定PowerImageView的大小
            setMeasuredDimension(mImageWidth, mImageHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie == null) {
            // mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法
            super.onDraw(canvas);
        } else {
            // mMovie不等于null，说明是张GIF图片
            if (isAutoPlay) {
                // 如果允许自动播放，就调用playMovie()方法播放GIF动画
                playMovie(canvas);
                invalidate();
            } else {
                // 不允许自动播放时，判断当前图片是否正在播放
                if (isPlaying) {
                    // 正在播放就继续调用playMovie()方法，一直到动画播放结束为止
                    if (playMovie(canvas)) {
                        isPlaying = false;
                    }
                    invalidate();
                } else {
                    // 还没开始播放就只绘制GIF图片的第一帧
                    mMovie.setTime(0);
                    mMovie.draw(canvas, 0, 0);
                }
            }

        }

    }

    /**
     * 开始播放GIF动画，播放完成返回true，未完成返回false
     *
     * @param mCanvas
     * @return
     */
    public boolean playMovie(Canvas mCanvas) {
        long currentTime = SystemClock.uptimeMillis();
        if (startTime == 0) {
            startTime = currentTime;
        }
        int duration = mMovie.duration();
        if (duration == 0) {
            duration = 1000;
        }
        int mTime = (int) ((currentTime - startTime) % duration);
        mMovie.setTime(mTime);
        mMovie.draw(mCanvas, 0, 0);
        if ((currentTime - startTime) > duration) {
            startTime = 0;
            return true;
        }
        return false;
    }

    /**
     * 通过Java反射，获取到src指定图片资源所对应的id
     *
     * @return 返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0
     */
    private int getResourceId(TypedArray mArray) {
        try {
            Field mField = TypedArray.class.getDeclaredField("mValue");
            mField.setAccessible(true);
            TypedValue mTypedValue = (TypedValue) mField.get(mArray);
            return mTypedValue.resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mArray != null) {
                mArray.recycle();
            }
        }
        return 0;
    }
}
