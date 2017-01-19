package com.willian.weibo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.willian.weibo.R;
import com.willian.weibo.bean.ChatGroup;
import com.willian.weibo.bean.ChatItem;
import com.willian.weibo.bean.ItemInfo;
import com.willian.weibo.sql.ChatItemDAO;
import com.willian.weibo.sql.ChatItemDAOImpl;

import java.util.List;

/**
 * Item 适配器
 */
public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatGroup> mMessageList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public MessageAdapter(Context context, List<ChatGroup> messageList) {
        this.mContext = context;
        this.mMessageList = messageList;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_msg_chat, null);
            viewHolder = new ViewHolder();
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.iv_chat_group_avatar);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.tv_chat_group_name);
            viewHolder.itemCaption = (TextView) convertView.findViewById(R.id.tv_chat_content);
            viewHolder.itemTime = (TextView) convertView.findViewById(R.id.tv_chat_group_time);
            // 第二次绘制ListView时，直接从getTag()中取出
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatGroup chatGroup = mMessageList.get(position);

        int groupId = chatGroup.getGroupId();
        String groupName = chatGroup.getGroupName();

        ChatItemDAO chatItemDAO = new ChatItemDAOImpl(mContext);
        ChatItem chatItem = chatItemDAO.queryLastChatItem(groupId);

        mImageLoader.displayImage(chatGroup.getGroupAvatar(), viewHolder.itemImage);
        viewHolder.itemName.setText(groupName);
        // 获取最新的聊天记录和时间
        viewHolder.itemCaption.setText(chatItem.getInputContent());
        viewHolder.itemTime.setText(chatItem.getChatTime());
        return convertView;
    }

    public static class ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView itemCaption;
        public TextView itemTime;
    }
}
