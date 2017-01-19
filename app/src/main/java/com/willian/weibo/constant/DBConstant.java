package com.willian.weibo.constant;

/**
 * Created by willian on 2016/6/30.
 */
public class DBConstant {
    // 数据库版本号
    public static final int DB_VERSION = 1;
    // 数据库名称
    public static final String DB_NAME = "weibo";
    // 聊天会话表
    public static final String CHAT_GROUP = "chat_group";
    // 聊天记录表
    public static final String CHAT_ITEM = "chat_item";

    public static final class Column {
        public static final String GROUP_ID = "id_group";
        public static final String GROUP_NAME = "group_name";
        public static final String GROUP_AVATAR = "group_avatar";

        public static final String CHAT_ITEM_ID = "id_chat_item";
        public static final String CHAT_GROUP_ID = "id_chat_group";
        public static final String CHAT_USER_NAME = "user_name";
        public static final String CHAT_USER_AVATAR = "avatar_url";
        public static final String CHAT_TYPE = "chat_type";
        public static final String IS_FROM = "is_from";
        public static final String CHAT_CONTENT = "chat_content";
        public static final String CHAT_TIME = "chat_time";
    }
}
