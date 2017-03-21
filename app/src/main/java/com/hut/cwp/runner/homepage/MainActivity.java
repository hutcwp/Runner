package com.hut.cwp.runner.homepage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.homepage.fragments.AMapFragment;
import com.hut.cwp.runner.homepage.fragments.BMapFragment;
import com.hut.cwp.runner.homepage.fragments.DataFragment;
import com.hut.cwp.runner.map.IDataCallback;

public class MainActivity extends AppCompatActivity implements IDataCallback {

    private DataFragment dataFragment;
    private AMapFragment aMapFragment;

    private BMapFragment bMapFragment;


    private boolean isShow = false;

    private Button btn_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        btn_map = (Button) findViewById(R.id.btn_map);


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

                    showAMapFragment();
                    isShow = !isShow;
                } else {

                    showDataFragment();
                    isShow = !isShow;
                }

            }
        });

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


}
