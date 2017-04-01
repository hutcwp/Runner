package com.hut.cwp.runner.map.clazz;

import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.activitys.historyshowpage.fragments.MapFragment;
import com.hut.cwp.runner.dao.bean.PathRecord;
import com.hut.cwp.runner.dao.utils.DBUtil;
import com.hut.cwp.runner.map.utils.RunDataModelUtil;
import com.hut.cwp.runner.map.utils.TraceUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Adminis on 2017/3/26.
 */

public class Trace {


    private AMap mAMap;

    private int mRecordItemId;

    private List<LatLng> mOriginLatLngList;  //原始轨迹

    private MapFragment mapFragment;

    public Trace(AMap mAMap, MapFragment fragment, int mRecordItemId) {

        this.mAMap = mAMap;
        this.mapFragment = fragment;
        this.mRecordItemId = mRecordItemId;

    }


    /**
     * 轨迹数据初始化
     */
    public void setupRecord() {

        DBUtil dbhelper = new DBUtil(mapFragment.getContext());

        dbhelper.open();

        PathRecord record = dbhelper.queryRecordById(mRecordItemId);
        Log.i("mTAG", "mRecord==null " + (record == null) + " " + record);

        DecimalFormat df = new DecimalFormat("######0.00");
        String distance = df.format(record.getDistance() / 1000.0) + " km";

        String duration = RunDataModelUtil.timeFormat((long) record.getDuration());
        String calorie = df.format(record.getCalorie() / 1000) + "k ca";
        String vector = df.format(record.getAveragespeed()) + " m/s";

        mapFragment.upDataShowing(distance, duration, vector, calorie);

        dbhelper.close();

        if (record != null) {
            List<AMapLocation> recordList = record.getPathline();
            AMapLocation startLoc = record.getStartpoint();
            AMapLocation endLoc = record.getEndpoint();
            if (recordList == null || startLoc == null || endLoc == null) {
                Log.i("mTAG", "recordList==null " + (recordList == null) + " " + recordList);
                Log.i("mTAG", "startLoc==null " + (startLoc == null) + " " + startLoc);
                Log.i("mTAG", "endLoc==null " + (endLoc == null) + " " + endLoc);
                return;
            }
            LatLng startLatLng = new LatLng(startLoc.getLatitude(),
                    startLoc.getLongitude());
            LatLng endLatLng = new LatLng(endLoc.getLatitude(),
                    endLoc.getLongitude());
            mOriginLatLngList = TraceUtil.parseLatLngList(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);
        } else {
            Log.i("mTAG", "mRecord == null");
        }

    }

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint 开始位置
     * @param endPoint   结束位置
     * @param originList 途径集合
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint,
                                List<LatLng> originList) {
        mAMap.addPolyline(new PolylineOptions().color(
                Color.GREEN).addAll(originList));
        mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.img_local_start)));
        mAMap.addMarker(new MarkerOptions().position(
                endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.img_local_end)));

        Log.i("Trace", "addOriginTrace");
        mAMap.animateCamera(CameraUpdateFactory.newLatLng(startPoint));
        mAMap.setMinZoomLevel(18);

    }


}
