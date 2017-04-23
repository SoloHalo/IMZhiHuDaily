package com.example.cql.imzhihudaily.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.ref.WeakReference;

/**
 * Created by CQL on 2017/3/5.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String NAME = "like";

    private static DBHelper INSTANCE;

    private static WeakReference<Context> mContextRef;

    public static DBHelper getInstance() {
        if (INSTANCE == null) {
            if (mContextRef == null) {
                throw new RuntimeException("you should call init");
            }
            Context context = mContextRef.get();
            if (context == null) {
                throw new RuntimeException("context should not be null");
            }
            INSTANCE = new DBHelper(context, NAME, null, VERSION);
        }
        return INSTANCE;
    }

    public static void init(Context context) {
        mContextRef = new WeakReference<>(context.getApplicationContext());
    }

    protected DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createLikeTable(db);
    }

    //创建数据库
    private void createLikeTable(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + NAME + "(" +
                "id integer primary key autoincrement," +
                "zhihu_id integer not null," +
                "title text not null," +
                "time integer not null," +
                "json_content text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + NAME);
        createLikeTable(db);
    }
}
