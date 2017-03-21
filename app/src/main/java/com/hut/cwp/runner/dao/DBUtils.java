package com.hut.cwp.runner.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Adminis on 2017/3/20.
 */

public class DBUtils {

    private SQLiteDatabase db;
    private MyDataBaseHelper helper;

    private Context mContext;

    private static DBUtils instance = null;

    private DBUtils(Context mContext) {

        this.mContext = mContext;

        helper = new MyDataBaseHelper(mContext, "RunData.db", null, 1);

        db = helper.getWritableDatabase();
    }

    public static DBUtils getInstance(Context context) {

        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }


    public void insert(RunData runData) {

        if (checkDataValid(runData)) {

            ContentValues values = new ContentValues();
            values.put("time", runData.getTime());
            values.put("date", runData.getDate());
            values.put("alltime", runData.getAlltime());
            values.put("calorie", runData.getCalorie());
            values.put("vector", runData.getVector());
            values.put("distance", runData.getDistance());
            db.insert("RUN_DATA", null, values);

            Toasty.normal(mContext, "数据已经保存至数据库", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkDataValid(RunData data) {

        if (data == null) {
            Toasty.error(mContext, "数据保存失败,runData为空").show();
        }
        if (data.getDistance() <= 0) {
            Toasty.error(mContext, "数据不符合，路程 ： " + data.getDistance()).show();
            return false;
        }

        return true;
    }


    public List<RunData> select() {

        List datas = new ArrayList();

        Cursor cursor = db.query("RUN_DATA", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                Float alltime = cursor.getFloat(cursor.getColumnIndex("alltime"));
                Float calorie = cursor.getFloat(cursor.getColumnIndex("calorie"));
                Float vector = cursor.getFloat(cursor.getColumnIndex("vector"));
                Float distance = cursor.getFloat(cursor.getColumnIndex("distance"));


                RunData runData = new RunData();
                runData.setId(id);
                runData.setDate(date);
                runData.setTime(time);
                runData.setAlltime(alltime);
                runData.setCalorie(calorie);
                runData.setVector(vector);
                runData.setDistance(distance);

                datas.add(runData);

                Log.i("MyTAG", "id :" + id);
                Log.i("MyTAG", "date :" + date);
                Log.i("MyTAG", "time:" + time);
                Log.i("MyTAG", "alltime:" + alltime);
                Log.i("MyTAG", "calorie:" + calorie);
                Log.i("MyTAG", "vector:" + vector);
                Log.i("MyTAG", "distance:" + distance);

            } while (cursor.moveToNext());
        }

        return datas;
    }
}
