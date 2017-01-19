package com.willian.weibo.utils;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sina.weibo.sdk.openapi.models.User;

import java.io.File;

/**
 * 设置全局变量，完成一些初始化等工作
 */
public class BaseApplication extends Application {
    // 当前登录用户
    public User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        initImageLoader(this);
    }

    /**
     * 初始化图片处理
     *
     * @param context
     */
    private void initImageLoader(Context context) {
        // 设置缓存目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "weibo/Cache");

        ImageLoaderConfiguration mConfig = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .diskCacheFileCount(100) //缓存的文件数量
                .diskCache(new UnlimitedDiskCache(cacheDir)) // 自定义缓存路径
                .defaultDisplayImageOptions(ImageOptionHelper.getImageOptions())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .build();

        ImageLoader.getInstance().init(mConfig);
    }
}
