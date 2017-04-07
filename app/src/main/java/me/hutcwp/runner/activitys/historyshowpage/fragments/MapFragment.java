package me.hutcwp.runner.activitys.historyshowpage.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

import me.hutcwp.runner.R;
import me.hutcwp.runner.activitys.historylistpage.RecordListActivity;
import me.hutcwp.runner.map.clazz.Trace;
import me.hutcwp.runner.map.impl.AmapImpl;

/**
 * Created by Adminis on 2017/3/7.
 */

public class MapFragment extends Fragment {

    private AMap mAMap;
    private MapView mMapView = null;
    private int mRecordItemId;
    private AmapImpl aImpl;
    private Trace trace;

    private TextView text_distance, text_calorie, text_duration, text_vector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_history_map, container, false);
        initView(view);
        initData();

        mMapView.onCreate(savedInstanceState);

        return view;
    }

    /**
     * 初始化View
     * @param view
     */
    public void initView(View view){

        mMapView = (MapView) view.findViewById(R.id.amap);

        text_distance = (TextView) view.findViewById(R.id.text_distance);
        text_duration = (TextView) view.findViewById(R.id.text_alltime);
        text_calorie = (TextView) view.findViewById(R.id.text_calorie);
        text_vector = (TextView) view.findViewById(R.id.text_vector);

    }

    /**
     * 初始化Data
     */
    public void initData(){

        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }

        Intent recordIntent = getActivity().getIntent();
        if (recordIntent != null) {
            mRecordItemId = recordIntent.getIntExtra(
                    RecordListActivity.RECORD_ID, -1);
        }

        trace = new Trace(mAMap, MapFragment.this, mRecordItemId);
        aImpl = new AmapImpl(trace);

    }


    public void upDataShowing(String distance,String duration,String vector,String calorie){

        text_distance.setText(distance);
        text_duration.setText(duration );
        text_calorie.setText(calorie);
        text_vector.setText(vector );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


}
