package com.hut.cwp.runner.reoger.methords;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.base.application.MyApplication;
import com.hut.cwp.runner.reoger.bean.HistoryTrackData;
import com.hut.cwp.runner.reoger.service.GsonService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24540 on 2017/3/3.
 * 显示历史轨迹
 */

public class DrawHistoryPath {

    public DrawHistoryPath(Context mContext,MapView mapView) {
        this.mContext = mContext;
        this.mMapView = mapView;
        mBaiduMap =mMapView.getMap();
    }



    private static MapView mMapView;
    private static BaiduMap mBaiduMap;
    private Context mContext;



    private static OnTrackListener entityListener = null;

    private static MapStatusUpdate msUpdate = null;
    private static BitmapDescriptor bmStart;  //图标
    private static BitmapDescriptor bmEnd;  //图标
    private static List<LatLng> pointList = new ArrayList<LatLng>();  //定位点的集合
    private static PolylineOptions polyline = null;  //路线覆盖物
    private LBSTraceClient client;  // 实例化轨迹服务客户端

    private int mGatherInterval;  //位置采集周期 (s)
    private int mPackInterval;  //打包周期 (s)
    private String mEntityName;  // entity标识
    private long mServiceId;//鹰眼服务ID
    private int mTraceType;  //轨迹服务类型
    private int mStartTime;//开始时间
    private int mEndTime;//结束时间


    private OverlayOptions startMarker;
    private OverlayOptions endMarker;


    private MyApplication mMyApplication;

    public void startDrawMethord() {
        mMyApplication = MyApplication.getMyApplication();
        client = new LBSTraceClient(mContext.getApplicationContext());
        mServiceId = mMyApplication.getServiceId();
        mEntityName = mMyApplication.getEntityName();
        mGatherInterval = mMyApplication.getGatherInterval();
        mTraceType = mMyApplication.getTraceType();
        mPackInterval = mMyApplication.getPackInterval();

        mStartTime = (int) (System.currentTimeMillis()/1000-12*60*60);
        mEndTime = (int) (System.currentTimeMillis()/1000);

        initOnEntityListener();
        initQueryHistory();

    }

    private void initQueryHistory() {
        Toast.makeText(mContext,"正在查询，请稍候"+mStartTime+" 开始和结束的时间:"+mEndTime,Toast.LENGTH_SHORT).show();
        int simpleReturn = 0;
        // 是否纠偏
        int isProcessed = 0;
        // 纠偏选项
        String processOption = "need_denoise=1,need_vacuate=1,need_mapmatch=0";
        // 分页大小
        int pageSize = 5000;
        // 分页索引
        int pageIndex = 1;


        client.queryHistoryTrack(mServiceId , mEntityName, simpleReturn, isProcessed,
                processOption, mStartTime, mEndTime, pageSize, pageIndex, entityListener);
    }

    //初始化设置实体状态监听器
    private void initOnEntityListener() {
        //实体状态监听器
        entityListener = new OnTrackListener() {
            public void onQueryHistoryTrackCallback(String message) {
                Log.d("TAG","历史轨迹信息为："+message);
                // 解析并保存轨迹信息
                HistoryTrackData historyTrackData = GsonService.parseJson(message,
                        HistoryTrackData.class);
                if (historyTrackData != null && historyTrackData.getStatus() == 0) {
                    if (historyTrackData.getListPoints() != null) {
                        pointList.clear();
                        pointList.addAll(historyTrackData.getListPoints());
                    }else{
                        pointList.clear();

                    }
                    if (pointList!=null ||pointList.size()!=0)
                    for (int i = 0; i < pointList.size(); i++) {
                        Log.d("T","123"+pointList.get(i));
                    }
                    drawHistoryTrack(pointList);
                }


            }
            @Override
            public void onRequestFailedCallback(String s) {

                Toast.makeText(mContext,"查询失败",Toast.LENGTH_SHORT).show();
                Log.d("TAG","查询失败");
            }
        };
    }

    public void setHistoryTime(int startTime){
        this.mStartTime =  startTime;
        this.mEndTime   =  startTime+24*60*60;
        if(mEndTime>(System.currentTimeMillis()/1000)){
            mEndTime = (int) (System.currentTimeMillis()/1000);
        }
        initQueryHistory();
    }


    /**
     * 绘制历史轨迹
     *
     * @param points
     */
    public void drawHistoryTrack(final List<LatLng> points) {
        // 绘制新覆盖物前，清空之前的覆盖物
        mBaiduMap.clear();

        if (points == null || points.size() == 0) {
            Looper.prepare();
            Toast.makeText(mContext, "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
            Looper.loop();

        } else if (points.size() > 1) {

            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();

            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            bmStart = BitmapDescriptorFactory.fromResource(R.mipmap.start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.mipmap.end);

            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);

            // 添加终点图标
             endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);

            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.GREEN).points(points);

            addMarker();

        }

    }





    private void addMarker() {

        if (msUpdate != null) {
            mBaiduMap.setMapStatus(msUpdate);
        }
        if (polyline != null) {
            mBaiduMap.addOverlay(polyline);
        }
        if (startMarker != null) {
            mBaiduMap.addOverlay(startMarker);
        }
        if(endMarker!=null){
            mBaiduMap.addOverlay(endMarker);
        }

    }

}
