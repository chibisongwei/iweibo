package com.willian.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.willian.weibo.R;
import com.willian.weibo.bean.Contact;
import com.willian.weibo.utils.ImageOptionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人适配器
 */
public class FriendsAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;

    private List<Contact> mContactList;
    // 选择的联系人
    private List<Contact> mSelectedList = new ArrayList<Contact>();
    // 每个排序字母在ListView中的位置
    private Map<String, Integer> mLetterSection = new HashMap<String, Integer>();

    public FriendsAdapter(Context mContext, List<Contact> mContactList, List<String> mLetterList) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mContactList = mContactList;
        this.mImageLoader = ImageLoader.getInstance();
        // 获取每个联系人分组的字母索引在ListView中的位置
        for (int i = 0; i < mLetterList.size(); i++) {
            for (int j = 0; j < mContactList.size(); j++) {
                if (mContactList.get(j).getSortLetter().equals(mLetterList.get(i))) {
                    mLetterSection.put(mLetterList.get(i), j);
                    break;
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mContactList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_friends, null);
            viewHolder = new ViewHolder();
            viewHolder.letterLayout = (LinearLayout) convertView.findViewById(R.id.layout_letter);
            viewHolder.letter = (TextView) convertView.findViewById(R.id.tv_letter);
            viewHolder.friendLayout = (LinearLayout) convertView.findViewById(R.id.layout_friend);
            viewHolder.userCheck = (CheckBox) convertView.findViewById(R.id.cb_friend);
            viewHolder.userAvatar = (ImageView) convertView.findViewById(R.id.iv_friend);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_friend);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Contact curContact = (Contact) getItem(position);
        String curLetter = curContact.getSortLetter();
        String preLetter = (position - 1) >= 0 ? mContactList.get(position - 1).getSortLetter() : "";
        if (!preLetter.equals(curLetter)) {
            viewHolder.letterLayout.setVisibility(View.VISIBLE);
            viewHolder.letter.setText(curContact.getSortLetter());
        } else {
            viewHolder.letterLayout.setVisibility(View.GONE);
        }

        viewHolder.userName.setText(curContact.getUserName());
        mImageLoader.displayImage(curContact.getHeadUrl(), viewHolder.userAvatar, ImageOptionHelper.getAvatarOptions());

        final ViewHolder finalViewHolder = viewHolder;
        final Button chatButton = (Button) parent.getRootView().findViewById(R.id.btn_right);
        // 点击Item，则选择CheckBox
        viewHolder.friendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果CheckBox没有被选中，则选中；
                if (!finalViewHolder.userCheck.isChecked()) {
                    mSelectedList.add(curContact);
                    finalViewHolder.userCheck.setChecked(true);
                    chatButton.setEnabled(true);
                    chatButton.setText(mContext.getResources().getString(R.string.chat) + "(" + mSelectedList.size() + ")");
                    chatButton.setBackgroundResource(R.drawable.common_button_orange);
                } else {
                    // 如果CheckBox已被选中，则不选中；
                    finalViewHolder.userCheck.setChecked(false);
                    mSelectedList.remove(curContact);
                    if (mSelectedList.size() > 0) {
                        chatButton.setEnabled(true);
                        chatButton.setText(mContext.getResources().getString(R.string.chat) + "(" + mSelectedList.size() + ")");
                        chatButton.setBackgroundResource(R.drawable.common_button_orange);
                    } else {
                        chatButton.setEnabled(false);
                        chatButton.setText(mContext.getResources().getString(R.string.chat));
                        chatButton.setBackgroundResource(R.drawable.common_button_white_disable);
                    }
                }
            }
        });

        return convertView;
    }

    /**
     * 获取选择的联系人
     *
     * @return
     */
    public List<Contact> getFriendList() {
        return mSelectedList;
    }

    /**
     * 获取字母索引-位置集合
     *
     * @return
     */
    public Map<String, Integer> getLetterSection() {
        return mLetterSection;
    }

    public static class ViewHolder {
        public LinearLayout letterLayout;
        public TextView letter;
        public LinearLayout friendLayout;
        public CheckBox userCheck;
        public ImageView userAvatar;
        public TextView userName;
    }
}
