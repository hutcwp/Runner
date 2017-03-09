package com.hut.cwp.runner.cwp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.cwp.Impl.PlayMusicBySystem;
import com.hut.cwp.runner.cwp.Impl.TakePhotoByOriginal;
import com.hut.cwp.runner.cwp.Impl.TakePhotoByScale;
import com.hut.cwp.runner.cwp.clazz.Music;
import com.hut.cwp.runner.cwp.clazz.Potho;
import com.hut.cwp.runner.cwp.interfaces.IPlayMusic;
import com.hut.cwp.runner.cwp.interfaces.ITakePhoto;
import com.hut.cwp.runner.reoger.interfaces.IDistance;
import com.hut.cwp.runner.reoger.methords.Pedometer;
import com.hut.cwp.runner.reoger.methords.StartLBSThread;
import com.hut.cwp.runner.reoger.methords.TimeCounts;
import com.hut.cwp.runner.reoger.service.MileagSpeedService;

public class RunnerMainActivity extends AppCompatActivity implements View.OnClickListener, IDistance {


    private IPlayMusic mIPlayMusic = new PlayMusicBySystem();
    private ITakePhoto mITakePhoto = new TakePhotoByScale();

    private Potho mPhoto = new Potho(this);
    private Music mMusic = new Music(this);


    private Button btn_camera, btn_photo, btn_music;

    private Button btn_map, btn_pause;


    private TimeCounts time;

    private static TextView text_time, text_miles, text_vector;

    private static TextView text_step;

    private Pedometer p;

    private StartLBSThread threadLBS;

    private boolean isRunning = true;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_main);


        initFindViewById();

        setOnClickListener();

        mPhoto.setmITakePhoto(mITakePhoto);
        mPhoto.setmITakePhoto(new TakePhotoByOriginal());
        mMusic.setiPlayMusic(mIPlayMusic);


        time = new TimeCounts();
        p = new Pedometer(this);

        beginRun();

    }


    private void initFindViewById() {

        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_music = (Button) findViewById(R.id.btn_music);
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_pause = (Button) findViewById(R.id.btn_pause);

        text_miles = (TextView) findViewById(R.id.text_mils);
        text_vector = (TextView) findViewById(R.id.text_vector);
        text_step = (TextView) findViewById(R.id.text_step);
        text_time = (TextView) findViewById(R.id.text_time);


    }

    private void setOnClickListener() {

        btn_map.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_music.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_map:
                Intent intent = new Intent(RunnerMainActivity.this, RunnerMapActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_camera://第一种方法，获取压缩图

                mPhoto.takePhoto();
                break;

            case R.id.btn_photo://第二种方法，获取原图

                mPhoto.checkPhoto();
                break;

            case R.id.btn_music:

                mMusic.playMusic();

            case R.id.btn_pause:
                pauseRun();

            default:
                break;
        }
    }


    //获取里程信息
    @Override
    public void getDistance(final int distance) {

        Log.d("TAG", "在这里回传来自服务的数据" + distance);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (distance != -1) {
                    if (text_miles != null) {
                        text_miles.setText(distance + " 米");
                    }
                }
            }
        });
    }

    //获取当前的速度
    @Override
    public void getSpeed(final double v) {

        Log.d("TAG", "在这里获取速度信息:" + v);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (v != -1) {
                    if (text_vector != null) {
                        text_vector.setText(v + " 米/秒");
                    }
                }
            }
        });
    }

    //获取当前的时间
    @Override
    public void getTime(final String time) {
        Log.d("TAG", time);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (text_time != null) {
                    text_time.setText(time);

                }
            }
        });
    }

    @Override
    public void getSteps(final int steps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (text_step != null) {

                    text_step.setText("步数: " + steps);
                }
            }
        });
    }


    //开始跑步 ，测试 注意，不能连续按两次的开始，一个开始之后只能暂停、结束之后才能再次按开始跑步。这里就没有做判断了
    public void beginRun() {
        threadLBS = new StartLBSThread(this);
        threadLBS.startLBSThreadMethord();//地图地位选址开始

        Intent intent = new Intent(getApplicationContext(), MileagSpeedService.class);
        startService(intent);//启动记步+里程的服务

        p.register();//启动计步

        time.startTime();//启动计数器


    }


    @Override
    public void onBackPressed() {

        if(isRunning) {
            Toast.makeText(getApplicationContext(), "正在跑步中，不能中途退出，请结束后在退出本界面", Toast.LENGTH_LONG).show();
        }
        else{
            super.onBackPressed();
        }
        }

    //暂停跑步
    public void pauseRun() {
        time.setPause(!isRunning);
        isRunning = !isRunning;

        if (isRunning) {
            btn_pause.setText("暂停");
        } else {
            btn_pause.setText("恢复");
        }
    }

    //结束跑步
    public void stopRun(View view) {
        Intent intent = new Intent(getApplicationContext(), MileagSpeedService.class);
        stopService(intent);//停止获取里程和速度，步数的服务

        StartLBSThread.setHasOpenMap(false, null);//停止在地图上绘点
        threadLBS.stopTrace();//停止轨迹服务

        time.stopTime();//停止计时
        p.unRegister();
    }


}
