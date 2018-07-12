package com.lcsd.fstudc.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 */
public class DbManager {
    private static MySqliteHelper helper;

    public static MySqliteHelper getIntance(Context context) {
        if (helper == null) {
            helper = new MySqliteHelper(context);
        }
        return helper;
    }

    /**
     * 根据sql语句查询cursor对象
     */
    public static Cursor selectDataBySql(SQLiteDatabase db, String sql, String[] selectionArgs) {
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(sql, selectionArgs);
        }
        return cursor;
    }

    /**
     * 将Cursor转化为List
     */
    public static List<ImageLive> cursorToList(Cursor cursor) {
        List<ImageLive> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ImageLive imageLive = new ImageLive();
            imageLive.setId(cursor.getInt(cursor.getColumnIndex(Constant.ID)));
            imageLive.setTitle(cursor.getString(cursor.getColumnIndex(Constant.TITLE)));
            imageLive.setTime(cursor.getString(cursor.getColumnIndex(Constant.TIME)));
            imageLive.setStartime(cursor.getString(cursor.getColumnIndex(Constant.STARTIME)));
            imageLive.setEndtime(cursor.getString(cursor.getColumnIndex(Constant.ENDTIME)));
            imageLive.setSponsor(cursor.getString(cursor.getColumnIndex(Constant.SPONSOR)));
            imageLive.setAddress(cursor.getString(cursor.getColumnIndex(Constant.ADDRESS)));
            imageLive.setImgs(cursor.getString(cursor.getColumnIndex(Constant.IMGS)));
            imageLive.setSynopsis(cursor.getString(cursor.getColumnIndex(Constant.SYNOPSIS)));
            list.add(imageLive);
        }
        return list;
    }
}
