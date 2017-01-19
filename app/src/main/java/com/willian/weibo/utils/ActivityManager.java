package com.willian.weibo.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity统一管理工具类
 */
public class ActivityManager {

    private List<Activity> activityList;

    private static ActivityManager instance;


    private ActivityManager() {
        activityList = new ArrayList<Activity>();
    }

    public static ActivityManager getInstance() {

        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;

    }

    /**
     * 添加一个Activity
     *
     * @param mActivity
     */
    public void addActivity(Activity mActivity) {

        if (!activityList.contains(mActivity)) {
            activityList.add(mActivity);
        }
    }

    /**
     * 删除一个Activity
     *
     * @param mActivity
     */
    public void removeActivity(Activity mActivity) {
        if (activityList.contains(mActivity)) {
            activityList.remove(mActivity);
        }
    }

    /**
     * 销毁所有Activity
     */
    public void finishAllActivity() {
        for (Activity mActivity : activityList) {
            if (!mActivity.isFinishing()) {
                mActivity.finish();
            }
        }
        activityList.clear();
    }
}
