package com.hut.cwp.runner.reoger.threads;

import android.util.Log;

import com.hut.cwp.runner.reoger.methords.DataUtils;

/**
 * Created by 24540 on 2017/2/28.
 * 计算当前里程的线程
 */

public class DetInfoFromIntent extends Thread {
    private int startTime;
    private static boolean isRunning = true;
    private DataUtils utils;

    public DetInfoFromIntent(int startTime){
        this.startTime = startTime;
        utils = new DataUtils(this.startTime);
    }

    public  void setIsRunning(boolean isRunning) {
      this.isRunning = isRunning;
    }

    @Override
    public void run(){
        while(isRunning){
            try {
                utils.getMileag();
                utils.getCurrentSpeed();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("TAG","线程停止了");
    }
}
