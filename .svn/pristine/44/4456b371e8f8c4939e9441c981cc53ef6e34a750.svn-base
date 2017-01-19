package com.willian.weibo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;
import com.willian.weibo.R;
import com.willian.weibo.adapter.StatusAdapter;
import com.willian.weibo.sdk.AccessTokenKeeper;
import com.willian.weibo.sdk.Constants;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private View mView;

    private View footView;

    private PullToRefreshListView mListView;
    /**
     * 用于获取微博信息流等操作的API
     */
    private StatusesAPI mStatusesAPI;
    /**
     * 当前页数
     */
    private int curPage = 1;

    private List<Status> mStatusList = new ArrayList<Status>();

    private StatusAdapter mStatusAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        initView();
        // 进入首页，默认获取当前用户关注的最新50条围脖
        loadWeibo(1);

        return mView;
    }

    private void initView() {
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY,
                mAccessToken);

        new TitleBarBuilder(mView)
                .setTitleText(getResources().getText(R.string.tab_home).toString())
                .setLeftImage(R.drawable.titlebar_friend_attention_selector)
                .setRightImage(R.drawable.titlebar_radar_selector)
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showToast(getActivity(), "Left", Toast.LENGTH_SHORT);
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showToast(getActivity(), "Right", Toast.LENGTH_SHORT);
                    }
                });
        footView = View.inflate(getActivity(), R.layout.listview_foot, null);
        mListView = (PullToRefreshListView) mView.findViewById(R.id.lv_home);
        mStatusAdapter = new StatusAdapter(getActivity(), mStatusList);
        mListView.setAdapter(mStatusAdapter);

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadWeibo(1);
            }
        });
        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadWeibo(curPage + 1);
            }
        });
    }

    private void loadWeibo(final int page) {

        mStatusesAPI.friendsTimeline(0L, 0L, 50, page, false, 0, false, new RequestListener() {

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
                        Toast.makeText(HomeFragment.this.getActivity(), response,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                LogUtil.e(TAG, e.getMessage());
                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                Toast.makeText(HomeFragment.this.getActivity(), info.toString(),
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

        if (curPage < statuses.total_number) {
            addFootView(mListView, footView);
        } else {
            removeFootView(mListView, footView);
        }

        mListView.onRefreshComplete();
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
}
