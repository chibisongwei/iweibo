package com.willian.weibo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.willian.weibo.utils.ActivityManager;
import com.willian.weibo.utils.BaseApplication;
import com.willian.weibo.utils.LoggerUtil;

/**
 * 所有Activity的父类
 */
public class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";

    private ActivityManager activityManager = ActivityManager.getInstance();

    protected BaseApplication mApplication;

    protected ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManager.addActivity(this);
        mApplication = (BaseApplication) getApplication();
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoggerUtil.showLog(TAG, "===========onDestroy==========", 6);
        activityManager.removeActivity(this);
    }

    /**
     * 系统注销时销毁所有Activity
     */
    public void logout() {
        activityManager.finishAllActivity();
    }
}
