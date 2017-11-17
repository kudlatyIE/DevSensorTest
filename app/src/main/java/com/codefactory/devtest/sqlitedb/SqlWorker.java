package com.codefactory.devtest.sqlitedb;

/**
 * Created by kudlaty on 13/10/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SqlWorker {

    private final static String TAG = SqlWorker.class.getSimpleName();
    private final static String DATE = "date", TIME = "time",BIO = "bioScore", GEST = "gestScore", RESULT = "result";
    private static String dbName;
    /**
     * hold table name for logs of userIDs (table name = userID)
     */
    private static String TAB_USER_LOGS;//table name == user ID
    private static String date, time;
    private static double bio, gest;
    private static boolean result;
    private Context c;
    private SQLiteDatabase db;
    private Sqlhelper helper;
    private static final String ADD_USER_TAB = "create table if not exists "+TAB_USER_LOGS+" (id integer primary key autoincrement, "+DATE+" string, "+TIME+" string, "
            + BIO+" double, "+GEST+" double, "+RESULT+" boolean); )",
            REMOVE_USER_TAB = "drop table "+TAB_USER_LOGS,
            ADD_LOG_SQL = "insert into "+TAB_USER_LOGS+" values("+date+", "+time+", "+bio+", "+gest+", "+result+")";



    public SqlWorker(Context context, String name){
        this.c=context;
        this.dbName=name;
        this.helper = new Sqlhelper(context, name);
    }

    public void open(){
        this.db = helper.getReadableDatabase();
        Log.d(TAG, "opening db: "+db.getPath());
    }

    public void close(){
        this.helper.close();
    }

    public String getDbPath(){
        return this.c.getDatabasePath(helper.getDatabaseName()).getAbsolutePath().toString();
    }

    public String getDbName(){
        return helper.getDatabaseName();
    }
    public boolean createUser(String userId, double bio, double gest, boolean loginResult) throws Exception{
        if(userId!=null) SqlWorker.TAB_USER_LOGS = userId;
        else throw new Exception("empty user ID!");

        Cursor c = db.rawQuery("select DISTINCT tbl_name from sqlite_master where type='table' and tbl_name = '"+TAB_USER_LOGS+"'", null);
        if(c!=null){
            if(c.getCount()>0) throw new Exception ("table name exist!");

        }
        c.close();
        getDateTimeNow();
        SqlWorker.bio=bio;
        SqlWorker.gest=gest;
        SqlWorker.result=loginResult;

        db.execSQL(ADD_USER_TAB);


        return true;
    }

    public String getDateTimeNow(){
        Date d = new Date();
        SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm:ss");
        SqlWorker.date = dateF.format(d);
        SqlWorker.time = timeF.format(d);
        String s = SqlWorker.date+" "+SqlWorker.time;
        Log.d(TAG, "full date time: "+s);
        return s;
    }



}
