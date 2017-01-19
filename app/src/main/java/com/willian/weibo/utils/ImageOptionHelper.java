package com.willian.weibo.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.willian.weibo.R;

/**
 * 设置显示图片的参数
 */
public class ImageOptionHelper {

    /**
     * 用户头像的显示方式
     *
     * @return
     */
    public static DisplayImageOptions getAvatarOptions() {
        DisplayImageOptions avatarOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的解码类型
                .showImageOnLoading(R.mipmap.avatar_default)
                .showImageForEmptyUri(R.mipmap.avatar_default)
                .showImageOnFail(R.mipmap.avatar_default)
                .displayer(new RoundedBitmapDisplayer(999)) //是否设置为圆角，弧度为多少
                .build();
        return avatarOptions;
    }

    /**
     * 普通图片的显示方式
     *
     * @return
     */
    public static DisplayImageOptions getImageOptions() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.pic_bg)
                .showImageForEmptyUri(R.drawable.pic_bg)
                .showImageOnFail(R.drawable.pic_bg)
                .delayBeforeLoading(100) // 载入图片前稍做延时可以提高整体滑动的流畅度
                .build();
        return imageOptions;
    }
}
