package com.hut.cwp.runner.reoger.methords;

import android.util.Log;

import com.hut.cwp.runner.cwp.activity.RunnerMainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 24540 on 2017/3/3.
 */

public class TimeCounts {
    private static int setp_counts =0;
    private Timer timer;
    private boolean isRunning = false;
    private RunnerMainActivity activity;
    private MyTimerTask timerTask;



    public TimeCounts() {
        activity = new RunnerMainActivity();
        timer = new Timer();
         timerTask = new MyTimerTask();

    }

    public  void setPause(boolean isRunning){
            this.isRunning = isRunning;
    }


    public void startTime(){
       if(!isRunning){
           if (timer != null){
               if(timerTask != null){
                   timerTask.cancel();
                   timerTask = new MyTimerTask();
               }
           }

                setp_counts = 0;
               isRunning = true;
               timer.schedule(timerTask,1000,1000);
       }else{
           Log.d("TAG","正在计时，不要重复操作");
       }
    }

    public void stopTime(){
        if(isRunning){
            timerTask.cancel();
            //timer.cancel();
            isRunning = false;
        }
    }


    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            if(isRunning){
                setp_counts++;
                String time =DataUtils.FormatTime(setp_counts);
                activity.getTime(time);
            }
        }
    }
}
