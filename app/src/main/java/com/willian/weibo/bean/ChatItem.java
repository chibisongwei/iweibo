package com.willian.weibo.bean;

/**
 * 聊天记录
 */
public class ChatItem {

    private int chatItemId;
    // ChatGroup ID
    private int chatGroupId;
    // 聊天类型
    private int chatType;
    // 用户头像
    private String avatarUrl;
    // 用户昵称
    private String userName;
    // 聊天时间
    private String chatTime;
    // 聊天文本信息
    private String inputContent;
    // 聊天语音信息
    private Recorder recorder;

    private boolean isFrom;

    public int getChatItemId() {
        return chatItemId;
    }

    public void setChatItemId(int chatItemId) {
        this.chatItemId = chatItemId;
    }

    public int getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getInputContent() {
        return inputContent;
    }

    public void setInputContent(String inputContent) {
        this.inputContent = inputContent;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isFrom() {
        return isFrom;
    }

    public void setFrom(boolean from) {
        isFrom = from;
    }
}
