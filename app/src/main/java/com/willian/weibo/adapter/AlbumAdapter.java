package com.willian.weibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.willian.weibo.R;
import com.willian.weibo.activity.ImageShowActivity;
import com.willian.weibo.activity.TakePhotoActivity;
import com.willian.weibo.constant.Constant;
import com.willian.weibo.utils.ToastUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 相册图片的适配器
 */
public class AlbumAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_FIRST = 0;

    private static final int VIEW_TYPE_OTHER = 1;

    private static final int VIEW_TYPE_TOTAL = 2;

    private Context mContext;
    // 所有图片的URL
    private List<String> mPhotoList;
    // 已选择图片的URL
    private List<String> mSelectedPhotos;
    // 相册文件夹路径
    private String mDirPath;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public AlbumAdapter(Context mContext, List<String> mPhotoList, List<String> mSelectedPhotoList, String mDirPath) {
        this.mContext = mContext;
        this.mPhotoList = mPhotoList;
        this.mSelectedPhotos = mSelectedPhotoList;
        this.mDirPath = mDirPath;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FIRST;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_TOTAL;
    }

    @Override
    public int getCount() {
        return mPhotoList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mPhotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder viewHolder = null;
        CameraViewHolder cameraViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case VIEW_TYPE_FIRST:
                    convertView = mInflater.inflate(R.layout.gridview_item_album_camera, null);
                    cameraViewHolder = new CameraViewHolder();
                    cameraViewHolder.cameraLayout = (LinearLayout) convertView.findViewById(R.id.layout_camera);
                    convertView.setTag(cameraViewHolder);
                    break;
                case VIEW_TYPE_OTHER:
                    convertView = mInflater.inflate(R.layout.gridview_item_album_photo, null);
                    viewHolder = new ViewHolder();
                    viewHolder.albumPhoto = (ImageView) convertView.findViewById(R.id.iv_album_photo);
                    viewHolder.checkBtn = (ImageButton) convertView.findViewById(R.id.ib_choose);
                    // 第二次绘制ListView时，直接从getTag()中取出
                    convertView.setTag(viewHolder);
                    break;
            }
        } else {
            switch (type) {
                case VIEW_TYPE_FIRST:
                    cameraViewHolder = (CameraViewHolder) convertView.getTag();
                    break;
                case VIEW_TYPE_OTHER:
                    viewHolder = (ViewHolder) convertView.getTag();
                    break;
            }
        }

        switch (type) {
            case VIEW_TYPE_FIRST:
                cameraViewHolder.cameraLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 调用系统相机进行拍照
                        Intent mIntent = new Intent(mContext, TakePhotoActivity.class);
                        // 此处需要把用户已选择的图片作为参数传递
                        mIntent.putExtra("newPhotos", (Serializable) mSelectedPhotos);
                        mContext.startActivity(mIntent);
                    }
                });
                break;
            case VIEW_TYPE_OTHER:
                final Button nextStep = (Button) parent.getRootView().findViewById(R.id.btn_right);

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
                viewHolder.albumPhoto.setLayoutParams(params);

                final String photoPath = Constant.SD_CARD_PREFIX_URI + mDirPath + mPhotoList.get(position - 1);
                if (mSelectedPhotos.contains(photoPath)) {
                    viewHolder.albumPhoto.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.checkBtn.setImageResource(R.mipmap.compose_photo_preview_right);

                    nextStep.setEnabled(true);
                    nextStep.setText(mContext.getResources().getString(R.string.next_step) + "(" + mSelectedPhotos.size() + ")");
                    nextStep.setBackgroundResource(R.drawable.common_button_orange);
                } else {
                    viewHolder.albumPhoto.setColorFilter(null);
                    viewHolder.checkBtn.setImageResource(R.mipmap.compose_photo_preview_default);
                }

                ImageAware imageAware = new ImageViewAware(viewHolder.albumPhoto, false);
                mImageLoader.displayImage(photoPath, imageAware);
                // 滑动时禁止加载图片
                mGridView.setOnScrollListener(new PauseOnScrollListener(mImageLoader, true, true));

                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.checkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 已经选择过该图片
                        if (mSelectedPhotos.contains(photoPath)) {
                            finalViewHolder.checkBtn.setImageResource(R.mipmap.compose_photo_preview_default);
                            finalViewHolder.albumPhoto.setColorFilter(null);

                            mSelectedPhotos.remove(photoPath);
                            if (mSelectedPhotos.size() > 0) {
                                nextStep.setEnabled(true);
                                nextStep.setTextColor(mContext.getResources().getColor(R.color.white));
                                nextStep.setText(mContext.getResources().getString(R.string.next_step) + "(" + mSelectedPhotos.size() + ")");
                                nextStep.setBackgroundResource(R.drawable.common_button_orange);
                            } else {
                                nextStep.setEnabled(false);
                                nextStep.setTextColor(mContext.getResources().getColor(R.color.text_gray));
                                nextStep.setText(mContext.getResources().getString(R.string.next_step));
                                nextStep.setBackgroundResource(R.drawable.common_button_white_disable);
                            }
                        } else {
                            // 未选择该图片
                            if (mSelectedPhotos.size() >= 9) {
                                ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.no_more_than_nine), Toast.LENGTH_SHORT);
                            } else {
                                finalViewHolder.albumPhoto.setColorFilter(Color.parseColor("#77000000"));
                                finalViewHolder.checkBtn.setImageResource(R.mipmap.compose_photo_preview_right);
                                mSelectedPhotos.add(photoPath);
                                nextStep.setEnabled(true);
                                nextStep.setTextColor(mContext.getResources().getColor(R.color.white));
                                nextStep.setText(mContext.getResources().getString(R.string.next_step) + "(" + mSelectedPhotos.size() + ")");
                                nextStep.setBackgroundResource(R.drawable.common_button_orange);
                            }
                        }
                    }
                });

                viewHolder.albumPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(mContext, ImageShowActivity.class);
                        mIntent.putExtra("imageUrl", photoPath);
                        mContext.startActivity(mIntent);
                    }
                });
                break;
        }
        return convertView;
    }

    public void changeData(List<String> photoList, String dirPath) {
        this.mPhotoList = photoList;
        this.mDirPath = dirPath;
        notifyDataSetChanged();
    }

    public List<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    public static class ViewHolder {
        public ImageView albumPhoto;
        public ImageButton checkBtn;
    }

    public static class CameraViewHolder {
        public LinearLayout cameraLayout;
    }
}