package com.willian.weibo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.willian.weibo.R;
import com.willian.weibo.adapter.EmotionAdapter;
import com.willian.weibo.adapter.EmotionPagerAdapter;
import com.willian.weibo.adapter.WriteStatusPhotoAdapter;
import com.willian.weibo.sdk.AccessTokenKeeper;
import com.willian.weibo.sdk.Constants;
import com.willian.weibo.utils.DisplayUtils;
import com.willian.weibo.utils.EmotionUtil;
import com.willian.weibo.utils.LoggerUtil;
import com.willian.weibo.utils.StringUtil;
import com.willian.weibo.utils.ToastUtil;
import com.willian.weibo.widget.WrapHeightGridView;
import com.willian.weibo.utils.TitleBarBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 写微博
 */
public class WriteStatusActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "WriteStatusActivity";

    // 引用的微博
    private Status repostStatus;

    // 从相册选择图片
    public final static int REQUEST_CODE_FORM_ALBUM = 1;
    // 九宫格图片
    private WrapHeightGridView mStatusPicGridView;
    private WriteStatusPhotoAdapter mStatusPicAdapter;
    private List<String> mPicUrlList = new ArrayList<String>();

    private ImageView mStatusPicImage;

    private ImageView mEmotionImage;

    private EditText contentText;
    // 表情面板
    private ViewPager emotionViewPager;

    private LinearLayout emotionLayout;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_status);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        repostStatus = (Status) getIntent().getSerializableExtra("status");
        // 拍照返回后的图片URL
        List<String> newPhotos = (List) getIntent().getSerializableExtra("newPhotos");
        if (newPhotos != null) {
            mPicUrlList = newPhotos;
        }

        initView();
        // 初始化转发微博
        initRetweetStatus();
        // 初始化表情
        initEmotion();
    }

    private void initView() {
        new TitleBarBuilder(this)
                .setTitleText(getResources().getText(R.string.send_weibo).toString())
                .setLeftText(getResources().getText(R.string.cancel).toString())
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.slide_out_from_right, R.anim.slide_in_from_left);
                    }
                })
                .setRightText(getResources().getText(R.string.send).toString())
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 发微博
                        sendStatus();
                    }
                });

        mStatusPicGridView = (WrapHeightGridView) findViewById(R.id.gv_write_status);
        mStatusPicAdapter = new WriteStatusPhotoAdapter(this, mPicUrlList, mStatusPicGridView);
        mStatusPicGridView.setAdapter(mStatusPicAdapter);
        mStatusPicGridView.setOnItemClickListener(this);

        mStatusPicImage = (ImageView) findViewById(R.id.iv_status_picture);
        mStatusPicImage.setOnClickListener(this);

        contentText = (EditText) findViewById(R.id.et_write_status);
        contentText.setOnClickListener(this);
        emotionViewPager = (ViewPager) findViewById(R.id.vp_emotion_dashboard);
        emotionLayout = (LinearLayout) findViewById(R.id.layout_emotion_dashboard);
        mEmotionImage = (ImageView) findViewById(R.id.iv_status_emoji);
        mEmotionImage.setOnClickListener(this);
    }

    /**
     * 初始化转发微博信息
     */
    private void initRetweetStatus() {
        LinearLayout cardLayout = (LinearLayout) findViewById(R.id.layout_retweet_card);
        ImageView cardImage = (ImageView) cardLayout.findViewById(R.id.iv_card_userhead);
        TextView cardName = (TextView) cardLayout.findViewById(R.id.tv_card_username);
        TextView cardContent = (TextView) cardLayout.findViewById(R.id.tv_card_content);

        if (repostStatus != null) {
            Status cardStatus;
            // 判断转发的微博是否包含转发内容
            Status retweetedStatus = repostStatus.retweeted_status;
            if (retweetedStatus != null) {
                // 微博内容
                String content = "//@" + repostStatus.user.name + ":" + repostStatus.text;
                contentText.setText(StringUtil.getWeiboContent(this, contentText, content));
                cardStatus = retweetedStatus;
            } else {
                contentText.setHint(getResources().getString(R.string.repost_status_hint));
                cardStatus = repostStatus;
            }

            // 设置转发图片内容
            List<String> imageUrls = cardStatus.pic_urls;
            if (imageUrls != null && imageUrls.size() > 0) {
                // 取第一张图片作为转发图片
                mImageLoader.displayImage(imageUrls.get(0), cardImage);
            } else {
                // 设置用户头像为转发图片
                String userHeadUrl = cardStatus.user.avatar_large;
                mImageLoader.displayImage(userHeadUrl, cardImage);
            }
            // 设置转发文字内容
            cardName.setText("@" + cardStatus.user.name);
            cardContent.setText(StringUtil.getWeiboContent(this, cardContent, cardStatus.text));

            cardLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化表情面板
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int gvWidth = DisplayUtils.getScreenWidthPixels(this);
        // 表情边距
        int spacing = DisplayUtils.dp2px(this, 8);
        // GridView中item的宽度
        int itemWidth = (gvWidth - spacing * 8) / 7;
        // GirdView的高度
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gridViews = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        // 遍历所有的表情名字
        for (String emotionName : EmotionUtil.emotionMap.keySet()) {
            emotionNames.add(emotionName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
                gridViews.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<String>();
            }
        }

        // 检查最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
            gridViews.add(gv);
        }

        // 将多个GridView添加显示到ViewPager中
        EmotionPagerAdapter emotionPagerAdapter = new EmotionPagerAdapter(gridViews);
        emotionViewPager.setAdapter(emotionPagerAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        emotionViewPager.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.tab_emotion_bg_normal);
        gv.setSelector(R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionAdapter adapter = new EmotionAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return gv;
    }

    private void sendStatus() {
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        StatusesAPI mStatuesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
        // 微博文本内容
        String statusContent = contentText.getText().toString();
        // 是否是转发微博
        if (repostStatus != null) {
            if (TextUtils.isEmpty(statusContent)) {
                statusContent = getResources().getString(R.string.repost_status);
            }
            // 要转发的微博ID
            long retweetId = Long.parseLong(repostStatus.idstr);
            // 转发微博
            mStatuesAPI.repost(retweetId, statusContent, 0, mListener);
        } else {
            if (!TextUtils.isEmpty(statusContent)) {
                // 只发送文字微博，图片微博暂不做处理
                mStatuesAPI.update(statusContent, "0.0", "0.0", mListener);
            } else {
                ToastUtil.showToast(this, getResources().getString(R.string.status_not_null), Toast.LENGTH_SHORT);
                return;
            }
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LoggerUtil.showLog(TAG, response);
                // 调用 Status#parse 解析字符串成微博对象
                Status status = Status.parse(response);
                ToastUtil.showToast(WriteStatusActivity.this,
                        "发送一送微博成功, id = " + status.id, Toast.LENGTH_SHORT);
                finish();
            } else {
                Toast.makeText(WriteStatusActivity.this, response,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LoggerUtil.showLog(TAG, e.getMessage(), 6);
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WriteStatusActivity.this, info.toString(),
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_FORM_ALBUM:
                // 用户选中的图片
                List<String> selectedPhotos = (List) data.getSerializableExtra("selectedPhotos");
                for (String photoUrl : selectedPhotos) {
                    if (!mPicUrlList.contains(photoUrl)) {
                        mPicUrlList.add(photoUrl);
                    }
                }
                updateImages();
                break;
            default:
                break;
        }
    }

    /**
     * 更新图片
     */
    private void updateImages() {
        if (mPicUrlList.size() > 0) {
            mStatusPicGridView.setVisibility(View.VISIBLE);
            mStatusPicAdapter.notifyDataSetChanged();
        } else {
            mStatusPicGridView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_write_status:
                // 显示软键盘
                inputMethodManager.showSoftInput(contentText, 0);
                // 隐藏表情面板
                if (emotionLayout.getVisibility() == View.VISIBLE) {
                    emotionLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_status_picture:
                Intent mAlbumIntent = new Intent(this, AlbumActivity.class);
                // 传入已选择的图片
                mAlbumIntent.putExtra("mSelectedPhotos", (Serializable) mStatusPicAdapter.getPhotoUrlList());
                startActivityForResult(mAlbumIntent, REQUEST_CODE_FORM_ALBUM);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;
            case R.id.iv_status_at:

                break;
            case R.id.iv_status_topic:

                break;
            case R.id.iv_status_emoji:
                if (emotionLayout.getVisibility() == View.VISIBLE) {
                    // 显示软键盘
                    inputMethodManager.showSoftInput(contentText, 0);
                    // 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
                    mEmotionImage.setImageResource(R.drawable.toolbar_emoji_selector);
                    emotionLayout.setVisibility(View.GONE);
                } else {
                    // 隐藏软键盘
                    inputMethodManager.hideSoftInputFromWindow(contentText.getWindowToken(), 0);
                    // 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
                    mEmotionImage.setImageResource(R.drawable.compose_keyboardbutton_selector);
                    emotionLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iv_status_more:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();

        if (itemAdapter instanceof WriteStatusPhotoAdapter) {
            // 点击的是添加图片
            if (position == mStatusPicAdapter.getCount() - 1) {
                Intent mAlbumIntent = new Intent(WriteStatusActivity.this, AlbumActivity.class);
                // 传入已选择的图片
                mAlbumIntent.putExtra("mSelectedPhotos", (Serializable) mStatusPicAdapter.getPhotoUrlList());
                startActivityForResult(mAlbumIntent, REQUEST_CODE_FORM_ALBUM);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        } else if (itemAdapter instanceof EmotionAdapter) {
            // 点击的是表情
            EmotionAdapter emotionAdapter = (EmotionAdapter) itemAdapter;
            // 如果点击了最后一个回退按钮,则调用删除键事件
            if (position == emotionAdapter.getCount() - 1) {
                contentText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                String emotionName = (String) emotionAdapter.getItem(position);
                // 获取EidtText光标的起始位置
                int startIndex = contentText.getSelectionStart();
                StringBuilder sb = new StringBuilder(contentText.getText().toString());
                sb.insert(startIndex, emotionName);
                // 特殊文字处理,将表情等转换一下
                contentText.setText(StringUtil.getWeiboContent(this, contentText, sb.toString()));
                // 将光标设置到新增完表情的右侧
                contentText.setSelection(startIndex + emotionName.length());
            }
        }
    }
}
