package com.willian.weibo.bean;

import java.io.Serializable;

/**
 * 联系人
 */
public class Contact implements Serializable{
    // 用户头像URL
    private String headUrl;
    // 用户名
    private String userName;
    // 排序字母
    private String sortLetter;

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSortLetter() {
        return sortLetter;
    }

    public void setSortLetter(String sortLetter) {
        this.sortLetter = sortLetter;
    }
}
