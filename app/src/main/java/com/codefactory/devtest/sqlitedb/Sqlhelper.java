package com.codefactory.devtest.sqlitedb;

/**
 * Created by kudlaty on 13/10/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Sqlhelper extends SQLiteOpenHelper {

    private Context c;
    private final static String TAG = Sqlhelper.class.getSimpleName();
    private final static String TAB_ACC = "accounts";
    private final static String SQL_DROP = "DROP TABLE IF EXISTS ";
    private static String DATABASE_NAME = "testowfnia1.db";
    private static int DATABASE_VERSION = 1;
    private static final List<String> DATABASE_CREATE_SQL = new  ArrayList<String> ();

    static{
        DATABASE_CREATE_SQL.add("create table "+TAB_ACC+" (id integer primary key autoincrement, userid string, username string); )");

    }

    public Sqlhelper(Context context, String name){
        super(context, dbName(name), null, DATABASE_VERSION);
        this.c=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "create db:::"+DATABASE_NAME);
        for(String s: DATABASE_CREATE_SQL){
            Log.d(TAG, "create table:::"+s);
            db.execSQL(s);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP + TAB_ACC);
        onCreate(db);

    }

    private static String dbName(String name){
        switch(name){
            case "test1": DATABASE_NAME = "festofnia1.db"; break;
            case "test2": DATABASE_NAME = "festofnia2.db"; break;
            case "test3": DATABASE_NAME = "festofnia3.db"; break;

        }
        return DATABASE_NAME;
    }

}
