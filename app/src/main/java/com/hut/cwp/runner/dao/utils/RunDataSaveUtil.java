package com.hut.cwp.runner.dao.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.hut.cwp.runner.dao.bean.PathRecord;
import com.hut.cwp.runner.dao.bean.RunData;
import com.hut.cwp.runner.map.utils.TraceUtil;
import com.hut.cwp.runner.map.utils.RunDataModelUtil;

import java.util.List;

/**
 * Created by Adminis on 2017/3/27.
 */

public class RunDataSaveUtil {

    private DBUtil DbHepler;

    private Context mContext;

    public static RunDataSaveUtil instance = null;

    public static RunDataSaveUtil getInstance(Context context) {

        if (instance == null) {
            instance = new RunDataSaveUtil(context);
        }

        return instance;
    }

    private RunDataSaveUtil(Context mContext) {

        this.mContext = mContext;
    }

    /**
     * 保存运动数据和轨迹
     */
    public void saveRecord(List<AMapLocation> list,long mStartTime , long alltime) {
        if (list != null && list.size() > 0) {

            float duration = alltime;
            float distance = RunDataModelUtil.getDistance(list);
            float average = RunDataModelUtil.getAverage(distance, alltime);
            float calorie = RunDataModelUtil.getCalorie(distance);

            String cur_time = RunDataModelUtil.getcueTime(mStartTime);
            String date = RunDataModelUtil.getcueDate(mStartTime);
            String mouth = RunDataModelUtil.getcueMouth(mStartTime);

            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);

            String stratpoint = TraceUtil.amapLocationToString(firstLocaiton);
            String endpoint = TraceUtil.amapLocationToString(lastLocaiton);
            String pathlineSring = TraceUtil.getPathLineString(list);


            RunData data = new RunData();
            data.setDuration(duration);
            data.setDate(date);
            data.setDistance(distance);
            data.setVector(average);
            data.setCalorie(10);
            data.setDate_mouth(mouth);

            PathRecord record = new PathRecord();

            record.setTime(cur_time);
            record.setCalorie(calorie);
            record.setAveragespeed(average);
            record.setDistance(distance);
            record.setDate(date);
            record.setDuration(duration);

            Log.i("test", "v " + average);

            save(data, record, pathlineSring, stratpoint, endpoint);

        } else {
            Toast.makeText(mContext, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void save(RunData data, PathRecord record, String pathlineSring, String stratpoint, String endpoint) {

        DbHepler = new DBUtil(mContext);
        DbHepler.open();

        DbHepler.createRecord(record, pathlineSring, stratpoint, endpoint);
        DbHepler.upRunDataByDate(data);

        DbHepler.close();
    }
}
