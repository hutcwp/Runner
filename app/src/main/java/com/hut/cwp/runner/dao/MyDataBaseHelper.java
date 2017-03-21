package com.hut.cwp.runner.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Created by Adminis on 2017/3/20.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context mContext ;
    public static final String CREATE_DATA = "create table RUN_DATA ("
            +"id integer primary key autoincrement,"
            +"date text, "
            +"time text, "
            +"alltime float,"
            +"calorie float,"
            +"vector float,"
            +"distance float)";

    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_DATA);
        Toasty.info(mContext,"数据库创建成功", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
