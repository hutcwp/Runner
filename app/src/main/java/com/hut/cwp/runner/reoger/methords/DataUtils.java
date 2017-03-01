package com.hut.cwp.runner.reoger.methords;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.hut.cwp.runner.base.application.MyApplication;
import com.hut.cwp.runner.cwp.activity.RunnerMainActivity;
import com.hut.cwp.runner.reoger.bean.DistanceJson;
import com.hut.cwp.runner.reoger.bean.ITypeInfo;
import com.hut.cwp.runner.reoger.service.GsonService;

/**
 * Created by 24540 on 2017/2/28.
 * 获得与跑步相关的数据
 */

public class DataUtils {
    private MyApplication myApplication = null;
    private long serviceID;
    private String entityName;
    private LBSTraceClient client;  // 实例化轨迹服务客户端
    private Context mContext;
    private int startTime;
    private RunnerMainActivity mainActivity;

    private int previouslyCountSetp=0;

    public DataUtils(int startTime) {
        this.myApplication = MyApplication.getMyApplication();
        this.serviceID = myApplication.getServiceId();
        this.entityName = myApplication.getEntityName();
        mContext = myApplication.getmContext();
        this.startTime = startTime;
        myApplication.setStartTime(startTime);
        client = new LBSTraceClient(mContext);  //实例化轨迹服务客户端
        mainActivity = new RunnerMainActivity();
    }

    //计算里程
    public void getMileag() {

        int isProcessed = 1;
        String processOption = "need_denoise=1,need_vacuate=1,need_mapmatch=0";
        String supplementMode = "walking";
       // int startTime = (int) (System.currentTimeMillis() / 1000 - 100);//传入的时间还需要修改

        int endTime = (int) (System.currentTimeMillis() / 1000);
        myApplication.setEndTime(endTime);
        client.queryDistance(serviceID, entityName, isProcessed, processOption, supplementMode, startTime, endTime, onTrackListener);
    }

    OnTrackListener onTrackListener = new OnTrackListener() {
        @Override
        public void onRequestFailedCallback(String s) {

        }

        @Override
        public void onQueryDistanceCallback(String s) {
            super.onQueryDistanceCallback(s);
            Log.d("TAG", "查询里程回调的接口，数据是" + s);
            DistanceJson distanceJson = GsonService.parseJson(s,DistanceJson.class);
            Log.d("TAG","距离是"+distanceJson.getDistance());
            //从这里回传距离信息
            Message message = new Message();
            message.what = ITypeInfo.Distance;
            message.arg1 = (int) distanceJson.getDistance();
            mainActivity.mHandler.sendMessage(message);
        }
    };


    //获取当前速度
    //计算公式 步数*0.9/5 单位m/s
    public void getCurrentSpeed(){
        int stepsCount = StepDetector.CURRENT_SETP;
        float speed = (float) (0.9*(stepsCount-previouslyCountSetp)/5);
        previouslyCountSetp= stepsCount;
        Message msg = new Message();
        msg.what =ITypeInfo.Speed;
        Bundle bundle = new Bundle();
        bundle.putFloat(ITypeInfo.SPEED,speed);
        mainActivity.mHandler.sendMessage(msg);
    }



}
