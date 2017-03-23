package com.hut.cwp.runner.homepage;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.homepage.fragments.AMapFragment;
import com.hut.cwp.runner.homepage.fragments.BMapFragment;
import com.hut.cwp.runner.homepage.fragments.DataFragment;
import com.hut.cwp.runner.map.IDataCallback;

import java.lang.reflect.Field;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements IDataCallback, View.OnClickListener {

    private DataFragment dataFragment;
    private AMapFragment aMapFragment;

    private BMapFragment bMapFragment;


    private boolean isShow = false;

    private Button btn_map;
    private FloatingActionButton fbtn;

    private Button btn_pause, btn_finish;

    private RelativeLayout layout_contor;

    private FrameLayout.LayoutParams params;


    float startX;
    float startY;
    float currentX;
    float currentY;

    float sX, sY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        layout_contor = (RelativeLayout) findViewById(R.id.layout_contor);

        btn_map = (Button) findViewById(R.id.btn_map);
        fbtn = (FloatingActionButton) findViewById(R.id.fbtn_state_change);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_finish = (Button) findViewById(R.id.btn_finish);


        btn_pause.setOnClickListener(this);
        btn_finish.setOnClickListener(this);

        dataFragment = DataFragment.getInstance();
        aMapFragment = new AMapFragment();
        bMapFragment = new BMapFragment();


        if (!aMapFragment.isAdded()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.layout, aMapFragment);
            transaction.commit();
        }

        if (!dataFragment.isAdded()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.layout, dataFragment);
            transaction.commit();
        }

        if (!bMapFragment.isAdded()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.layout, bMapFragment);
            transaction.commit();
        }

        showAMapFragment();

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isShow) {
                    Toasty.normal(MainActivity.this, "map").show();
                    showAMapFragment();
                    isShow = !isShow;
                } else {

                    showDataFragment();
                    isShow = !isShow;
                }

            }
        });

        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (layout_contor.getVisibility() == View.VISIBLE) {

                    layout_contor.setVisibility(View.GONE);
                    btn_finish.setAnimation(AnimationUtils.makeOutAnimation(MainActivity.this, false));
                    btn_pause.setAnimation(AnimationUtils.makeOutAnimation(MainActivity.this, true));

                } else {

                    layout_contor.setVisibility(View.VISIBLE);
                    btn_pause.setAnimation(AnimationUtils.makeInAnimation(MainActivity.this, true));
                    btn_finish.setAnimation(AnimationUtils.makeInAnimation(MainActivity.this, true));

                }

            }
        });


        fbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();

                        sX = startX;
                        sY = startY;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        currentX = event.getRawX();
                        currentY = event.getRawY();

                        float dx = currentX - startX;
                        float dy = currentY - startY;

                        params = (FrameLayout.LayoutParams) fbtn.getLayoutParams();

                        params.leftMargin += dx;
//                        params.topMargin += dy;
                        params.bottomMargin -= dy;

                        params.topMargin += dy;

                        fbtn.setLayoutParams(params);


                        startX = currentX;
                        startY = currentY;

                        break;

                    case MotionEvent.ACTION_UP:

                        if (params.leftMargin > getScreenWidth() / 2) {

                            params.leftMargin = getScreenWidth() - fbtn.getWidth();
                        } else {
                            params.leftMargin = 0;
                        }


                        if (Math.abs(currentX - sX) > 8 || Math.abs(currentY - sY) > 8) {
                            return true;
                        }
                        break;
                }
                return false;
            }
        });

    }


    private int getScreenWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    private int getScreenHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    //状态栏的高
    private int getStateHeight() {

        Class<?> c = null;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }


    private void showBMapFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(bMapFragment);
        transaction.hide(aMapFragment);
        transaction.hide(dataFragment);
        transaction.commit();

    }

    private void showAMapFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(aMapFragment);
        transaction.hide(bMapFragment);
        transaction.hide(dataFragment);
        transaction.commit();

    }


    private void showDataFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(dataFragment);
        transaction.hide(aMapFragment);
        transaction.hide(bMapFragment);
        transaction.commit();

    }


    @Override
    public void upVector(float vector) {

        Log.e("MyTAG", " haha v:" + vector);
        if (dataFragment != null) {
            dataFragment.upVector(vector);
        }

    }


    @Override
    public void upDistance(float distance) {

        if (dataFragment != null) {
            dataFragment.upDistance(distance);
        }
//        Log.e("MyTAG", " haha distance:" + distance);

    }


    public void startLocation() {

//        bMapFragment.startLocation();
        aMapFragment.startLocation();
    }


    public void pauseLocation() {

//        Toasty.normal(this,"pasue", Toast.LENGTH_SHORT).show();
        aMapFragment.pauseLocation();
//        bMapFragment.pauseLocation();
    }


    public void closeLocation() {

//        bMapFragment.pauseLocation();
//        Toasty.normal(this,"stop", Toast.LENGTH_SHORT).show();
        aMapFragment.closeLocation();

        this.finish();
        overridePendingTransition(R.anim.inside, R.anim.exit);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pause:

                if (btn_pause.getText().equals("暂停")) {

                    pauseLocation();
                    btn_pause.setText("继续");
                } else {

                    startLocation();
                    btn_pause.setText("暂停");
                }
                break;

            case R.id.btn_finish:

                closeLocation();
                break;
        }

    }
}
