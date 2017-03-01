package com.hut.cwp.runner.reoger.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.hut.cwp.runner.reoger.methords.StepCountModeDispatcher;
import com.hut.cwp.runner.reoger.methords.StepDetector;
import com.hut.cwp.runner.reoger.threads.DetInfoFromIntent;

/**
 * Created by 24540 on 2017/2/28.
 */

public class MileagSpeedService extends Service {
    private DetInfoFromIntent gets;

    private int startTime;

    public static Boolean FLAG = false;// 服务运行标志

    private SensorManager mSensorManager;// 传感器服务
    private StepDetector detector;// 传感器监听对象

    private PowerManager mPowerManager;// 电源管理服务
    private PowerManager.WakeLock mWakeLock;// 屏幕灯


    StepCountModeDispatcher dispatcher;
    boolean isSuppoort;

    @Override
    public void onCreate() {
        super.onCreate();
        startTime= (int) (System.currentTimeMillis()/1000);

        gets = new DetInfoFromIntent(startTime);
        gets.setIsRunning(true);
        gets.start();
        Log.d("TAG","service开启");

        dispatcher = new StepCountModeDispatcher(this);
        isSuppoort = dispatcher.isSupportStepCountSensor();
        if(isSuppoort){//判断手机是否支持
            FLAG = true;// 标记为服务正在运行

            // 创建监听器类，实例化监听对象
            detector = new StepDetector(this);

            // 获取传感器的服务，初始化传感器
            mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
            // 注册传感器，注册监听器
            mSensorManager.registerListener(detector,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_FASTEST);

            // 电源管理服务
            mPowerManager = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                    |PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
            mWakeLock.acquire();
        }else{
            Toast.makeText(getApplicationContext(),"手机不支持，没有相应的传感器",Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG","service关闭");
        gets.setIsRunning(false);

        if(isSuppoort){
            FLAG = false;// 服务停止
            if (detector != null) {
                mSensorManager.unregisterListener(detector);
            }
            if (mWakeLock != null) {
                mWakeLock.release();
            }
        }else{
            Toast.makeText(getApplicationContext(),"手机不支持",Toast.LENGTH_SHORT).show();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
