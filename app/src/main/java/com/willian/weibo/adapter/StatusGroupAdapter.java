package com.willian.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.willian.weibo.R;
import com.willian.weibo.bean.ItemInfo;

import java.util.List;

/**
 * 微博分组适配器
 */
public class StatusGroupAdapter extends BaseAdapter {

    private Context mContext;
    private List<ItemInfo> mItemList;
    private LayoutInflater mInflater;

    public StatusGroupAdapter(Context mContext, List<ItemInfo> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.listview_item_group, null);
            viewHolder.groupLayout = (LinearLayout) convertView.findViewById(R.id.layout_group_name);
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemInfo mItemInfo = (ItemInfo) getItem(position);
        if (mItemInfo.isSelected()) {
            viewHolder.groupLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_group_item_highlighted));
            viewHolder.groupName.setTextColor(mContext.getResources().getColor(R.color.text_orange));
        } else {
            viewHolder.groupLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_group_item));
            viewHolder.groupName.setTextColor(mContext.getResources().getColor(R.color.white));
        }
        viewHolder.groupName.setText(mItemInfo.getItemName());

        return convertView;
    }

    public static class ViewHolder {
        public LinearLayout groupLayout;
        public TextView groupName;
    }
}
