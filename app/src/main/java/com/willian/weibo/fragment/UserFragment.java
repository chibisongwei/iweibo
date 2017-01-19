package com.willian.weibo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.willian.weibo.R;
import com.willian.weibo.activity.MyStatusActivity;
import com.willian.weibo.activity.UserInfoActivity;
import com.willian.weibo.adapter.ItemAdapter;
import com.willian.weibo.bean.ItemInfo;
import com.willian.weibo.sdk.AccessTokenKeeper;
import com.willian.weibo.sdk.Constants;
import com.willian.weibo.utils.BaseApplication;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.utils.ToastUtil;
import com.willian.weibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息界面
 */
public class UserFragment extends Fragment {

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 用户信息接口
     */
    private UsersAPI mUsersAPI;

    private ImageLoader mImageLoader;

    private View mView;

    private ImageView mineAvatar;

    private TextView mineName;

    private TextView mineCaption;

    private TextView statusCountText;

    private TextView followCountText;

    private TextView fansCountText;

    private LinearLayout mStatusCountLayout;

    private WrapHeightListView mItemListView;

    private ItemAdapter mMineAdapter;

    private List<ItemInfo> itemList = new ArrayList<ItemInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_user, container, false);
        // 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
        mUsersAPI = new UsersAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        mImageLoader = ImageLoader.getInstance();
        // 初始化View
        initView();
        // 设置Item信息
        setItem();
        // 处理点击事件
        handleAction();

        loadUserInfo();

        return mView;
    }

    private void handleAction() {
        // 点击用户头像
        mineAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(mIntent);
            }
        });
        // 点击微博数
        mStatusCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), MyStatusActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
    }


    private void initView() {
        new TitleBarBuilder(mView)
                .setTitleText(getResources().getString(R.string.tab_user))
                .setLeftText(getResources().getString(R.string.add_friend))
                .setRightText(getResources().getString(R.string.setting))
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

        mineAvatar = (ImageView) mView.findViewById(R.id.iv_mine_avatar);
        mineName = (TextView) mView.findViewById(R.id.tv_mine_username);
        mineCaption = (TextView) mView.findViewById(R.id.tv_mine_caption);

        statusCountText = (TextView) mView.findViewById(R.id.tv_status_count);
        followCountText = (TextView) mView.findViewById(R.id.tv_follow_count);
        fansCountText = (TextView) mView.findViewById(R.id.tv_fans_count);

        mStatusCountLayout = (LinearLayout) mView.findViewById(R.id.layout_status_count);

        mItemListView = (WrapHeightListView) mView.findViewById(R.id.lv_mine_items);
        mMineAdapter = new ItemAdapter(getActivity(), itemList);
        mItemListView.setAdapter(mMineAdapter);
    }

    /**
     * 加载用户数据
     */
    private void loadUserInfo() {
        // 加载用户信息
        long uid = Long.parseLong(mAccessToken.getUid());
        mUsersAPI.show(uid, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    User user = User.parse(response);
                    if (user != null) {
                        BaseApplication mApplication = (BaseApplication) getActivity().getApplication();
                        mApplication.currentUser = user;
                        setUserInfo(user);
                    }
                } else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
    }

    private void setUserInfo(User user) {
        // 用户头像
        String avatarUrl = user.avatar_large;
        // 用户昵称
        String userName = user.screen_name;
        // 用户简介
        String userCaption = user.description;
        // 微博数
        int statusCounts = user.statuses_count;
        // 关注数
        int followCounts = user.friends_count;
        // 粉丝数
        int fansCounts = user.followers_count;

        mineName.setText(userName);
        if (!TextUtils.isEmpty(userCaption)) {
            mineCaption.setText(getResources().getString(R.string.profile) + userCaption);
        } else {
            mineCaption.setText(getResources().getString(R.string.profile) + getResources().getString(R.string.profile_null));
        }
        mImageLoader.displayImage(avatarUrl, mineAvatar);
        statusCountText.setText("" + statusCounts);
        followCountText.setText("" + followCounts);
        fansCountText.setText("" + fansCounts);
    }

    private void setItem() {
        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_1, getResources().getString(R.string.new_friend), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_2, getResources().getString(R.string.new_task), getResources().getString(R.string.new_task_caption), true));
        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_3, getResources().getString(R.string.my_album), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_4, getResources().getString(R.string.my_attitude), "", true));
        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_5, getResources().getString(R.string.weibo_pay), getResources().getString(R.string.weibo_pay_caption), true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_4, getResources().getString(R.string.weibo_sport), getResources().getString(R.string.weibo_sport_caption), true));
        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_3, getResources().getString(R.string.draft), "", true));
        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_2, getResources().getString(R.string.more), getResources().getString(R.string.more_caption), true));
        mMineAdapter.notifyDataSetChanged();
    }
}
