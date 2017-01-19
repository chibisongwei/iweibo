package com.willian.weibo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.willian.weibo.R;
import com.willian.weibo.activity.ImageBrowserActivity;
import com.willian.weibo.activity.StatusDetailActivity;
import com.willian.weibo.activity.UserInfoActivity;
import com.willian.weibo.activity.WriteCommentActivity;
import com.willian.weibo.activity.WriteStatusActivity;
import com.willian.weibo.utils.DateUtil;
import com.willian.weibo.utils.StringUtil;
import com.willian.weibo.utils.ToastUtil;
import com.willian.weibo.utils.WeiboUtil;

import java.util.List;

/**
 * 微博详情信息适配器
 */
public class AtStatusAdapter extends BaseAdapter {

    private Activity mContext;
    private List<Status> mStatusList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public AtStatusAdapter(Activity mContext, List<Status> mStatusList) {
        this.mContext = mContext;
        this.mStatusList = mStatusList;
        mInflater = LayoutInflater.from(mContext);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return mStatusList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStatusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_at_status, null);
            viewHolder = new ViewHolder();
            viewHolder.mStatusLayout = (LinearLayout) convertView.findViewById(R.id.layout_status);
            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.verifiedImage = (ImageView) convertView.findViewById(R.id.iv_verified);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.sendTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.statusSource = (TextView) convertView.findViewById(R.id.tv_source);

            viewHolder.statusContent = (TextView) convertView.findViewById(R.id.tv_status_content);
            viewHolder.statusImageLayout = (FrameLayout) convertView.findViewById(R.id.layout_status_image);
            viewHolder.statusImages = (GridView) viewHolder.statusImageLayout.findViewById(R.id.gv_status_image);
            viewHolder.statusSingleImage = (ImageView) viewHolder.statusImageLayout.findViewById(R.id.iv_status_image);

            viewHolder.cardLayout = (LinearLayout) convertView.findViewById(R.id.layout_status_retweet);
            viewHolder.cardImage = (ImageView) convertView.findViewById(R.id.iv_card_userhead);
            viewHolder.cardName = (TextView) convertView.findViewById(R.id.tv_card_username);
            viewHolder.cardContent = (TextView) convertView.findViewById(R.id.tv_card_content);

            viewHolder.retweetCount = (TextView) convertView.findViewById(R.id.tv_retweet_count);
            viewHolder.commentCount = (TextView) convertView.findViewById(R.id.tv_comment_count);
            viewHolder.attitudeCount = (TextView) convertView.findViewById(R.id.tv_like_count);

            viewHolder.retweetActionLayout = (LinearLayout) convertView.findViewById(R.id.layout_retweet);
            viewHolder.commentActionLayout = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            viewHolder.attitudeActionLayout = (LinearLayout) convertView.findViewById(R.id.layout_attitude);

            // 第二次绘制ListView时，直接从getTag()中取出
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Status mStatus = mStatusList.get(position);

        // 设置微博的信息
        setStatus(mStatus, viewHolder);

        // 设置转发微博的信息
        setRetweetStatus(mStatus, viewHolder);

        // 获取微博转发数
        final int retweet_count = mStatus.reposts_count;
        // 获取微博评论数
        final int comment_count = mStatus.comments_count;
        // 获取微博点赞数
        int attitude_count = mStatus.attitudes_count;

        viewHolder.retweetCount.setText(retweet_count == 0 ? mContext.getResources().getString(R.string.retweet) : WeiboUtil.getDisplayCount(retweet_count));
        viewHolder.commentCount.setText(comment_count == 0 ? mContext.getResources().getString(R.string.comment) : WeiboUtil.getDisplayCount(comment_count));
        viewHolder.attitudeCount.setText(attitude_count == 0 ? mContext.getResources().getString(R.string.attitude) : WeiboUtil.getDisplayCount(attitude_count));

        viewHolder.mStatusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, StatusDetailActivity.class);
                mIntent.putExtra("status", mStatus);
                mContext.startActivity(mIntent);
                mContext.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        viewHolder.retweetActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, WriteStatusActivity.class);
                mIntent.putExtra("status", mStatus);
                mContext.startActivity(mIntent);
                mContext.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        viewHolder.commentActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment_count > 0) {
                    Intent mIntent = new Intent(mContext, StatusDetailActivity.class);
                    mIntent.putExtra("status", mStatus);
                    mIntent.putExtra("scroll2Comment", true);
                    mContext.startActivity(mIntent);
                    mContext.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                } else {
                    // 如果还没有评论，则跳转至评论发表界面
                    Intent mIntent = new Intent(mContext, WriteCommentActivity.class);
                    mIntent.putExtra("status", mStatus);
                    mContext.startActivity(mIntent);
                    mContext.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                }
            }
        });

        viewHolder.attitudeActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(mContext, "Like", Toast.LENGTH_SHORT);
            }
        });

        // 点击用户头像
        viewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, UserInfoActivity.class);
                mIntent.putExtra("userName", mStatus.user.name);
                mContext.startActivity(mIntent);
                mContext.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        return convertView;
    }

    /**
     * 设置微博信息
     *
     * @param mStatus
     * @param viewHolder
     */
    private void setStatus(Status mStatus, ViewHolder viewHolder) {

        // 获取围脖发布者的头像
        String userImageUrl = mStatus.user.avatar_large;
        // 获取微博发布者的ID
        String userName = mStatus.user.name;
        // 是否认证
        boolean verified = mStatus.user.verified;

        mImageLoader.displayImage(userImageUrl, viewHolder.userImage);
        viewHolder.userName.setText(userName);

        if (verified) {
            viewHolder.verifiedImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.verifiedImage.setVisibility(View.GONE);
        }

        // 获取微博发送时间
        String createdAt = mStatus.created_at;
        // 格式化时间
        String createdTime = DateUtil.getPostDate(createdAt, viewHolder.sendTime);

        viewHolder.sendTime.setText(createdTime);

        String sourceName = "";

        if (!TextUtils.isEmpty(mStatus.source)) {
            sourceName = mContext.getResources().getString(R.string.weibo_from) + " " + Html.fromHtml(mStatus.source);
        }
        viewHolder.statusSource.setText(sourceName);

        // 获取微博内容
        viewHolder.statusContent.setText(StringUtil.getWeiboContent(mContext, viewHolder.statusContent, mStatus.text));

        setImages(mStatus, viewHolder.statusImageLayout, viewHolder.statusImages, viewHolder.statusSingleImage);
    }

    /**
     * 设置转发微博信息
     *
     * @param status
     * @param viewHolder
     */
    private void setRetweetStatus(Status status, ViewHolder viewHolder) {

        // 获取转发的微博
        Status retweetStatus = status.retweeted_status;

        if (retweetStatus != null) {
            // 设置转发图片内容
            List<String> imageUrls = retweetStatus.pic_urls;
            if (imageUrls != null && imageUrls.size() > 0) {
                // 取第一张图片作为转发图片
                mImageLoader.displayImage(imageUrls.get(0), viewHolder.cardImage);
            } else {
                // 设置用户头像为转发图片
                String userHeadUrl = retweetStatus.user.avatar_large;
                mImageLoader.displayImage(userHeadUrl, viewHolder.cardImage);
            }

            // 设置转发文字内容
            viewHolder.cardName.setText("@" + retweetStatus.user.name);
            viewHolder.cardContent.setText(StringUtil.getWeiboContent(mContext, viewHolder.cardContent, retweetStatus.text));
            viewHolder.cardLayout.setVisibility(View.VISIBLE);
        }else{
            viewHolder.cardLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 微博图片设置
     *
     * @param status
     * @param layout
     * @param gridView
     * @param imageView
     */
    private void setImages(final Status status, FrameLayout layout, GridView gridView, ImageView imageView) {
        // 获取微博中质量的单图
        String picUrlStr = status.bmiddle_pic;
        // 获取微博多图，此处获取的为缩略图
        List<String> picUrlList = status.pic_urls;

        // 多图
        if (picUrlList != null && picUrlList.size() > 1) {
            layout.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            StatusPictureAdapter mPicAdapter = new StatusPictureAdapter(mContext, picUrlList);
            gridView.setAdapter(mPicAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent mIntent = new Intent(mContext, ImageBrowserActivity.class);
                    mIntent.putExtra("status", status);
                    mIntent.putExtra("position", position);
                    mContext.startActivity(mIntent);
                }
            });

        } else if (!TextUtils.isEmpty(picUrlStr)) { // 单图
            layout.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            mImageLoader.displayImage(picUrlStr, imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(mContext, ImageBrowserActivity.class);
                    mIntent.putExtra("status", status);
                    mContext.startActivity(mIntent);
                }
            });
        } else { // 没有图片，则不显示该布局
            layout.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder {
        public LinearLayout mStatusLayout;

        public ImageView userImage;
        public ImageView verifiedImage;
        public TextView userName;
        public TextView sendTime;
        public TextView statusSource;

        public TextView statusContent;
        public FrameLayout statusImageLayout;
        public GridView statusImages;
        public ImageView statusSingleImage;

        public LinearLayout cardLayout;
        public ImageView cardImage;
        public TextView cardName;
        public TextView cardContent;

        public LinearLayout retweetActionLayout;
        public LinearLayout commentActionLayout;
        public LinearLayout attitudeActionLayout;
        public TextView retweetCount;
        public TextView commentCount;
        public TextView attitudeCount;
    }
}
