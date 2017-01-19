package com.willian.weibo.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.willian.weibo.R;
import com.willian.weibo.utils.DisplayUtils;
import com.willian.weibo.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willian on 2016/4/11.
 */
public class ImageBrowserAdapter extends PagerAdapter {

    private Activity mContext;

    private List<String> imageUrls;

    private List<View> imageList;

    private ImageLoader mImageLoader;

    public ImageBrowserAdapter(Activity mContext, List<String> imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
        this.mImageLoader = ImageLoader.getInstance();
        // 填充显示图片的页面布局
        initImageList();
    }

    private void initImageList() {
        imageList = new ArrayList<View>();
        for (int i = 0; i < imageUrls.size(); i++) {
            View view = View.inflate(mContext, R.layout.item_image_browser, null);
            imageList.add(view);
        }
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View mView = imageList.get(position);
        final ImageView browserImage = (ImageView) mView.findViewById(R.id.iv_image_browser);
        String imageUrl = imageUrls.get(position);
        // 获取原图的URL
        String originalImageUrl = ImageUtil.getOriginalPic(imageUrl);

        mImageLoader.loadImage(originalImageUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // 根据缩放比例调整图片显示的宽高
                float scale = (float) loadedImage.getHeight() / loadedImage.getWidth();
                int screenWidthPixels = DisplayUtils.getScreenWidthPixels(mContext);
                int screenHeightPixels = DisplayUtils.getScreenHeightPixels(mContext);
                int height = (int) (screenWidthPixels * scale);

                if (height < screenHeightPixels) {
                    height = screenHeightPixels;
                }

                ViewGroup.LayoutParams params = browserImage.getLayoutParams();
                params.height = height;
                params.width = screenWidthPixels;

                browserImage.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        browserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });

        container.addView(mView);
        return mView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public Bitmap getBitmap(int position) {
        Bitmap bitmap = null;
        View view = imageList.get(position % imageUrls.size());
        ImageView iv_image_browser = (ImageView) view.findViewById(R.id.iv_image_browser);
        Drawable drawable = iv_image_browser.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            bitmap = bd.getBitmap();
        }

        return bitmap;
    }
}
