package com.willian.weibo.bean;

/**
 * 聊天会话
 */
public class ChatGroup {

    private int groupId;
    // 聊天对象
    private String groupName;
    // 头像URL
    private String groupAvatar;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }
}
