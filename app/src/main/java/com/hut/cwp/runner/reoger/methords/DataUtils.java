package com.hut.cwp.runner.reoger.methords;

import android.content.Context;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.hut.cwp.runner.base.application.MyApplication;
import com.hut.cwp.runner.cwp.activity.RunnerMainActivity;
import com.hut.cwp.runner.cwp.activity.RunnerMapActivity;
import com.hut.cwp.runner.reoger.bean.DistanceJson;
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
    private RunnerMapActivity mainActivity;

    private RunnerMainActivity mRunnerMainActivity;

    private List<Double> previouslyDistance = new ArrayList<>();

<<<<<<< HEAD
    double ditance;
    //// 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036，换算一下 就不做专门的接口了


    private int previouslyCountSetp = 0;
=======
    private List<Double> previouslyDistance = new ArrayList<>();
    double ditance ;
>>>>>>> origin/master

    public DataUtils(int startTime) {
        this.myApplication = MyApplication.getMyApplication();
        this.serviceID = myApplication.getServiceId();
        this.entityName = myApplication.getEntityName();
        mContext = myApplication.getmContext();
        this.startTime = startTime;
        myApplication.setStartTime(startTime);
        client = new LBSTraceClient(mContext);  //实例化轨迹服务客户端
        mainActivity = new RunnerMapActivity();
        mRunnerMainActivity = new RunnerMainActivity();

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
<<<<<<< HEAD
            Log.d("TAG", "距离是" + distanceJson.getDistance());
            //从这里回传距离信息
            mRunnerMainActivity.getDistance((int) distanceJson.getDistance());
            if (previouslyDistance.size() >= 2) {
                Double a = previouslyDistance.get(1);
                previouslyDistance.clear();
                previouslyDistance.add(a);
            }
            ditance = distanceJson.getDistance();
            previouslyDistance.add(ditance);
            Log.d("TAG", "距离是" + previouslyDistance);


=======
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
>>>>>>> origin/master
        }
    };


    //获取当前速度
<<<<<<< HEAD
    public void getCurrentSpeed() {
        double distance;
        double spends[] = new double[2];
        double v;
        if (previouslyDistance.size() > 1) {
            distance = Math.abs(previouslyDistance.get(0) - previouslyDistance.get(1));
            v = distance / 5;
            if (spends[0] == 0) {
                spends[0] = v;
            } else if(spends[1]==0){
                spends[1] =v;
            }else{
                spends[0]=spends[1];
                spends[1] = v;
            }
        } else {
            v = 0;
        }

        for (int i = 0; i < spends.length; i++) {
            Log.d("TAG","v====="+spends[i]);
        }

        double realV ;
        if(v<8){
            //一般来说，速度不会大于8m/s 大于的话 说明出现了gps漂移的现象
            if(Math.abs(spends[0]-spends[1])>10){//两次的速度差不能大于10m/s,就意味着加速度不能大于2m/s
                realV = v;
            }
            else{
                realV =  (spends[0]+spends[1])/ 2;
            }
        }else{
           if(spends[1]!=0){
               realV = spends[1];
           }else{
               realV = spends[0];
           }
        }



        mRunnerMainActivity.getSpeed(realV);
=======
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
>>>>>>> origin/master
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
