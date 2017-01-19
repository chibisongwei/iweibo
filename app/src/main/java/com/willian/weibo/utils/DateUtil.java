package com.willian.weibo.utils;

import android.graphics.Color;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间格式化处理
 */
public class DateUtil {

    public static final long ONE_MINUTE = 60 * 1000;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 获取微博发送时间
     *
     * @param dateStr
     * @return
     */
    public static String getPostDate(String dateStr, TextView textView) {

        String postDateStr = "";

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            // 微博发送时间
            Date postDate = sdf.parse(dateStr);
            // 当前时间
            Date currentDate = new Date();
            // 间隔时间
            long durTime = currentDate.getTime() - postDate.getTime();
            // 间隔天数
            int durDay = caculateDays(postDate, currentDate);
            // 当前时间3天以内发的微博，发送时间的字体设置为橙色
            if(durDay > -3 ){
                textView.setTextColor(Color.parseColor("#FFA500"));
            }else{
                textView.setTextColor(Color.parseColor("#232323"));
            }

            if (durTime <= 10 * ONE_MINUTE) {
                postDateStr = "刚刚";
            } else if (durTime < ONE_HOUR) {
                postDateStr = durTime / ONE_MINUTE + "分钟前";
            } else if (durDay == 0) {
                postDateStr = durTime / ONE_HOUR + "小时前";
            } else if (durDay == -1) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
                postDateStr = "昨天" + dateFormat.format(postDate);
            } else if (isSameYear(currentDate, postDate) && durDay < -1) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.CHINA);
                postDateStr = dateFormat.format(postDate);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
                postDateStr = dateFormat.format(postDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return postDateStr;
    }

    /**
     * 判断是否为同一年
     *
     * @param targetDate
     * @param compareDate
     * @return
     */
    public static boolean isSameYear(Date targetDate, Date compareDate) {
        Calendar mTargetCalendar = Calendar.getInstance();
        mTargetCalendar.setTime(targetDate);
        int targetYear = mTargetCalendar.get(Calendar.YEAR);

        Calendar mCompareCalendar = Calendar.getInstance();
        mCompareCalendar.setTime(compareDate);
        int compareYear = mCompareCalendar.get(Calendar.YEAR);

        return targetYear == compareYear;
    }

    /**
     * 计算间隔天数
     *
     * @param targetDate
     * @param compareDate
     * @return
     */
    public static int caculateDays(Date targetDate, Date compareDate) {
        Calendar mTargetCalendar = Calendar.getInstance();
        mTargetCalendar.setTime(targetDate);
        int targetDay = mTargetCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar mCompareCalendar = Calendar.getInstance();
        mCompareCalendar.setTime(compareDate);
        int compareDay = mCompareCalendar.get(Calendar.DAY_OF_YEAR);

        return targetDay - compareDay;
    }
}
