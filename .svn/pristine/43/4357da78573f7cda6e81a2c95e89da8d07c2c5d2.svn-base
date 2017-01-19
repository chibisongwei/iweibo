package com.willian.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.willian.weibo.R;
import com.willian.weibo.bean.ItemInfo;

import java.util.List;

/**
 * Item 适配器
 */
public class ItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<ItemInfo> mList;
    private LayoutInflater mInflater;

    public ItemAdapter(Context mContext, List<ItemInfo> mList) {
        this.mContext = mContext;
        this.mList = mList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_info, null);
            viewHolder = new ViewHolder();
            viewHolder.dividerView = convertView.findViewById(R.id.view_divider);
            viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.iv_item_image);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.tv_item_name);
            viewHolder.itemCaption = (TextView) convertView.findViewById(R.id.tv_item_caption);
            viewHolder.itemArrow = (ImageView) convertView.findViewById(R.id.iv_item_arrow);
            // 第二次绘制ListView时，直接从getTag()中取出
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemInfo mineItem = mList.get(position);

        if (mineItem.isShowDivider()) {
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.dividerView.setVisibility(View.GONE);
        }

        viewHolder.itemImage.setImageResource(mineItem.getItemImage());
        viewHolder.itemName.setText(mineItem.getItemName());
        viewHolder.itemCaption.setText(mineItem.getItemCaption());

        if (mineItem.isShowArrow()) {
            viewHolder.itemArrow.setVisibility(View.VISIBLE);
        } else {
            viewHolder.itemArrow.setVisibility(View.GONE);
        }

        return convertView;
    }

    public class ViewHolder {
        public View dividerView;
        public ImageView itemImage;
        public TextView itemName;
        public TextView itemCaption;
        public ImageView itemArrow;
    }
}
