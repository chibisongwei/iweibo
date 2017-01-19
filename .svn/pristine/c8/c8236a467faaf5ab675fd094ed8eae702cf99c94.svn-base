package com.willian.weibo.activity;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.willian.weibo.R;
import com.willian.weibo.adapter.StatusAdapter;
import com.willian.weibo.sdk.AccessTokenKeeper;
import com.willian.weibo.sdk.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息界面
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "UserInfoActivity";

    private boolean isCurrentUser;

    private User mUser;

    private View mTitleBar;

    private View bottomLine;

    private ImageView mBackImage;

    private TextView mTitleText;

    private View userInfoHeader;

    private ImageView coverImage;

    private ImageView userAvatarImage;

    private TextView userNameText;

    private TextView userFansText;

    private TextView userSignText;

    private TextView userFollowText;

    private View userInfoTab;

    private RadioButton homeButton;

    private RadioButton statusButton;

    private RadioButton albumButton;

    private View floatTab;

    private RadioButton floatHomeButton;

    private RadioButton floatStatusButton;

    private RadioButton floatAlbumButton;

    private View footerView;

    private PullToRefreshListView mPullListView;

    private StatusAdapter mStatusAdapter;

    private List<Status> mStatusList = new ArrayList<Status>();

    private Oauth2AccessToken mAccessToken;

    private UsersAPI mUsersAPI;

    private StatusesAPI mStatusesAPI;

    private String userName;

    private int curPage;

    // 背景图片最小高度
    private int minImageHeight = -1;
    // 背景图片最大高度
    private int maxImageHeight = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);

        mUsersAPI = new UsersAPI(this, Constants.APP_KEY, mAccessToken);
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY,
                mAccessToken);

        userName = getIntent().getStringExtra("userName");
        if (TextUtils.isEmpty(userName)) {
            isCurrentUser = true;
            mUser = mApplication.currentUser;
        }

        initView();

        loadData();
    }

    private void initView() {

        initTitleView();

        initUserInfo();

        initUserTab();

        initListView();

    }

    private void initTitleView() {
        mTitleBar = findViewById(R.id.layout_title_bar);
        bottomLine = findViewById(R.id.bottom_line);
        mBackImage = (ImageView) mTitleBar.findViewById(R.id.iv_back);
        mTitleText = (TextView) mTitleBar.findViewById(R.id.tv_title);

        mBackImage.setOnClickListener(this);
    }

    private void initUserInfo() {
        coverImage = (ImageView) findViewById(R.id.iv_cover);

        userInfoHeader = View.inflate(this, R.layout.user_info_header, null);
        userAvatarImage = (ImageView) userInfoHeader.findViewById(R.id.iv_avatar);
        userNameText = (TextView) userInfoHeader.findViewById(R.id.tv_name);
        userFollowText = (TextView) userInfoHeader.findViewById(R.id.tv_follows);
        userFansText = (TextView) userInfoHeader.findViewById(R.id.tv_fans);
        userSignText = (TextView) userInfoHeader.findViewById(R.id.tv_sign);
    }

    private void initUserTab() {
        // 悬浮显示的菜单栏
        floatTab = findViewById(R.id.tab_user_info);
        floatHomeButton = (RadioButton) floatTab.findViewById(R.id.rb_home);
        floatStatusButton = (RadioButton) floatTab.findViewById(R.id.rb_status);
        floatAlbumButton = (RadioButton) floatTab.findViewById(R.id.rb_album);
        // 添加到列表中的菜单栏
        userInfoTab = View.inflate(this, R.layout.include_user_info_tab, null);
        homeButton = (RadioButton) userInfoTab.findViewById(R.id.rb_home);
        statusButton = (RadioButton) userInfoTab.findViewById(R.id.rb_status);
        albumButton = (RadioButton) userInfoTab.findViewById(R.id.rb_album);
    }

    private void initListView() {
        mPullListView = (PullToRefreshListView) findViewById(R.id.plv_user_info);
        mStatusAdapter = new StatusAdapter(this, mStatusList);
        mPullListView.setAdapter(mStatusAdapter);

        ILoadingLayout loadingLayout = mPullListView.getLoadingLayoutProxy();
        loadingLayout.setPullLabel("");
        loadingLayout.setRefreshingLabel("");
        loadingLayout.setReleaseLabel("");
        loadingLayout.setLoadingDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));

        footerView = View.inflate(this, R.layout.listview_foot, null);

        ListView listView = mPullListView.getRefreshableView();
        listView.addHeaderView(userInfoHeader);
        listView.addHeaderView(userInfoTab);

        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadStatuses(1);
            }
        });

        mPullListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                ListView listView = mPullListView.getRefreshableView();
                if (listView.getFooterViewsCount() > 1) {
                    loadStatuses(curPage + 1);
                }
            }
        });

        mPullListView.setOnMyScrollListener(new PullToRefreshListView.OnMyScrollListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                int scrollY = t;

                // 初始化高度
                if (minImageHeight == -1) {
                    minImageHeight = coverImage.getHeight();
                }

                // 最大高度
                if (maxImageHeight == -1) {
                    Rect mRect = coverImage.getDrawable().getBounds();
                    maxImageHeight = mRect.bottom - mRect.top;
                }

                if (minImageHeight - scrollY < maxImageHeight) {
                    coverImage.layout(0, 0, coverImage.getWidth(), minImageHeight - scrollY);
                } else {
                    coverImage.layout(0, -scrollY - (maxImageHeight - minImageHeight), coverImage.getWidth(), -scrollY - (maxImageHeight - minImageHeight) + coverImage.getHeight());
                }
            }
        });

        // 滚动监听
        mPullListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                coverImage.layout(0, userInfoHeader.getTop(), coverImage.getWidth(), userInfoHeader.getTop() + coverImage.getHeight());
                if (userInfoHeader.getBottom() < mTitleBar.getBottom()) {
                    floatTab.setVisibility(View.VISIBLE);
                    mTitleBar.setBackgroundResource(R.color.white);
                    mTitleText.setVisibility(View.VISIBLE);
                    bottomLine.setVisibility(View.VISIBLE);
                } else {
                    floatTab.setVisibility(View.GONE);
                    mTitleBar.setBackgroundResource(R.color.transparent);
                    mTitleText.setVisibility(View.GONE);
                    bottomLine.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadData() {
        if (isCurrentUser) {
            // 如果是当前授权用户，则直接设置用户信息；
            setUserInfo(mUser);
        } else {
            // 如果是其他用户，则调用微博API接口查询；
            loadUserInfo();
        }
        // 加载当前用户对于的微博
        loadStatuses(1);
    }

    private void setUserInfo(User user) {
        mTitleText.setText(user.name);
        mImageLoader.displayImage(user.avatar_large, userAvatarImage);
        userNameText.setText(user.name);
        userFollowText.setText("" + user.friends_count);
        userFansText.setText("" + user.followers_count);
        userSignText.setText(user.remark);
    }

    private void loadUserInfo() {
        mUsersAPI.show(userName, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    User user = User.parse(response);
                    if (user != null) {
                        setUserInfo(user);
                    }
                } else {
                    Toast.makeText(UserInfoActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
    }

    /**
     * 此处只能获取当前客户的微博，目前接口API不支持获取指定用户的微博
     *
     * @param page
     */
    private void loadStatuses(final int page) {

        mStatusesAPI.userTimeline(0L, 0L, 0L, 50, page, false, 0, false, new RequestListener() {

            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    LogUtil.i(TAG, response);
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        if (page == 1) {
                            mStatusList.clear();
                        }
                        curPage = page;
                        // 加载更多数据
                        loadMoreData(statuses);
                    } else {
                        Toast.makeText(UserInfoActivity.this, response,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                LogUtil.e(TAG, e.getMessage());
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                Toast.makeText(UserInfoActivity.this, info.toString(),
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    private void loadMoreData(StatusList statuses) {
        for (Status mStatus : statuses.statusList) {
            if (!mStatusList.contains(mStatus)) {
                mStatusList.add(mStatus);
            }
        }
        mStatusAdapter.notifyDataSetChanged();
        // 由于API接口限制，最多只能获取5条微博
        /**
        if (mStatusList.size() < statuses.total_number) {
            addFootView(mPullListView, footerView);
        } else {
            removeFootView(mPullListView, footerView);
        }
        */
        mPullListView.onRefreshComplete();
    }

    private void addFootView(PullToRefreshListView pullListView, View footView) {
        ListView listView = pullListView.getRefreshableView();
        if (listView.getFooterViewsCount() == 1) {
            listView.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView pullListView, View footView) {
        ListView listView = pullListView.getRefreshableView();
        if (listView.getFooterViewsCount() > 1) {
            listView.removeFooterView(footView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_from_right);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                homeButton.setChecked(true);
                floatHomeButton.setChecked(true);
                break;
            case R.id.rb_status:
                statusButton.setChecked(true);
                floatStatusButton.setChecked(true);
                break;
            case R.id.rb_album:
                albumButton.setChecked(true);
                floatAlbumButton.setChecked(true);
                break;
            default:
                break;
        }
    }
}
