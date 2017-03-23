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

        helper = new MyDataBaseHelper(mContext, "RunDailyData.db", null, 1);

        db = helper.getWritableDatabase();
    }

    public static DBUtils getInstance(Context context) {

        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }


    public void upDateMouthTable(RunDailyData data) {

        Cursor cursor = db.query("RUN_Mouth_DATA", null, null, null, null, null, null, null);


        if (cursor.moveToFirst()) {

            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                Log.i("MyTAG1",date);
                Log.i("MyTAG1",data.getDate().equals(date)+"");

                if (data.getDate().equals(date)) {

                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    float alltime = cursor.getFloat(cursor.getColumnIndex("alltime")) + data.getSpendtime();
                    float allcalorie = cursor.getFloat(cursor.getColumnIndex("allcalorie")) + data.getCalorie();
                    float allvector = cursor.getFloat(cursor.getColumnIndex("allvector")) + data.getVector();
                    float alldistance = cursor.getFloat(cursor.getColumnIndex("alldistance")) + data.getDistance();

                    ContentValues values = new ContentValues();
                    values.put("alltime", alltime);
                    values.put("allcalorie", allcalorie);
                    values.put("allvector", allvector);
                    values.put("alldistance", alldistance);
                    db.update("RUN_Mouth_DATA", values, "id=?", new String[]{id + ""});

                    Log.i("MyTAG1", "mouth数据更新" + "id: " + id);
                    return;
                }

            } while (cursor.moveToNext());
        }

        insertToMouthTable(data);

    }

    public void insertToMouthTable(RunDailyData data) {

        if (data != null) {
            ContentValues values = new ContentValues();

            values.put("date", data.getDate());
            values.put("alltime", data.getSpendtime());
            values.put("allcalorie", data.getCalorie());
            values.put("allvector", data.getVector());
            values.put("alldistance", data.getDistance());

            db.insert("RUN_Mouth_DATA", null, values);

            Log.i("MyTAG1", "插入数据成功");
        }
    }


    public List<RunMouthData> selectFromMouth() {

        List datas = new ArrayList();

        Cursor cursor = db.query("RUN_Mouth_DATA", null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                String date = cursor.getString(cursor.getColumnIndex("date"));
                Float alltime = cursor.getFloat(cursor.getColumnIndex("alltime"));
                Float allcalorie = cursor.getFloat(cursor.getColumnIndex("allcalorie"));
                Float allvector = cursor.getFloat(cursor.getColumnIndex("allvector"));
                Float alldistance = cursor.getFloat(cursor.getColumnIndex("alldistance"));


                RunMouthData runMouthData = new RunMouthData();
                runMouthData.setId(id);
                runMouthData.setDate(date);
                runMouthData.setAlltime(alltime);
                runMouthData.setAllcalorie(allcalorie);
                runMouthData.setAllvector(allvector);
                runMouthData.setAlldistance(alldistance);


                datas.add(runMouthData);

                Log.i("MyTAG", "id :" + id);
                Log.i("MyTAG", "date :" + date);
                Log.i("MyTAG", "alltime:" + alltime);
                Log.i("MyTAG", "allcalorie:" + allcalorie);
                Log.i("MyTAG", "allvector:" + allvector);
                Log.i("MyTAG", "alldistance:" + alldistance);

            } while (cursor.moveToNext());
        }

        return datas;
    }

    public void insertToDailyTable(RunDailyData runDailyData) {

        if (checkDailyDataValid(runDailyData)) {

            ContentValues values = new ContentValues();
            values.put("time", runDailyData.getTime());
            values.put("date", runDailyData.getDate());
            values.put("spendtime", runDailyData.getSpendtime());
            values.put("calorie", runDailyData.getCalorie());
            values.put("vector", runDailyData.getVector());
            values.put("distance", runDailyData.getDistance());
            db.insert("RUN_Daily_DATA", null, values);

            upDateMouthTable(runDailyData);

            Toasty.normal(mContext, "数据已经保存至数据库", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkDailyDataValid(RunDailyData data) {

        if (data == null) {
            Toasty.error(mContext, "数据保存失败,runData为空").show();
        }
        if (data.getDistance() <= 0) {
            Toasty.error(mContext, "数据不符合，路程 ： " + data.getDistance()).show();
            return false;
        }

        return true;
    }


    public List<RunDailyData> selectFromDaily() {

        List datas = new ArrayList();

        Cursor cursor = db.query("RUN_Daily_DATA", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                Float spendtime = cursor.getFloat(cursor.getColumnIndex("spendtime"));
                Float calorie = cursor.getFloat(cursor.getColumnIndex("calorie"));
                Float vector = cursor.getFloat(cursor.getColumnIndex("vector"));
                Float distance = cursor.getFloat(cursor.getColumnIndex("distance"));


                RunDailyData runDailyData = new RunDailyData();
                runDailyData.setId(id);
                runDailyData.setDate(date);
                runDailyData.setTime(time);
                runDailyData.setSpendtime(spendtime);
                runDailyData.setCalorie(calorie);
                runDailyData.setVector(vector);
                runDailyData.setDistance(distance);

                datas.add(runDailyData);

                Log.i("MyTAG", "id :" + id);
                Log.i("MyTAG", "date :" + date);
                Log.i("MyTAG", "time:" + time);
                Log.i("MyTAG", "alltime:" + spendtime);
                Log.i("MyTAG", "calorie:" + calorie);
                Log.i("MyTAG", "vector:" + vector);
                Log.i("MyTAG", "distance:" + distance);

            } while (cursor.moveToNext());
        }

        return datas;
    }
}
