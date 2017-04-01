package com.hut.cwp.runner.map.clazz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.activitys.runningpage.RuningActivity;
import com.hut.cwp.runner.dao.bean.PathRecord;
import com.hut.cwp.runner.map.utils.RunDataModelUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Adminis on 2017/3/27.
 */

public class Location implements LocationSource, AMapLocationListener, SensorEventListener {

    private AMap aMap;
    private Context mContext;

    private SensorManager mSM;
    private Sensor mSensor;
    private static float angle = 0;

    private MyLocationStyle myLocationStyle;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    //以前的定位点
    private LatLng oldLatLng;
    //是否是第一次定位
    private boolean isFirstLatLng = true;
    //定义一个UiSettings对象
    private UiSettings mUiSettings;
    //历史轨迹 位置集合
    private List<LatLng> points = new ArrayList<>();
    //单次跑步总路程
    private static boolean isRunning = false;

    private PathRecord record;


    private long time = 0;


    private RuningActivity activity;

    public Location(AMap aMap, Activity activity) {

        this.aMap = aMap;
        this.mContext = activity;
        this.activity = (RuningActivity) activity;
        init();
        initRecord();
    }

    /**
     * 初始化工作
     */
    public void init() {

        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.img_local_flag));
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        aMap.setMinZoomLevel(14);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(28));

        mUiSettings.setMyLocationButtonEnabled(true);
        mSM = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSM.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSM.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);//注册回调函数
    }

    /**
     * 初始化轨迹记录
     */
    public void initRecord() {
        aMap.clear();
        if (record != null) {
            record = null;
        }
        record = new PathRecord();
        record.setDate(RunDataModelUtil.getcueDate(System.currentTimeMillis()));
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);
                // 获取新的定位位置
                LatLng newLatLng = RunDataModelUtil.getLocationLatLng(aMapLocation);
                record.addpoint(aMapLocation);

                DecimalFormat df = new DecimalFormat(mContext.getString(R.string.datefomatter));
                String distance = df.format(RunDataModelUtil.getDistance(record.getPathline()) / 1000);
                String vector = df.format(RunDataModelUtil.getAverage(RunDataModelUtil.getDistance(record.getPathline()),time));
                time++;

                String duration = RunDataModelUtil.timeFormat(time);

                if (activity != null) {
                    //默认值为0
                    activity.upVector(vector, "0");
                    activity.upDistance(distance, "0");
                    activity.upDuration(duration, "0");
                }
                if (isFirstLatLng) {
                    //记录第一次的定位信息
                    oldLatLng = newLatLng;
                    isFirstLatLng = false;
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
                }
                //位置有变化
                if (oldLatLng != newLatLng) {
                    //画出最新运动轨迹
                    aMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
                    setUpMap(oldLatLng, newLatLng);
                    //加到历史轨迹中去
                    points.add(newLatLng);
                    //更新最新位置
                    oldLatLng = newLatLng;
                    /**
                     * 下面的语句不能一起生效，只有后者生效
                     */
                    aMap.animateCamera(CameraUpdateFactory.changeBearing(angle - 25));
                }

            } else {
                String errText = mContext.getString(R.string.local_failure) + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                if (isFirstLatLng) {

                    Toast.makeText(mContext, errText, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(mContext);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationOption.setInterval(1000);
            mLocationOption.setSensorEnable(true);
            mlocationClient.setLocationOption(mLocationOption);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */

            startLocation();

        }
    }

    @Override
    public void deactivate() {

        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        Toasty.info(mContext, mContext.getString(R.string.run_finish_text)).show();
    }

    /**
     * 传感器的接口实现
     * 监听方向的变化，使地图的随手机方向改变而改变
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float degree = event.values[0];
            angle = degree;
        }
    }

    /**
     * 传感器的接口方法
     *
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 开启定位
     */
    public void startLocation() {

        if (!isRunning) {
            mUiSettings.setAllGesturesEnabled(false);
            isRunning = true;
            if (isFirstLatLng) {

                record = new PathRecord();
                Toasty.normal(mContext, mContext.getString(R.string.start_new_run), Toast.LENGTH_SHORT).show();
            } else {
                Toasty.normal(mContext, mContext.getString(R.string.run_resume)).show();
            }
        }
        mlocationClient.startLocation();

    }

    /**
     * 关闭定位
     */
    public void pauseLocation() {
        if (mlocationClient.isStarted()) {
            if (isRunning) {
                isRunning = false;
                mUiSettings.setAllGesturesEnabled(true);
            }
            mlocationClient.stopLocation();
            Toasty.info(mContext, mContext.getString(R.string.run_pause)).show();
        }
    }

    public void closeLocation() {

        //回收工作
        isRunning = false;
        isFirstLatLng = true;
        deactivate();

    }

    /**
     * 返回当前轨迹记录
     *
     * @return
     */
    public PathRecord getRecord() {

        return record;
    }


    public long getDuration(){

        return time;
    }
    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     * 用于绘制轨迹
     */
    private void setUpMap(LatLng oldData, LatLng newData) {
        // 绘制一个大地曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));
    }

}
