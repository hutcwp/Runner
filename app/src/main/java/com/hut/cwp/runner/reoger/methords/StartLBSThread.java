package com.hut.cwp.runner.reoger.methords;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.base.application.MyApplication;
import com.hut.cwp.runner.reoger.bean.RealtimeTrackData;
import com.hut.cwp.runner.reoger.service.GsonService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24540 on 2017/3/3.
 */

public class StartLBSThread {

    public StartLBSThread(Context mContext) {
        this.mContext = mContext;

    }

    private static boolean mHasOpenMap = false;

    private static MapView mMapView;
    private static BaiduMap mBaiduMap;
    private Context mContext;


    private static OnStartTraceListener startTraceListener = null;  //开启轨迹服务监听器
    private static OnEntityListener entityListener = null;
    private RefreshThread refreshThread = null;  //刷新地图线程以获取实时点
    private static MapStatusUpdate msUpdate = null;
    private static BitmapDescriptor realtimeBitmap;  //图标
    private static OverlayOptions overlay;  //覆盖物
    private static List<LatLng> pointList = new ArrayList<LatLng>();  //定位点的集合
    private static PolylineOptions polyline = null;  //路线覆盖物
    private Trace trace;  // 实例化轨迹服务
    private LBSTraceClient client;  // 实例化轨迹服务客户端

    private int mGatherInterval;  //位置采集周期 (s)
    private int mPackInterval;  //打包周期 (s)
    private String mEntityName;  // entity标识
    private long mServiceId;// 鹰眼服务ID
    private int mTraceType;  //轨迹服务类型

    private MyApplication mMyApplication;

    public void startLBSThreadMethord() {
        mMyApplication = MyApplication.getMyApplication();
        client = new LBSTraceClient(mContext.getApplicationContext());
        mServiceId = mMyApplication.getServiceId();
        mEntityName = mMyApplication.getEntityName();
        mGatherInterval = mMyApplication.getGatherInterval();
        mTraceType = mMyApplication.getTraceType();
        mPackInterval = mMyApplication.getPackInterval();

        trace = new Trace(mContext.getApplicationContext(), mServiceId, mEntityName, mTraceType);
        client.setInterval(mMyApplication.getGatherInterval(), mPackInterval);

        initOnEntityListener();
        initOnStartTraceListener();
        client.startTrace(trace, startTraceListener);  // 开启轨迹服务

    }

    //初始化设置实体状态监听器
    private void initOnEntityListener() {
        //实体状态监听器
        entityListener = new OnEntityListener() {

            @Override
            public void onRequestFailedCallback(String arg0) {
                Looper.prepare();
                Toast.makeText(
                        mContext.getApplicationContext(),
                        "entity请求失败的回调接口信息：" + arg0,
                        Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }

            @Override
            public void onQueryEntityListCallback(String arg0) {
                /**
                 * 查询实体集合回调函数，此时调用实时轨迹方法
                 */
                showRealtimeTrack(arg0);
            }



        };
    }


    //开始追踪
    private void initOnStartTraceListener() {
        // 实例化开启轨迹服务回调接口
        startTraceListener = new OnStartTraceListener() {
            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
                Log.i("TAG", "onTraceCallback=" + arg1);
                if (arg0 == 0 || arg0 == 10006) {
                    startRefreshThread(true);
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
                Log.i("TAG", "onTracePushCallback=" + arg1);
            }
        };
    }

    /**
     * 查询实时线路
     */
    private void queryRealtimeTrack() {

        String entityName = this.mEntityName;
        String columnKey = "";
        int returnType = 0;
        int activeTime = 0;
        int pageSize = 10;
        int pageIndex = 1;

        this.client.queryEntityList(
                mServiceId,
                entityName,
                columnKey,
                returnType,
                activeTime,
                pageSize,
                pageIndex,
                entityListener
        );

    }

    /**
     * 展示实时线路图
     *
     * @param realtimeTrack
     */
    protected void showRealtimeTrack(String realtimeTrack) {

        if (refreshThread == null || !refreshThread.refresh) {
            return;
        }

        //数据以JSON形式存取
        RealtimeTrackData realtimeTrackData = GsonService.parseJson(realtimeTrack, RealtimeTrackData.class);

        if (realtimeTrackData != null && realtimeTrackData.getStatus() == 0) {

            LatLng latLng = realtimeTrackData.getRealtimePoint();

            if (latLng != null) {
                pointList.add(latLng);
                drawRealtimePoint(latLng);
                Log.d("TAG", "打印的信息 " + realtimeTrackData.getMessage());
            } else {
                Toast.makeText(mContext.getApplicationContext(), "当前无轨迹点", Toast.LENGTH_LONG).show();
            }

        }

    }

    public static void setHasOpenMap(boolean flag, MapView mapView) {
        mHasOpenMap = flag;
        if(flag){
            mMapView = mapView;
            mBaiduMap = mapView.getMap();
        }

    }


    /**
     * 轨迹刷新线程
     *
     * @author BLYang
     */
    private class RefreshThread extends Thread {

        protected boolean refresh = true;

        public void run() {

            while (refresh) {
                queryRealtimeTrack();
                try {
                    Thread.sleep(mPackInterval * 1000);
                } catch (InterruptedException e) {
                    System.out.println("线程休眠失败");
                }
            }

        }
    }


    /**
     * 画出实时线路点
     *
     * @param point
     */
    private void drawRealtimePoint(LatLng point) {
        if (mHasOpenMap)
            mBaiduMap.clear();
        MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        realtimeBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.runner);
        overlay = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);

        if (pointList.size() >= 2 && pointList.size() <= 1000) {
            polyline = new PolylineOptions().width(10).color(Color.RED).points(pointList);
        }
        if (mHasOpenMap)
            addMarker();

    }


    private void addMarker() {

        if (msUpdate != null) {
            mBaiduMap.setMapStatus(msUpdate);
        }

        if (polyline != null) {
            mBaiduMap.addOverlay(polyline);
        }
        if (overlay != null) {
            mBaiduMap.addOverlay(overlay);
        }


    }


    /**
     * 启动刷新线程
     *
     * @param isStart
     */
    private void startRefreshThread(boolean isStart) {

        if (refreshThread == null) {
            refreshThread = new RefreshThread();
        }

        refreshThread.refresh = isStart;

        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }
    }



    OnStopTraceListener topTraceListener = new OnStopTraceListener(){
        // 轨迹服务停止成功
        @Override
        public void onStopTraceSuccess() {
            Toast.makeText(mContext, "停止服务成功", Toast.LENGTH_SHORT).show();
        }
        // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
        @Override
        public void onStopTraceFailed(int arg0, String arg1) {
        }
    };

    /**
     * 停止轨迹服务
     */
    public void stopTrace(){
        if(client!=null){
            client.stopTrace(trace,topTraceListener);
        }else{
            Log.d("TAG","停止服务失败");
        }
    }
}
