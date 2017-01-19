package com.willian.weibo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.willian.weibo.R;
import com.willian.weibo.utils.EmotionUtil;

import java.util.List;

/**
 * Created by willian on 2016/4/21.
 */
public class EmotionAdapter extends BaseAdapter {

    private Context context;
    private List<String> emotionNames;
    private int itemWidth;

    public EmotionAdapter(Context context, List<String> emotionNames, int itemWidth) {
        this.context = context;
        this.emotionNames = emotionNames;
        this.itemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return emotionNames.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return emotionNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView emtionImage = new ImageView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemWidth, itemWidth);
        emtionImage.setPadding(itemWidth / 8, itemWidth / 8, itemWidth / 8, itemWidth / 8);
        emtionImage.setLayoutParams(params);
        // 末尾位置显示删除Icon
        if (position == getCount() - 1) {
            emtionImage.setImageResource(R.drawable.compose_emotion_delete_selector);
        } else {
            String emotionName = emotionNames.get(position);
            emtionImage.setImageResource(EmotionUtil.getImageByName(emotionName));
        }

        return emtionImage;
    }
}
