package com.lcsd.fstudc.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.lcsd.fstudc.utils.L;


/**
 * Created by Administrator on 2017/7/25.
 */
public class MySqliteHelper extends SQLiteOpenHelper {

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MySqliteHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        L.d("TAG", "--------onCreate----------");
        String sql = "create table " + Constant.TABLE_NAME + "(" + Constant.ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + Constant.TITLE + " text,"
                + Constant.TIME + " text," + Constant.STARTIME + " text,"
                + Constant.ENDTIME + " text," + Constant.SPONSOR + " text,"
                + Constant.ADDRESS + " text," + Constant.IMGS + " text,"
                + Constant.SYNOPSIS + " text)";
        try {
            sqLiteDatabase.execSQL(sql);
            L.d("TAG", "-------创建表成功---------");
        } catch (SQLiteException e) {
            L.d("TAG", "--------失败" + e.getMessage() + "--------------");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        L.d("TAG", "--------onUpgrade----------");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        L.d("TAG", "--------onOpen----------");
    }
}
