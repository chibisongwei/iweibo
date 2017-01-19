package com.willian.weibo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.willian.weibo.utils.DisplayUtils;
import com.willian.weibo.utils.LoggerUtil;
import com.willian.weibo.R;
import com.willian.weibo.adapter.StatusCommentAdapter;
import com.willian.weibo.adapter.StatusPictureAdapter;
import com.willian.weibo.sdk.AccessTokenKeeper;
import com.willian.weibo.sdk.Constants;
import com.willian.weibo.utils.DateUtil;
import com.willian.weibo.utils.StringUtil;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.utils.Utility;
import com.willian.weibo.utils.WeiboUtil;
import com.willian.weibo.widget.MyScrollView;
import com.willian.weibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 微博详情界面
 */
public class WeiboDetailActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "WeiboDetailActivity";

    private static final int REQUEST_CODE_WRITE_COMMENT = 1;

    private ImageLoader mImageLoader;

    private LinearLayout mHeaderLayout;

    private ImageView userAvatar;
    private ImageView verifiedImage;
    private TextView userName;
    private TextView sendTime;
    private TextView source;

    private TextView statusContent;
    private FrameLayout statusImageLayout;
    private GridView statusImages;
    private ImageView statusSingleImage;

    private LinearLayout retweetLayout;
    private TextView retweetContent;
    private FrameLayout retweetImageLayout;
    private GridView retweetImages;
    private ImageView retweetSingleImage;

    private View statusDetailTab;
    private RadioGroup statusDetailRadioGroup;
    private RadioButton retweetButton;
    private RadioButton commentButton;
    private RadioButton attitudeButton;

    private ListView mListView;
    private List<Comment> mCommentList = new ArrayList<Comment>();
    private StatusCommentAdapter commentAdapter;

    /**
     * 底部操作栏
     */
    private LinearLayout bottomLayout;
    private LinearLayout retweetActionLayout;
    private LinearLayout commentActionLayout;
    private LinearLayout attitudeActionLayout;
    /**
     * 当前 Token 信息
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 微博评论接口
     */
    private CommentsAPI mCommentsAPI;

    // 详情页的微博信息
    private Status mStatus;
    // 是否需要滚动至评论部分
    private boolean scroll2Comment;
    /**
     * 当前页数
     */
    private int curPage = 1;

    private MyScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_detail);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);

        // 获取intent传入的信息
        mStatus = (Status) getIntent().getSerializableExtra("status");
        scroll2Comment = getIntent().getBooleanExtra("scroll2Comment", false);

        // 初始化
        initView();
        // 设置数据
        setData(mStatus);
        // 默认加载微博评论
        loadComments(1);
    }

    private void initView() {

        initTitleView();

        initStatusView();

        initListView();

        initBottomView();
    }

    private void initTitleView() {
        new TitleBarBuilder(this)
                .setTitleText(getResources().getText(R.string.weibo_detail).toString())
                .setLeftImage(R.drawable.titlebar_back_selector)
                .setRightImage(R.drawable.titlebar_icon_more_selector)
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    private void initStatusView() {
        mImageLoader = ImageLoader.getInstance();
        mHeaderLayout = (LinearLayout) findViewById(R.id.layout_status);
        // 设置背景色
        mHeaderLayout.setBackgroundResource(R.color.white);
        // 隐藏底部操作栏
        mHeaderLayout.findViewById(R.id.layout_status_action).setVisibility(View.GONE);
        // 显示Divider
        mHeaderLayout.findViewById(R.id.top_divider).setVisibility(View.VISIBLE);
        mHeaderLayout.findViewById(R.id.bottom_divider).setVisibility(View.VISIBLE);

        userAvatar = (ImageView) mHeaderLayout.findViewById(R.id.iv_avatar);
        verifiedImage = (ImageView) mHeaderLayout.findViewById(R.id.iv_verified);
        userName = (TextView) mHeaderLayout.findViewById(R.id.tv_username);
        sendTime = (TextView) mHeaderLayout.findViewById(R.id.tv_time);
        source = (TextView) mHeaderLayout.findViewById(R.id.tv_source);

        statusContent = (TextView) mHeaderLayout.findViewById(R.id.tv_status_content);
        statusImageLayout = (FrameLayout) mHeaderLayout.findViewById(R.id.layout_status_image);
        statusImages = (GridView) statusImageLayout.findViewById(R.id.gv_status_image);
        statusSingleImage = (ImageView) statusImageLayout.findViewById(R.id.iv_status_image);

        retweetLayout = (LinearLayout) mHeaderLayout.findViewById(R.id.layout_status_retweet);
        retweetContent = (TextView) mHeaderLayout.findViewById(R.id.tv_retweet_content);
        retweetImageLayout = (FrameLayout) mHeaderLayout.findViewById(R.id.layout_retweet_image);
        retweetImages = (GridView) retweetImageLayout.findViewById(R.id.gv_status_image);
        retweetSingleImage = (ImageView) retweetImageLayout.findViewById(R.id.iv_status_image);
    }

    private void initListView() {
        statusDetailTab = View.inflate(this, R.layout.include_status_detail_tab, null);
        statusDetailRadioGroup = (RadioGroup) statusDetailTab.findViewById(R.id.rg_status_detail_tab);
        retweetButton = (RadioButton) statusDetailTab.findViewById(R.id.rb_retweet);
        commentButton = (RadioButton) statusDetailTab.findViewById(R.id.rb_comment);
        attitudeButton = (RadioButton) statusDetailTab.findViewById(R.id.rb_attitude);
        statusDetailRadioGroup.setOnCheckedChangeListener(this);

        mListView = (ListView) findViewById(R.id.lv_detail);
        mListView.addHeaderView(statusDetailTab);
        commentAdapter = new StatusCommentAdapter(this, mCommentList);
        mListView.setAdapter(commentAdapter);
        mScrollView = (MyScrollView) findViewById(R.id.sv_detail);
        mScrollView.setHeaderLayout(mHeaderLayout);
        mScrollView.setListView(mListView);
    }

    private void initBottomView() {
        bottomLayout = (LinearLayout) findViewById(R.id.layout_detail_bar);
        retweetActionLayout = (LinearLayout) bottomLayout.findViewById(R.id.layout_retweet);
        commentActionLayout = (LinearLayout) bottomLayout.findViewById(R.id.layout_comment);
        attitudeActionLayout = (LinearLayout) bottomLayout.findViewById(R.id.layout_attitude);

        commentActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(WeiboDetailActivity.this, WriteCommentActivity.class);
                mIntent.putExtra("status", mStatus);
                startActivityForResult(mIntent, REQUEST_CODE_WRITE_COMMENT);
            }
        });
    }

    private void loadComments(final int page) {

        long statusId = Long.parseLong(mStatus.id);
        mCommentsAPI.show(statusId, 0L, 0L, 20, page, CommentsAPI.SRC_FILTER_ALL, new RequestListener() {

            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    LoggerUtil.showLog(TAG, response);
                    // 调用 CommentList#parse 解析字符串成评论列表对象
                    CommentList comments = CommentList.parse(response);
                    if (comments != null && comments.total_number > 0) {
                        if (page == 1) {
                            mCommentList.clear();
                        }
                        curPage = page;
                        commentButton.setText(getResources().getString(R.string.comment) + " " + WeiboUtil.getDisplayCount(comments.total_number));
                        // 加载更多数据
                        if (comments.commentList != null && comments.commentList.size() > 0) {
                            loadMoreData(comments);
                        }
                        // 判断是否需要滚动到评论列表
                        if (scroll2Comment) {
                            scroll2Comment = false;
                        }
                    } else {
                        Toast.makeText(WeiboDetailActivity.this, response,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                LoggerUtil.showLog(TAG, e.getMessage());
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                Toast.makeText(WeiboDetailActivity.this, info.toString(),
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private void loadMoreData(CommentList comments) {
        for (Comment mComment : comments.commentList) {
            if (!mCommentList.contains(comments)) {
                mCommentList.add(mComment);
            }
        }
        commentAdapter.notifyDataSetChanged();
        Utility.setListViewHeightBasedOnChildren(mListView);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_retweet:
                retweetButton.setChecked(true);
                break;
            case R.id.rb_comment:
                commentButton.setChecked(true);
                break;
            case R.id.rb_attitude:
                attitudeButton.setChecked(true);
                break;
            default:
                break;
        }
    }

    private void setData(final Status mStatus) {
        // 设置微博信息
        setStatus(mStatus);
        // 设置转发微博信息
        setRetweetStatus(mStatus);
        // listView headerView - 添加至列表中作为header的菜单栏
        retweetButton.setText(getResources().getString(R.string.retweet) + " " + WeiboUtil.getDisplayCount(mStatus.reposts_count));
        commentButton.setText(getResources().getString(R.string.comment) + " " + WeiboUtil.getDisplayCount(mStatus.comments_count));
        attitudeButton.setText(getResources().getString(R.string.attitude) + " " + WeiboUtil.getDisplayCount(mStatus.attitudes_count));
    }

    private void setStatus(Status mStatus) {
        // 获取围脖发布者的头像
        String userImageUrl = mStatus.user.avatar_large;
        // 获取微博发布者的ID
        String user_name = mStatus.user.name;
        // 是否认证
        boolean verified = mStatus.user.verified;

        mImageLoader.displayImage(userImageUrl, userAvatar);
        userName.setText(user_name);
        if (verified) {
            verifiedImage.setVisibility(View.VISIBLE);
        } else {
            verifiedImage.setVisibility(View.GONE);
        }
        // 获取微博发送时间
        String createdAt = mStatus.created_at;
        // 格式化时间
        String createdTime = DateUtil.getPostDate(createdAt, sendTime);

        sendTime.setText(createdTime);

        String sourceName = "";

        if (!TextUtils.isEmpty(mStatus.source)) {
            sourceName = getResources().getString(R.string.weibo_from) + " " + Html.fromHtml(mStatus.source);
        }
        source.setText(sourceName);

        // 获取微博内容
        statusContent.setText(StringUtil.getWeiboContent(this, statusContent, mStatus.text));

        setImages(mStatus, statusImageLayout, statusImages, statusSingleImage);
    }

    private void setRetweetStatus(Status status) {
        // 获取转发的微博
        Status retweetStatus = status.retweeted_status;

        if (retweetStatus != null) {
            User retweetUser = retweetStatus.user;
            if (retweetUser != null) {
                String retweetContentStr = "@" + retweetUser.name + ":" + retweetStatus.text;
                retweetLayout.setVisibility(View.VISIBLE);
                retweetContent.setText(StringUtil.getWeiboContent(this, retweetContent, retweetContentStr));
            } else {
                retweetContent.setText("抱歉，该微博已被删除。");
            }
            setImages(retweetStatus, retweetImageLayout, retweetImages, retweetSingleImage);
        } else {
            retweetLayout.setVisibility(View.GONE);
        }
    }

    private void setImages(final Status status, FrameLayout layout, GridView gridView, final ImageView imageView) {
        // 获取微博单图
        String picUrlStr = status.bmiddle_pic;
        // 获取微博多图
        List<String> picUrlList = status.pic_urls;

        // 多图
        if (picUrlList != null && picUrlList.size() > 1) {
            layout.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            StatusPictureAdapter mPicAdapter = new StatusPictureAdapter(this, picUrlList);
            gridView.setAdapter(mPicAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent mIntent = new Intent(WeiboDetailActivity.this, ImageBrowserActivity.class);
                    mIntent.putExtra("status", status);
                    mIntent.putExtra("position", position);
                    startActivity(mIntent);
                }
            });

        } else if (!TextUtils.isEmpty(picUrlStr)) { // 单图
            layout.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            mImageLoader.loadImage(picUrlStr, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // 调整图片显示的宽高
                    int imageHeight = loadedImage.getHeight();
                    int imageWidth = loadedImage.getWidth();

                    if(imageHeight > imageWidth){
                        imageHeight = DisplayUtils.getScreenWidthPixels(WeiboDetailActivity.this)*3/5;
                        imageWidth = (DisplayUtils.getScreenWidthPixels(WeiboDetailActivity.this)*3/5)*3/4;
                    }else {
                        imageHeight = (DisplayUtils.getScreenWidthPixels(WeiboDetailActivity.this)*3/5)*3/4;
                        imageWidth = DisplayUtils.getScreenWidthPixels(WeiboDetailActivity.this)*3/5;
                    }

                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = imageHeight;
                    params.width = imageWidth;

                    imageView.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(WeiboDetailActivity.this, ImageBrowserActivity.class);
                    mIntent.putExtra("status", status);
                    startActivity(mIntent);
                }
            });
        } else { // 没有图片，则不显示该布局
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_WRITE_COMMENT:
                // 评论发送是否成功
                boolean sendComment = data.getBooleanExtra("is_send_comment", false);
                if (sendComment) {
                    scroll2Comment = true;
                    loadComments(1);
                }
                break;
            default:
                break;
        }
    }
}
