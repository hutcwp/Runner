package com.hut.cwp.runner.map.impl;


import android.app.Activity;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.hut.cwp.runner.homepage.MainActivity;
import com.hut.cwp.runner.map.sub.SubMap;

import es.dmoral.toasty.Toasty;

/**
 * Created by Adminis on 2017/3/16.
 */

public class BMapImpl extends SubMap {


    private MainActivity activity;
    private static BMapImpl mBmapImpl;
    private BaiduMap bMap;
    MapView mapView;


    public static BMapImpl getInstance(Activity activity, BaiduMap bMap, MapView mapView) {

        if (mBmapImpl == null) {
            mBmapImpl = new BMapImpl(activity, bMap, mapView);
        }
        return mBmapImpl;
    }


    private BMapImpl(Activity activity, BaiduMap bMap, MapView mapView) {
        this.bMap = bMap;
        this.activity = (MainActivity) activity;
        this.mapView = mapView;
    }


    @Override
    public void startLocation() {

        Toasty.normal(activity, "开启定位").show();
    }

    @Override
    public void pauseLocation() {

        Toasty.normal(activity, "关闭定位").show();
    }

    @Override
    public void closeLocation() {

        Toasty.normal(activity, "关闭定位").show();
    }

    @Override
    public void lookHistory() {

        Toasty.normal(activity, "查看历史轨迹").show();
    }


    /**
     * 保存运动数据
     */
    private void saveRunningData() {

        Toasty.normal(activity, "数据已经保存至数据库", Toast.LENGTH_SHORT).show();
    }


}
