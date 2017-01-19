package com.willian.weibo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.willian.weibo.R;

/**
 * 引导Activity
 * 如果客户第一次使用App，则出现引导界面；
 */
public class GuideActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }
}
