package com.hut.cwp.runner.map.impl;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.dao.DBUtils;
import com.hut.cwp.runner.dao.RunDailyData;
import com.hut.cwp.runner.homepage.MainActivity;
import com.hut.cwp.runner.map.sub.SubMap;
import com.hut.cwp.runner.test.database.DbAdapter;
import com.hut.cwp.runner.test.record.PathRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Adminis on 2017/3/14.
 */

public class AmapImpl extends SubMap implements LocationSource, AMapLocationListener, SensorEventListener {


    private AMap aMap;


    private MainActivity activity;

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
    float distance = 0;
    float vector = 0;

    private static boolean isRunning = false;

    private static AmapImpl mAmapImpl;

    private SensorManager mSM;
    private Sensor mSensor;

    private static float angle = 0;


//**************************************
    private PathRecord record;
    private PolylineOptions mPolyoptions;

    private long mStartTime;
    private long mEndTime;
    private DbAdapter DbHepler;

    public static AmapImpl getInstance(Activity activity, AMap aMap) {


        mAmapImpl = new AmapImpl(activity, aMap);

        return mAmapImpl;
    }


    private AmapImpl(Activity activity, AMap aMap ) {

        this.aMap = aMap;
        this.activity = (MainActivity) activity;


        initRecord();
        initpolyline();

    }


    public void initRecord(){
        aMap.clear();

        if (record != null) {
            record = null;
        }
        record = new PathRecord();
        mStartTime = System.currentTimeMillis();
        record.setDate(getcueDate(mStartTime));
    }

    @Override
    public void startLocation() {

        if (!isRunning) {

            distance = 0;
            vector = 0;
            mUiSettings.setAllGesturesEnabled(false);
            isRunning = true;


            if (isFirstLatLng) {

                Toasty.normal(activity, "开启新的一次跑步", Toast.LENGTH_SHORT).show();

            } else {

                Toasty.normal(activity, "运动已恢复").show();
            }

        }

        mlocationClient.startLocation();
        Log.e("MyTAG", "startLoc");

    }

    @Override
    public void pauseLocation() {

        if (mlocationClient.isStarted()) {

            if (isRunning) {

                isRunning = false;
                mUiSettings.setAllGesturesEnabled(true);
            }

            mlocationClient.stopLocation();
            Toasty.info(activity, "运动已暂停").show();

            Log.e("MyTAG", "pauseLoc");
        }
    }


    @Override
    public void closeLocation() {


        saveRunningData();

        mEndTime = System.currentTimeMillis();
        saveRecord(record.getPathline(), record.getDate());
//        deactivate();

        Log.e("MyTAG", "stopLoc");


    }


    @Override
    public void lookHistory() {

        getHistory();
    }


    /**
     * 保存运动数据
     */
    private void saveRunningData() {

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new java.util.Date());

        SimpleDateFormat tDateFormat = new SimpleDateFormat("hh:mm:ss");
        String time = tDateFormat.format(new java.util.Date());

        RunDailyData data = new RunDailyData();

        data.setTime(time);
        data.setDate(date);
        data.setDistance(distance);
        data.setSpendtime(0);
        data.setVector(vector);
        data.setCalorie(distance * 10);

        DBUtils.getInstance(activity).insertToDailyTable(data);

    }

    /**
     * 获取历史记录并显示
     */
    public void getHistory() {

        mlocationClient.stopLocation();

        List<LatLng> latLngs = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            latLngs.add(new LatLng(45.45 + i * 0.1, 75.59 + i * 0.1));
        }

        points = latLngs;

        // 判断集合中是否多余3个坐标点
        if (points.size() > 3) {

            LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));

            //具有动画效果的移动
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

            SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
            // 设置滑动的图标
            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.navigation));

            //起点
            LatLng drivePoint = points.get(0);

            Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
            points.set(pair.first, drivePoint);
            //终点
            List<LatLng> subList = points.subList(pair.first, points.size());

            // 设置滑动的轨迹左边点
            smoothMarker.setPoints(subList);
            // 设置滑动的总时间
            smoothMarker.setTotalDuration(50);
            // 开始滑动
            smoothMarker.startSmoothMove();

        } else {

            Toast.makeText(activity, "没有历史轨迹", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 初始化工作
     */
    public void init() {

        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.local1));
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种

        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

        mSM = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSM.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSM.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);//注册回调函数

    }


    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap(LatLng oldData, LatLng newData) {

        // 绘制一个大地曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        Utils localUtils = new Utils();

        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                // 显示系统小蓝点
                mListener.onLocationChanged(aMapLocation);

                // 获取新的定位位置
                LatLng newLatLng = localUtils.getLocationLatLng(aMapLocation);

                record.addpoint(aMapLocation);
                mPolyoptions.add(newLatLng);
                redrawline();


                if (isFirstLatLng) {
                    //记录第一次的定位信息
                    oldLatLng = newLatLng;
                    isFirstLatLng = false;

                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition
                            (new CameraPosition(newLatLng, 18, 60, angle - 360)));

                }
                //位置有变化
                if (oldLatLng != newLatLng) {
                    //画出最新运动轨迹
                    setUpMap(oldLatLng, newLatLng);
                    //加到历史轨迹中去
                    points.add(newLatLng);
                    //更新最新位置
                    oldLatLng = newLatLng;
                    //更新距离
//                    distance += AMapUtils.calculateLineDistance(newLatLng, oldLatLng);
                    vector = aMapLocation.getSpeed();
                    distance += vector * 2.5;


                    Log.i("Tag", "distance" + distance);
                    /**
                     ** 3D地图显示
                     */
//                    Log.i("Tag", "Angle" + angle);

                    if (angle > 360) {

                        aMap.animateCamera(CameraUpdateFactory.changeBearing(angle - 20));

//                        aMap.moveCamera(CameraUpdateFactory.newCameraPosition
//                                (new CameraPosition(newLatLng, 18, 60, angle -20)));

                    } else {

                        aMap.animateCamera(CameraUpdateFactory.changeBearing(angle + 340));


//                        aMap.animateCamera(CameraUpdateFactory.newCameraPosition
//                                (new CameraPosition(newLatLng, 18, 60, angle +340)));

                    }

                    //回调接口，实时获取数据
                    activity.upVector(vector);
                    activity.upDistance(distance);

                    Log.e("TAG", "distance: " + distance + "");
                    Log.e("TAG", "vector: " + aMapLocation.getSpeed() + " m/s");
                }

            } else {

                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);

                if (isFirstLatLng) {
                    Toast.makeText(activity, errText, Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {

        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(activity);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
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

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {

        mListener = null;

        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }

        mlocationClient = null;

        Toasty.info(activity, "运动结束").show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            float degree = event.values[0];

            angle = degree;

//            if (angle > 360)
//
//                aMap.setMyLocationRotateAngle(angle + 360);// 设置小蓝点旋转角度
//            else
//                aMap.setMyLocationRotateAngle(angle);//

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }


    /**
     * 辅助工具类
     *
     * @文件名称: Utils.java
     * @类型名称: Utils
     */
    public class Utils {


        /**
         * 根据定位结果返回定位信息的字符串
         *
         * @param
         * @return
         */
        public synchronized String getLocationStr(AMapLocation location) {
            if (null == location) {
                return null;
            }
            StringBuffer sb = new StringBuffer();

            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");

                if (location.getProvider().equalsIgnoreCase(
                        android.location.LocationManager.GPS_PROVIDER)) {
                    // 以下信息只有提供者是GPS时才会有
                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : "
                            + location.getSatellites() + "\n");
                } else {
                    // 提供者是GPS时是没有以下信息的
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                }
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
            return sb.toString();
        }


        public synchronized LatLng getLocationLatLng(AMapLocation location) {
            //                    纬    度                经    度
            return new LatLng(location.getLatitude(), location.getLongitude());
        }


    }


    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    protected void saveRecord(List<AMapLocation> list, String time) {

        if (list != null && list.size() > 0) {

            DbHepler = new DbAdapter(activity);
            DbHepler.open();

            String duration = getDuration();
            float distance = getDistance(list);
            String average = getAverage(distance);
            String pathlineSring = getPathLineString(list);

            AMapLocation firstLocaiton = list.get(0);
            AMapLocation lastLocaiton = list.get(list.size() - 1);
            String stratpoint = amapLocationToString(firstLocaiton);
            String endpoint = amapLocationToString(lastLocaiton);
            DbHepler.createrecord(String.valueOf(distance), duration, average,
                    pathlineSring, stratpoint, endpoint, time);
            DbHepler.close();
        } else {
            Toast.makeText(activity, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private String getDuration() {
        return String.valueOf((mEndTime - mStartTime) / 1000f);
    }

    private String getAverage(float distance) {
        return String.valueOf(distance / (float) (mEndTime - mStartTime));
    }

    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.BLUE);
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }


    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 0) {
            aMap.clear(true);
            aMap.addPolyline(mPolyoptions);
        }
    }
}
