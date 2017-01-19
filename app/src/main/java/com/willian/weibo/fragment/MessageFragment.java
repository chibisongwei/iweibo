package com.willian.weibo.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.willian.weibo.R;
import com.willian.weibo.activity.AtStatusActivity;
import com.willian.weibo.activity.ChatActivity;
import com.willian.weibo.activity.FriendsActivity;
import com.willian.weibo.adapter.MessageAdapter;
import com.willian.weibo.bean.ChatGroup;
import com.willian.weibo.sql.ChatGroupDAO;
import com.willian.weibo.sql.ChatGroupDAOImpl;
import com.willian.weibo.utils.DisplayUtils;
import com.willian.weibo.utils.TitleBarBuilder;
import com.willian.weibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息界面
 */
public class MessageFragment extends Fragment {

    private View mView;

    private WrapHeightListView mListView;

    private MessageAdapter mItemAdapter;

    private List<ChatGroup> mItemList = new ArrayList<ChatGroup>();

    private PopupWindow mPopupWindow;

    private View rightView;

    private LinearLayout launchLayout;

    private LinearLayout privateLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, container, false);

        initView();

        initListView();

        setItemData();

        return mView;
    }

    private void initView() {
        new TitleBarBuilder(mView)
                .setLeftText(getResources().getString(R.string.find_group))
                .setTitleText(getResources().getText(R.string.tab_message).toString())
                .setRightImage(R.drawable.icon_newchat_selector)
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initPopupWindow();
                    }
                });
        rightView = mView.findViewById(R.id.iv_right);
    }

    private void initListView() {
        View headerView = View.inflate(getActivity(), R.layout.fragment_msg_header, null);
        LinearLayout atLayout = (LinearLayout) headerView.findViewById(R.id.layout_at);
        LinearLayout commentLayout = (LinearLayout) headerView.findViewById(R.id.layout_comment);
        LinearLayout attitudeLayout = (LinearLayout) headerView.findViewById(R.id.layout_attitude);

        mListView = (WrapHeightListView) mView.findViewById(R.id.lv_msg_items);
        mItemAdapter = new MessageAdapter(getActivity(), mItemList);
        mListView.setAdapter(mItemAdapter);
        mListView.addHeaderView(headerView);

        atLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 查看at我的微博
                Intent mIntent = new Intent(getActivity(), AtStatusActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 2) {
                    ChatGroup chatGroup = mItemList.get(position);
                    String groupName = chatGroup.getGroupName();
                    Intent mIntent = new Intent(getActivity(), ChatActivity.class);
                    mIntent.putExtra("groupName", groupName);
                    startActivity(mIntent);
                }
            }
        });
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        if (mPopupWindow == null) {
            View popView = View.inflate(getActivity(), R.layout.pop_chat_type, null);
            launchLayout = (LinearLayout) popView.findViewById(R.id.layout_launch);
            privateLayout = (LinearLayout) popView.findViewById(R.id.layout_private);
            mPopupWindow = new PopupWindow(popView, DisplayUtils.getScreenWidthPixels(getActivity()) * 2 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        // 设置弹出动画
        mPopupWindow.setAnimationStyle(R.style.popup_window_style);
        //设置弹出框的背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x90000000));
        // 位于某个View的正下方
        mPopupWindow.showAsDropDown(rightView, -50, 10);
        // 发起聊天
        launchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                // 关闭PopupWindow
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }
        });
        // 私密聊天
        privateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 关闭PopupWindow
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    private void setItemData() {
        ChatGroupDAO chatGroupDAO = new ChatGroupDAOImpl(getActivity());
        mItemList = chatGroupDAO.queryChatGroupList();
        if (mItemList != null && mItemList.size() > 0) {
            mItemAdapter.notifyDataSetChanged();
        }
    }
}
