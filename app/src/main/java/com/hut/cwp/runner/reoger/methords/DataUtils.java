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

import java.util.ArrayList;
import java.util.List;

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

    private List<Double> previouslyDistance = new ArrayList<>();
    double ditance ;

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
            DistanceJson distanceJson = GsonService.parseJson(s, DistanceJson.class);
            if(previouslyDistance.size()>=2){
                Double a = previouslyDistance.get(1);
               previouslyDistance.clear();
                previouslyDistance.add(a);
            }
            ditance = distanceJson.getDistance();
            previouslyDistance.add(ditance);
            Log.d("TAG", "距离是" + previouslyDistance);
            //从这里回传距离信息
            Message message = new Message();
            message.what = ITypeInfo.Distance;
            message.arg1 = (int) ditance;
            mainActivity.mHandler.sendMessage(message);
        }
    };


    //获取当前速度
    //计算公式 5s内走的里程/5 （m/s）*3.6 = km/h
    public void getCurrentSpeed() {
        double distance;
        double v;
        if(previouslyDistance.size()>1){
            distance = Math.abs(previouslyDistance.get(0)-previouslyDistance.get(1));
            v = distance/5;
        }else{
            v = 0;
        }

        Message msg = new Message();
        msg.what = ITypeInfo.Speed;
        Bundle bundle = new Bundle();
        bundle.putDouble(ITypeInfo.SPEED, v);
        msg.setData(bundle);
        Log.d("TAG","速度"+v);
        mainActivity.mHandler.sendMessage(msg);
    }

    /**
     * 将时间格式化
     *
     * @param time
     * @return
     */
    public static String FormatTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


}
