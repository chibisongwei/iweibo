package com.willian.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.willian.weibo.R;
import com.willian.weibo.utils.ImageUtil;

import java.util.List;

/**
 * Created by willian on 2016/3/17.
 */
public class StatusPictureAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mPicList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public StatusPictureAdapter(Context mContext, List<String> mPicList) {
        this.mPicList = mPicList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mPicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gridview_item_pic, null);
            viewHolder = new ViewHolder();
            viewHolder.weiboImage = (ImageView) convertView.findViewById(R.id.iv_picture);

            // 第二次绘制ListView时，直接从getTag()中取出
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GridView mGridView = (GridView) parent;
        // 获取item之间的水平间距
        int horizontalSpacing = mGridView.getHorizontalSpacing();
        // 获取列数
        int numColums = mGridView.getNumColumns();
        // 计算每个item的宽度
        int itemWidth = (mGridView.getWidth() - (numColums - 1) * horizontalSpacing
                - mGridView.getPaddingLeft() - mGridView.getPaddingRight()) / numColums;
        // 设置ImageView的宽度和高度
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
        viewHolder.weiboImage.setLayoutParams(params);

        String picUrl = mPicList.get(position);

        // 获取原图的URL
        String originalImageUrl = ImageUtil.getBmiddlePic(picUrl);

        mImageLoader.displayImage(originalImageUrl, viewHolder.weiboImage);

        return convertView;
    }

    public static class ViewHolder {
        public ImageView weiboImage;
    }
}
