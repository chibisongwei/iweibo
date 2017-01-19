package com.willian.weibo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.willian.weibo.R;
import com.willian.weibo.adapter.ItemAdapter;
import com.willian.weibo.bean.ItemInfo;
import com.willian.weibo.widget.WrapHeightListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 搜索界面
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private View mView;

    private WrapHeightListView mListView;

    private ItemAdapter mItemAdapter;

    private List<ItemInfo> itemList = new ArrayList<ItemInfo>();

    private ViewPager mViewPager;

    private int[] imageRes;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private List<ImageView> imageList;

    private int curItem = 0;

    private ScheduledExecutorService executorService;
    // 是否开启自动轮播
    private boolean isAutoPlay = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(curItem);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);

        initView();

        setItem();

        return mView;
    }

    private void initView() {
        imageRes = new int[]{R.mipmap.auto_image1, R.mipmap.auto_image2, R.mipmap.auto_image3};

        imageList = new ArrayList<ImageView>();
        for (int i = 0; i < imageRes.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(imageRes[i]);
            imageList.add(imageView);
        }

        mListView = (WrapHeightListView) mView.findViewById(R.id.lv_search_items);
        mItemAdapter = new ItemAdapter(getActivity(), itemList);
        mListView.setAdapter(mItemAdapter);

        imageView1 = (ImageView) mView.findViewById(R.id.iv_res1);
        imageView2 = (ImageView) mView.findViewById(R.id.iv_res2);
        imageView3 = (ImageView) mView.findViewById(R.id.iv_res3);

        mViewPager = (ViewPager) mView.findViewById(R.id.vp_ads);
        mViewPager.setFocusable(true);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View mView = imageList.get(position);
                container.addView(mView);
                return mView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                View mView = imageList.get(position);
                container.removeView(mView);
            }
        });

        // 滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curItem = position;
                resetImages();
                switch (curItem) {
                    case 0:
                        imageView1.setImageResource(R.mipmap.page_highlighted);
                        break;
                    case 1:
                        imageView2.setImageResource(R.mipmap.page_highlighted);
                        break;
                    case 2:
                        imageView3.setImageResource(R.mipmap.page_highlighted);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    // 滑动结束，即切换完毕或者加载完毕
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (!isAutoPlay && mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1) {
                            mViewPager.setCurrentItem(0);
                        } else if (!isAutoPlay && mViewPager.getCurrentItem() == 0) {
                            mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
                        }
                        break;
                    // 手势滑动空闲中
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        isAutoPlay = false;
                        break;
                    // 界面切换中
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isAutoPlay = true;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isAutoPlay) {
            startRoll();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRoll();
    }

    /**
     * 开始滚动
     */
    private void startRoll(){
        executorService = Executors.newSingleThreadScheduledExecutor();
        // 实现自动轮播，每3秒切换一次图片
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (mViewPager) {
                    curItem = (curItem + 1) % imageList.size();
                    mHandler.obtainMessage().sendToTarget();
                }
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    /**
     *  停止滚动
     */
    private void stopRoll(){
        executorService.shutdown();
    }

    private void resetImages() {
        imageView1.setImageResource(R.mipmap.page);
        imageView2.setImageResource(R.mipmap.page);
        imageView3.setImageResource(R.mipmap.page);
    }

    private void setItem() {
        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_1, getResources().getText(R.string.new_friend).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_2, getResources().getText(R.string.new_task).toString(), "", true));

        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));

        itemList.add(new ItemInfo(true, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        itemList.add(new ItemInfo(false, R.mipmap.push_icon_app_small_3, getResources().getText(R.string.my_album).toString(), "", true));
        mItemAdapter.notifyDataSetChanged();
    }
}
