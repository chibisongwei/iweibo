package com.willian.weibo.sql;

import com.willian.weibo.bean.ChatGroup;
import com.willian.weibo.bean.ChatItem;

import java.util.List;

/**
 * Created by willian on 2016/6/29.
 */
public interface ChatGroupDAO {

    /**
     * 新增聊天信息组
     *
     * @param chat
     */
    void addChatGroup(ChatGroup chat);

    /**
     * 根据聊天组名称查询
     * @param groupName
     * @return
     */
    ChatGroup queryChatGroupByName(String groupName);

    /**
     * 查询所有聊天信息组
     *
     * @return
     */
    List<ChatGroup> queryChatGroupList();
}
