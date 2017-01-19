package com.willian.weibo.sql;

import com.willian.weibo.bean.ChatItem;

/**
 * Created by willian on 2016/6/30.
 */
public interface ChatItemDAO {

    /**
     * 添加聊天记录
     *
     * @param chatItem
     */
    void addChatItem(ChatItem chatItem);

    /**
     * 根据groupId查询聊天记录
     *
     * @param groupId
     * @return
     */
    ChatItem queryLastChatItem(int groupId);
}
