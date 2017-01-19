package com.willian.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.willian.weibo.R;
import com.willian.weibo.bean.AlbumFolder;
import com.willian.weibo.constant.Constant;
import com.willian.weibo.utils.LoggerUtil;

import java.util.List;

/**
 * 相册文件夹适配器
 */
public class AlbumFolderAdapter extends BaseAdapter {

    private static final String TAG = "AlbumFolderAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumFolder> mFolderList;
    private ImageLoader mImageLoader;
    // 选中的相片路径集合
    private List<String> mSelectedPath;

    public AlbumFolderAdapter(Context mContext, List<AlbumFolder> mFolderList, List<String> mSelectedPath) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mFolderList = mFolderList;
        this.mSelectedPath = mSelectedPath;
        this.mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mFolderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFolderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.album_folder_item, null);
            viewHolder.dirLayout = (LinearLayout) convertView.findViewById(R.id.layout_dir);
            viewHolder.dirItemIcon = (ImageView) viewHolder.dirLayout.findViewById(R.id.dir_item_image);
            viewHolder.filterIcon = (ImageView) viewHolder.dirLayout.findViewById(R.id.iv_filter);
            viewHolder.dirItemName = (TextView) viewHolder.dirLayout.findViewById(R.id.dir_item_name);
            viewHolder.dirItemCount = (TextView) viewHolder.dirLayout.findViewById(R.id.dir_item_count);
            // 第二次绘制ListView时，直接从getTag()中取出
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AlbumFolder mAlbumFolder = mFolderList.get(position);
        // 文件夹名称
        String dirName = mAlbumFolder.getFolderName();
        // 文件夹Icon图片
        mImageLoader.displayImage(Constant.SD_CARD_PREFIX_URI + mAlbumFolder.getCoverImagePath(), viewHolder.dirItemIcon);
        // 文件夹名称
        viewHolder.dirItemName.setText(dirName);
        // 文件夹包含的图片数
        viewHolder.dirItemCount.setText("(" + mAlbumFolder.getImageCounts() + ")");
        // 文件夹是否被选中
        boolean isSelected = mAlbumFolder.isSelected();
        if (isSelected) {
            viewHolder.dirLayout.setBackgroundColor(mContext.getResources().getColor(R.color.alpha_gray));
        } else {
            viewHolder.dirLayout.setBackgroundColor(mContext.getResources().getColor(R.color.alpha_white));
        }

        String dirPath = mAlbumFolder.getFolderDir();
        if (mSelectedPath.size() > 0) {
            if (dirPath == "") {
                // 相机胶卷为总目录，包含所有相片
                viewHolder.filterIcon.setVisibility(View.VISIBLE);
            } else {
                for (String photoPath : mSelectedPath) {
                    // photoPath是否包含文件夹名称字符
                    if (photoPath.contains(dirName)) {
                        viewHolder.filterIcon.setVisibility(View.VISIBLE);
                        break;
                    } else {
                        viewHolder.filterIcon.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            // 没有选中任何图片
            viewHolder.filterIcon.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 点击切换显示不同文件夹下的图片
     *
     * @param folderList
     */
    public void changeData(List<AlbumFolder> folderList) {
        this.mFolderList = folderList;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public LinearLayout dirLayout;
        public ImageView dirItemIcon;
        public ImageView filterIcon;
        public TextView dirItemName;
        public TextView dirItemCount;
    }
}
