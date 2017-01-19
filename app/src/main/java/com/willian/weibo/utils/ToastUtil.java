package com.willian.weibo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 定义一个全局的Toast对象，这样可以避免连续显示Toast时不能取消上一次Toast消息的情况
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showToast(Context context, CharSequence text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
