package com.hut.cwp.runner.cwp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.baidu.trace.Trace;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.base.application.MyApplication;
import com.hut.cwp.runner.reoger.bean.ITypeInfo;
import com.hut.cwp.runner.reoger.bean.RealtimeTrackData;
import com.hut.cwp.runner.reoger.methords.DataUtils;
import com.hut.cwp.runner.reoger.methords.StepDetector;
import com.hut.cwp.runner.reoger.service.GsonService;
import com.hut.cwp.runner.reoger.service.MileagSpeedService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.baidu.location.h.j.R;


public class RunnerMainActivity extends AppCompatActivity {


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


    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private static TextView mTVMileag;
    private TextView mTVTime;
    private static TextView mTVSpeed;

    private int totalTime = 0;
    private boolean isRunning = true;

    private EditText mAccuracy;

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = ITypeInfo.Time;
            totalTime++;
            mHandler.sendMessage(msg);
        }
    };


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ITypeInfo.Distance://传回来的数据是里程信息
                    Log.d("TAG", "distance" + msg.arg1);
                    //这里回传的数据是巨距离，单位为m
                    //// 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036，换算一下 就不做专门的接口了
                    if (mTVMileag != null) {
                        mTVMileag.setText(msg.arg1 + " m");
                    } else {
                        Log.d("TAG", "如我所料 这里就是空");
                    }
                    break;
                case ITypeInfo.Time:
                    if (mTVTime != null) {
                        mTVTime.setText(DataUtils.FormatTime(totalTime));
                    }
                    break;
                case ITypeInfo.Speed:
                    Bundle bundle = msg.getData();
                    double v = bundle.getDouble(ITypeInfo.SPEED,0);
                    Log.d("TAG","cureent v= "+v);
                    if (mTVSpeed != null) {
                        mTVSpeed.setText(v+" m/s");
                    }
                    break;
                case ITypeInfo.StepsNum:
                    break;

            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_main);
        initView();
        initOnEntityListener();
        initOnStartTraceListener();
        client.startTrace(trace, startTraceListener);  // 开启轨迹服务

        Intent intent = new Intent(this, MileagSpeedService.class);
        startService(intent);

        initTime();
       findViewById(R.id.submit_area__).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int num =  Integer.parseInt(mAccuracy.getText().toString());
               StepDetector.SENSITIVITY = num;
               Toast.makeText(getApplicationContext(),"设置成功",Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void initTime() {
        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    //初始化各种数据
    private void initView() {
        mMapView = (MapView) findViewById(R.id.mMapView);
        mTVMileag = (TextView) findViewById(R.id.runner_main_activity_mileag);
        mTVTime = (TextView) findViewById(R.id.runner_main_activity_time);
        mTVSpeed = (TextView) findViewById(R.id.runner_main_activity_speed);
        mAccuracy = (EditText) findViewById(R.id.runner_main_activity_accuracy);

        mBaiduMap = mMapView.getMap();
        mMyApplication = MyApplication.getMyApplication();
        mMapView.showZoomControls(false);
        client = new LBSTraceClient(getApplicationContext());

        mServiceId = mMyApplication.getServiceId();
        mEntityName = mMyApplication.getEntityName();
        mGatherInterval = mMyApplication.getGatherInterval();
        mTraceType = mMyApplication.getTraceType();
        mPackInterval = mMyApplication.getPackInterval();

        trace = new Trace(getApplicationContext(), mServiceId, mEntityName, mTraceType);
        client.setInterval(mMyApplication.getGatherInterval(), mMyApplication.getPackInterval());
    }

    //初始化设置实体状态监听器
    private void initOnEntityListener() {
        //实体状态监听器
        entityListener = new OnEntityListener() {

            @Override
            public void onRequestFailedCallback(String arg0) {
                Looper.prepare();
                Toast.makeText(
                        getApplicationContext(),
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
                Toast.makeText(getApplicationContext(), "当前无轨迹点", Toast.LENGTH_LONG).show();
            }

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

        mBaiduMap.clear();
        MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        msUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        realtimeBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        overlay = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);

        if (pointList.size() >= 2 && pointList.size() <= 1000) {
            polyline = new PolylineOptions().width(10).color(Color.RED).points(pointList);
        }

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

}
