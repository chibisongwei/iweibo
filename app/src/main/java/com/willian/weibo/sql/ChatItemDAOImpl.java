package com.willian.weibo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.willian.weibo.bean.ChatItem;
import com.willian.weibo.constant.DBConstant;

/**
 * Created by willian on 2016/6/30.
 */
public class ChatItemDAOImpl implements ChatItemDAO {

    private static final String[] COLUMN = {DBConstant.Column.CHAT_CONTENT, DBConstant.Column.CHAT_TIME};

    private DatabaseHelper mDatabaseHelper;

    public ChatItemDAOImpl(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void addChatItem(ChatItem chatItem) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.Column.CHAT_GROUP_ID, chatItem.getChatGroupId());
        values.put(DBConstant.Column.CHAT_TYPE, chatItem.getChatType());
        values.put(DBConstant.Column.IS_FROM, chatItem.isFrom());
        values.put(DBConstant.Column.CHAT_USER_NAME, chatItem.getUserName());
        values.put(DBConstant.Column.CHAT_USER_AVATAR, chatItem.getAvatarUrl());
        values.put(DBConstant.Column.CHAT_CONTENT, chatItem.getInputContent());
        values.put(DBConstant.Column.CHAT_TIME, chatItem.getChatTime());
        db.insert(DBConstant.CHAT_ITEM, null, values);
        db.close();
    }

    @Override
    public ChatItem queryLastChatItem(int groupId) {
        ChatItem chatItem = null;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor mCursor = db.query(DBConstant.CHAT_ITEM, COLUMN, DBConstant.Column.CHAT_GROUP_ID + "=?", new String[]{String.valueOf(groupId)}, null, null, DBConstant.Column.CHAT_TIME);
        if (mCursor.isFirst()) {
            chatItem = new ChatItem();
            String lastMsg = mCursor.getString(mCursor.getColumnIndex(DBConstant.Column.CHAT_CONTENT));
            String lastTime = mCursor.getString(mCursor.getColumnIndex(DBConstant.Column.CHAT_TIME));
            chatItem.setInputContent(lastMsg);
            chatItem.setChatTime(lastTime);
        }
        mCursor.close();
        db.close();
        return chatItem;
    }
}
