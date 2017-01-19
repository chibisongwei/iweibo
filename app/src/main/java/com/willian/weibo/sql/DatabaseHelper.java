package com.willian.weibo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.willian.weibo.constant.DBConstant;

/**
 * Created by willian on 2016/6/29.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_CHAT_GROUP = "create table if not exists " + DBConstant.CHAT_GROUP + "("
            + DBConstant.Column.GROUP_ID + " integer primary key autoincrement,"
            + DBConstant.Column.GROUP_NAME + " text,"
            + DBConstant.Column.GROUP_AVATAR + " text);";

    private static final String CREATE_CHAT_ITEM = "create table if not exists " + DBConstant.CHAT_ITEM + "("
            + DBConstant.Column.CHAT_ITEM_ID + " integer primary key autoincrement,"
            + DBConstant.Column.CHAT_GROUP_ID + " integer,"
            + DBConstant.Column.IS_FROM + " integer,"
            + DBConstant.Column.CHAT_TYPE + " integer,"
            + DBConstant.Column.CHAT_USER_NAME + " text,"
            + DBConstant.Column.CHAT_USER_AVATAR + " text,"
            + DBConstant.Column.CHAT_CONTENT + " text,"
            + DBConstant.Column.CHAT_TIME + " text);";

    public DatabaseHelper(Context context) {
        super(context, DBConstant.DB_NAME, null, DBConstant.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHAT_GROUP);
        db.execSQL(CREATE_CHAT_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CHAT_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CHAT_ITEM);
        onCreate(db);
    }
}
