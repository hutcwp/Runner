package com.hut.cwp.runner.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by 24540 on 2017/2/27.
 */

public class MyApplication extends Application {


    private static MyApplication myApplication =null;
    public static MyApplication getMyApplication(){
        return myApplication;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private long  serviceId = 134220;   //服务Id
    private String entityName;         //实体名字。这里用手机imer作为名字
    private int gatherInterval = 8;  //位置采集周期 (s)
    private int packInterval = 10;  //打包周期 (s)
    private int traceType = 2;  //轨迹服务类型

    private int startTime;
    private int endTime;
    private Context mContext;


    public long getServiceId() {
        return serviceId;
    }

    public String getEntityName() {
        return entityName;
    }

    public int getGatherInterval() {
        return gatherInterval;
    }

    public int getPackInterval() {
        return packInterval;
    }

    public int getTraceType() {
        return traceType;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Context getmContext() {

        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        myApplication = this;
        SDKInitializer.initialize(mContext);
        entityName = getImei(mContext);
        Log.d("TAG","item*********"+entityName);

    }

    private String getImei(Context context){
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            Log.d("TAG","获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }
}
