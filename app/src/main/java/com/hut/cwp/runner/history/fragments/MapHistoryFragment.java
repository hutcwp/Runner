package com.hut.cwp.runner.history.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.dao.DBUtils;
import com.hut.cwp.runner.dao.RunData;

import java.util.List;

/**
 * Created by Adminis on 2017/3/7.
 */

public class MapHistoryFragment extends Fragment {

    MapView mMapView = null;


    private TextView text_distance,text_calorie,text_alltime,text_vector;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_map_history, container, false);

        initView(view);

        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

//        String html = "<font color='black' face='verdana'><size>2 </size>m</font>";
//        text_distance.setText(Html.fromHtml(html, null, new SizeLabel(60)));

        text_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String html = "<font color='red' face='verdana'><size>自定义Html标签</size>普通的大小</font>";
//                text_mils.setText(Html.fromHtml(html,null,new SizeLabel(20)));

                readDataFromDB();
            }
        });

        return view;
    }


    public void readDataFromDB(){

        List<RunData> list = DBUtils.getInstance(getActivity()).select();

        if (list.size()>0){

            upDateTextData(list.get(0));
        }

    }


    public void upDateTextData(RunData data){

        text_distance.setText(data.getDistance()+" m");
        text_alltime.setText(data.getAlltime()+" h");
        text_calorie.setText(data.getCalorie()+" ka");
        text_vector.setText(data.getVector()+" m/s");

    }


    private void initView(View view) {

        text_distance = (TextView) view.findViewById(R.id.text_distance);
        text_alltime = (TextView) view.findViewById(R.id.text_alltime);
        text_calorie = (TextView) view.findViewById(R.id.text_calorie);
        text_vector= (TextView) view.findViewById(R.id.text_vector);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
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
