package com.willian.weibo.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.willian.weibo.R;
import com.willian.weibo.adapter.AlbumAdapter;
import com.willian.weibo.adapter.AlbumFolderAdapter;
import com.willian.weibo.bean.AlbumFolder;
import com.willian.weibo.utils.DisplayUtils;
import com.willian.weibo.utils.LoggerUtil;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.utils.ToastUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * 相册图片
 */
public class AlbumActivity extends BaseActivity {

    private final static String TAG = "AlbumActivity";
    // 相册GridView
    private GridView mAlbumGridView;
    // 相册适配器
    private AlbumAdapter mAlbumAdapter;
    // 所有图片的URL集合
    private List<String> mPhotoList = new ArrayList<String>();
    // 已选择的图片URL集合
    private List<String> mSelectedPhotoList;
    // 所有相册文件夹的路径集合
    private HashSet<String> mDirPaths = new HashSet<String>();
    // 所有相册文件夹
    private List<AlbumFolder> mFolderList = new ArrayList<AlbumFolder>();
    // 相册文件夹集合
    private ListView mFolderListView;
    // 相册文件夹适配器
    private AlbumFolderAdapter folderAdapter;
    // 图片总数
    private int totalCount;
    // 第一张图片的路径
    private String firstImagePath;
    // 标题栏
    private View mTitleBar;

    private LinearLayout mTitleLayout;
    // 标题
    private TextView mTitleName;

    private ImageView mArrowImage;
    // 弹出框
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        // 获取用户已选择过的图片URL
        mSelectedPhotoList = (List) getIntent().getSerializableExtra("mSelectedPhotos");
        if (mSelectedPhotoList == null) {
            mSelectedPhotoList = new ArrayList<String>();
        }

        initView();
        // 加载相册图片
        loadAlbumPhotos();
        // 事件处理
        handleEvent();
    }

    private void initView() {
        /**
         * 初始化标题栏
         */
        new TitleBarBuilder(this)
                .setTitleText(getResources().getString(R.string.album))
                .setLeftText(getResources().getString(R.string.cancel))
                .setTitleText(getResources().getString(R.string.camera_film))
                .setArrowImage(R.mipmap.navigationbar_arrow_down)
                .setRightButton(getResources().getString(R.string.next_step))
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.in_from_top, R.anim.out_to_bottom);
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 已选中的照片
                        List<String> selectedPhotos = mAlbumAdapter.getSelectedPhotos();
                        Intent mIntent = new Intent();
                        mIntent.putExtra("selectedPhotos", (Serializable) selectedPhotos);
                        // 回传数据给WriteStatusActivity
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                });
        mTitleBar = findViewById(R.id.layout_title_bar);
        mTitleLayout = (LinearLayout) findViewById(R.id.layout_titile);
        mTitleName = (TextView) mTitleLayout.findViewById(R.id.tv_title);
        mArrowImage = (ImageView) mTitleLayout.findViewById(R.id.iv_arrow);

        mAlbumGridView = (GridView) findViewById(R.id.gv_album);
    }

    /**
     * 获取相册图片
     */
    private void loadAlbumPhotos() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showToast(AlbumActivity.this, "未检测到SD卡", Toast.LENGTH_SHORT);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = AlbumActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    mPhotoList.add(path);
                    if (TextUtils.isEmpty(firstImagePath)) {
                        firstImagePath = path;
                    }
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    AlbumFolder albumFolder;
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        LoggerUtil.showLog(TAG, ">>>>>>>>>>>>>>CoverImagePath>>>>>>>>>>>>>=" + path, 6);
                        mDirPaths.add(dirPath);
                        // 获取文件夹名称
                        int lastIndex = dirPath.lastIndexOf("/");
                        String dirName = dirPath.substring(lastIndex + 1);
                        albumFolder = new AlbumFolder();
                        albumFolder.setFolderName(dirName);
                        albumFolder.setFolderDir(dirPath);
                        albumFolder.setCoverImagePath(path);
                    }
                    if (parentFile.list() == null) {
                        continue;
                    }
                    // 计算每个文件夹包含的图片数
                    int imageCounts = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            // 把文件名都转换成小写
                            String lowerFileName = filename.toLowerCase();
                            if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".png") || lowerFileName.endsWith(".jpeg")) {
                                return true;
                            }
                            return false;
                        }
                    }).length;

                    albumFolder.setImageCounts(imageCounts);
                    mFolderList.add(albumFolder);
                    // 总图片数
                    totalCount += imageCounts;
                }
                mCursor.close();
                mDirPaths = null;
                mHandler.sendEmptyMessage(0x110);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateData();
        }
    };

    private void updateData() {
        // 默认文件夹包含所有相片
        AlbumFolder albumFolder = new AlbumFolder();
        albumFolder.setSelected(true);
        albumFolder.setFolderName(getResources().getString(R.string.camera_film));
        albumFolder.setFolderDir("");
        albumFolder.setCoverImagePath(firstImagePath);
        albumFolder.setImageCounts(totalCount);
        mFolderList.add(albumFolder);
        // 按文件夹名称首字母进行排序；
        Collections.sort(mFolderList, new Comparator<AlbumFolder>() {

            private Collator mCollator = Collator.getInstance(Locale.CHINA);

            @Override
            public int compare(AlbumFolder lhs, AlbumFolder rhs) {
                CollationKey key1 = mCollator.getCollationKey(lhs.getFolderName());
                CollationKey key2 = mCollator.getCollationKey(rhs.getFolderName());
                return mCollator.compare(key1.getSourceString(), key2.getSourceString());
            }
        });
        // 默认显示所有图片
        mAlbumAdapter = new AlbumAdapter(this, mPhotoList, mSelectedPhotoList, "");
        mAlbumGridView.setAdapter(mAlbumAdapter);
    }

    private void handleEvent() {
        mTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
            }
        });
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        // 弹窗时，设置背景窗体的透明度
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        mArrowImage.setImageResource(R.mipmap.navigationbar_arrow_up);
        if (popupWindow == null) {
            View folderLayout = View.inflate(this, R.layout.album_folder_list, null);
            mFolderListView = (ListView) folderLayout.findViewById(R.id.lv_folder);
            popupWindow = new PopupWindow(folderLayout, ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtils.getScreenHeightPixels(this) / 2);
            folderAdapter = new AlbumFolderAdapter(this, mFolderList, mAlbumAdapter.getSelectedPhotos());
            mFolderListView.setAdapter(folderAdapter);
        }
        //点击外部，自动消失
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 设置弹出动画
        popupWindow.setAnimationStyle(R.style.popup_window_style);
        //设置弹出框的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x90000000));
        // 位于某个View的正下方
        popupWindow.showAsDropDown(mTitleBar);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mArrowImage.setImageResource(R.mipmap.navigationbar_arrow_down);
                // 取消弹窗时，恢复背景窗体的透明度
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        mFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 选中某个Item
                for (int i = 0; i < mFolderList.size(); i++) {
                    mFolderList.get(i).setSelected(false);
                }
                mFolderList.get(position).setSelected(true);
                // 更新标题
                mTitleName.setText(mFolderList.get(position).getFolderName());
                // 更新图片
                String dirPath = mFolderList.get(position).getFolderDir();
                if (dirPath == "") {
                    // 显示所有图片
                    mAlbumAdapter.changeData(mPhotoList, dirPath);
                } else {
                    File mImgDir = new File(dirPath);
                    List<String> photoList = Arrays.asList(mImgDir.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            // 把文件名都转换成小写
                            String lowerFileName = filename.toLowerCase();
                            if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".png") || lowerFileName.endsWith(".jpeg")) {
                                return true;
                            }
                            return false;
                        }
                    }));
                    Collections.reverse(photoList);
                    mAlbumAdapter.changeData(photoList, dirPath + "/");
                }
                folderAdapter.changeData(mFolderList);
                // 关闭PopupWindow
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }
}