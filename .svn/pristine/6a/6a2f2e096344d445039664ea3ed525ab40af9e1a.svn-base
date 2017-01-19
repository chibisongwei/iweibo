package com.willian.weibo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.willian.weibo.R;
import com.willian.weibo.utils.LoggerUtil;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.widget.ZoomImageView;

/**
 * 显示相册单张图片
 */
public class ImageShowActivity extends BaseActivity {

    private static final String TAG = "ImageShowActivity";

    private ZoomImageView mZoomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        // 获取图片的显示路径
        String imageUrl = getIntent().getStringExtra("imageUrl");

        initView();
        int index = imageUrl.indexOf("/storage");
        LoggerUtil.showLog(TAG, "===========imageUrl==============" + imageUrl, 6);
        Bitmap bitmap = BitmapFactory.decodeFile(imageUrl.substring(index));
        mZoomImageView.setImageBitmap(bitmap);
    }

    private void initView() {
        new TitleBarBuilder(this)
                .setLeftImage(R.drawable.titlebar_back_selector)
                .setRightButton(getResources().getString(R.string.next_step))
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.slide_out_from_right, R.anim.slide_in_from_left);
                    }
                });

        mZoomImageView = (ZoomImageView) findViewById(R.id.iv_show);
    }
}
