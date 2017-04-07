package me.hutcwp.runner.map.impl;


import android.app.Activity;

import com.amap.api.maps.AMap;

import me.hutcwp.runner.activitys.runningpage.RuningActivity;
import me.hutcwp.runner.dao.utils.RunDataSaveUtil;
import me.hutcwp.runner.map.clazz.Location;
import me.hutcwp.runner.map.clazz.Trace;
import me.hutcwp.runner.map.sub.SubMap;

/**
 * 高德地图的实现类
 */
public class AmapImpl extends SubMap {


    private long mStartTime;

    private RuningActivity activity;


    private Trace trace;

    private Location location;

    /**
     * 用于显示轨迹
     *
     * @param
     */
    public AmapImpl(Trace trace) {

        this.trace = trace;
        showTraceRecord();

    }


    /**
     * 用于跑步界面
     *
     * @param activity
     * @param aMap
     */
    public AmapImpl(Activity activity, AMap aMap) {

        this.activity = (RuningActivity) activity;
        this.location = new Location(aMap, activity);
        startLocation();

    }


    @Override
    public void startLocation() {

        location.startLocation();
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public void pauseLocation() {

        location.pauseLocation();
    }


    @Override
    public void closeLocation() {

        location.closeLocation();

    }


    public void saveRunningData() {
        RunDataSaveUtil.getInstance(activity)
                .saveRecord(location
                        .getRecord()
                        .getPathline(),mStartTime, location.getDuration());
    }

    @Override
    public void lookHistory() {

        showTraceRecord();
    }

    /**
     * 显示轨迹
     */
    public void showTraceRecord() {

        if (trace != null) {
            trace.setupRecord();
        }
    }



}
