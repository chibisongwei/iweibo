package com.willian.weibo.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.willian.weibo.bean.ChatGroup;
import com.willian.weibo.constant.DBConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by willian on 2016/6/29.
 */
public class ChatGroupDAOImpl implements ChatGroupDAO {

    private static final String[] COLUMN = {DBConstant.Column.GROUP_ID, DBConstant.Column.GROUP_NAME, DBConstant.Column.GROUP_AVATAR};

    private DatabaseHelper mDatabaseHelper;

    public ChatGroupDAOImpl(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void addChatGroup(ChatGroup chat) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBConstant.Column.GROUP_NAME, chat.getGroupName());
        values.put(DBConstant.Column.GROUP_AVATAR, chat.getGroupAvatar());
        db.insert(DBConstant.CHAT_GROUP, null, values);
        db.close();
    }

    @Override
    public ChatGroup queryChatGroupByName(String groupName) {
        ChatGroup chatGroup = null;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor mCursor = db.query(DBConstant.CHAT_GROUP, COLUMN, DBConstant.Column.GROUP_NAME + "=?", new String[]{groupName}, null, null, null);
        if (mCursor.moveToFirst()) {
            int groupId = mCursor.getInt(mCursor.getColumnIndex(DBConstant.Column.GROUP_ID));
            String groupAvatar = mCursor.getString(mCursor.getColumnIndex(DBConstant.Column.GROUP_AVATAR));
            chatGroup = new ChatGroup();
            chatGroup.setGroupId(groupId);
            chatGroup.setGroupName(groupName);
            chatGroup.setGroupAvatar(groupAvatar);
        }
        mCursor.close();
        db.close();
        return chatGroup;
    }


    @Override
    public List<ChatGroup> queryChatGroupList() {
        List<ChatGroup> groupList = new ArrayList<ChatGroup>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor mCursor = db.query(DBConstant.CHAT_GROUP, COLUMN, null, null, null, null, null);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                int groupId = mCursor.getInt(mCursor.getColumnIndex(DBConstant.Column.GROUP_ID));
                String groupName = mCursor.getString(mCursor.getColumnIndex(DBConstant.Column.GROUP_NAME));
                String groupAvatar = mCursor.getString(mCursor.getColumnIndex(DBConstant.Column.GROUP_AVATAR));
                ChatGroup chatGroup = new ChatGroup();
                chatGroup.setGroupId(groupId);
                chatGroup.setGroupName(groupName);
                chatGroup.setGroupAvatar(groupAvatar);
                groupList.add(chatGroup);
            }
        }
        mCursor.close();
        db.close();
        return groupList;
    }
}
