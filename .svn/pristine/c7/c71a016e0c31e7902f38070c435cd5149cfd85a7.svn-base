package com.willian.weibo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.willian.weibo.R;
import com.willian.weibo.utils.DateUtil;
import com.willian.weibo.utils.StringUtil;

import java.util.List;

/**
 * Created by willian on 2016/4/3.
 */
public class StatusCommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<Comment> mCommentList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public StatusCommentAdapter(Context mContext, List<Comment> mCommentList) {
        this.mCommentList = mCommentList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.commentAvatar = (ImageView) convertView.findViewById(R.id.iv_comment_avatar);
            viewHolder.commentVerified = (ImageView) convertView.findViewById(R.id.iv_comment_verified);
            viewHolder.commentUser = (TextView) convertView.findViewById(R.id.tv_comment_user);
            viewHolder.commentTime = (TextView) convertView.findViewById(R.id.tv_comment_time);
            viewHolder.commentContent = (TextView) convertView.findViewById(R.id.tv_comment_content);
            // 第二次绘制ListView时，直接从getTag()中取出
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment mComment = mCommentList.get(position);
        // 评论者图像URL地址
        String userImageUrl = mComment.user.avatar_large;

        mImageLoader.displayImage(userImageUrl, viewHolder.commentAvatar);
        // 是否认证
        boolean verified = mComment.user.verified;
        if (verified) {
            viewHolder.commentVerified.setVisibility(View.VISIBLE);
        } else {
            viewHolder.commentVerified.setVisibility(View.GONE);
        }
        // 评论者名称
        String userName = mComment.user.name;

        viewHolder.commentUser.setText(userName);
        // 评论时间
        String createdAt = mComment.created_at;
        // 格式化时间
        String createdTime = DateUtil.getPostDate(createdAt, viewHolder.commentTime);

        viewHolder.commentTime.setText(createdTime);

        String commentContent = mComment.text;

        viewHolder.commentContent.setText(StringUtil.getWeiboContent(mContext, viewHolder.commentContent, commentContent));

        return convertView;
    }

    public static class ViewHolder {
        public ImageView commentAvatar;
        public ImageView commentVerified;
        public TextView commentUser;
        public TextView commentTime;
        public TextView commentContent;
    }
}
