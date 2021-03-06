package com.willian.weibo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 字母索引侧边栏
 */
public class SideBarButton extends Button {
    // 画笔
    private Paint mPaint;
    // 字母索引
    public static final String[] SECTIONS = new String[]{"A", "B", "C", "D",
            "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    public SideBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        // 设置画笔的颜色
        mPaint.setColor(Color.GRAY);
        // 设置画笔的字体位置
        mPaint.setTextAlign(Paint.Align.CENTER);
        // 设置画笔的字体大小
        mPaint.setTextSize(36f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float positionX = getWidth() / 2;
        float heightScale = getHeight() / SECTIONS.length;
        for (int i = 0; i < SECTIONS.length; i++) {
            float positionY = heightScale * (i + 1);
            // 绘制字母
            canvas.drawText(SECTIONS[i], positionX, positionY, mPaint);
        }
    }
}
