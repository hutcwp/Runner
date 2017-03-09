package com.hut.cwp.runner.cwp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.MapView;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.reoger.methords.StartLBSThread;


public class RunnerMapActivity extends AppCompatActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_map);
        mMapView = (MapView) findViewById(R.id.mMapView);
        mMapView.showZoomControls(false);
        StartLBSThread.setHasOpenMap(true,mMapView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RunnerMapActivity.this,RunnerMainActivity.class);
        startActivity(intent);
    }
}
