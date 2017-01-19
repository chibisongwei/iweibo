package com.willian.weibo.utils;

import com.willian.weibo.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 表情资源
 */
public class EmotionUtil implements Serializable {

    public static Map<String, Integer> emotionMap;

    /**
     * 静态代码块是自动执行的，静态方法是被调用的时候才执行；
     */
    static {
        emotionMap = new HashMap<String, Integer>();
        emotionMap.put("[害羞]", R.mipmap.d_haixiu);
        emotionMap.put("[哈哈]", R.mipmap.d_haha);
        emotionMap.put("[呵呵]", R.mipmap.d_hehe);
        emotionMap.put("[嘻嘻]", R.mipmap.d_xixi);
        emotionMap.put("[doge]", R.mipmap.d_doge);
        emotionMap.put("[汗]", R.mipmap.d_han);
        emotionMap.put("[衰]", R.mipmap.d_shuai);
        emotionMap.put("[阴险]", R.mipmap.d_yinxian);
    }

    /**
     * 根据名称获取对应的图片资源
     *
     * @param imageName
     * @return
     */
    public static int getImageByName(String imageName) {
        Integer integer = emotionMap.get(imageName);
        return integer == null ? -1 : integer;
    }
}
