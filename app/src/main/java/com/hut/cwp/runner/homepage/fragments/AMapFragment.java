package com.hut.cwp.runner.homepage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.map.impl.AmapImpl;
import com.hut.cwp.runner.map.proxy.ProxyAMap;

/**
 * Created by Adminis on 2017/3/15.
 */

public class AMapFragment extends Fragment {


    private AMap aMap;
    private MapView bMapView;

    private AmapImpl aMapImpl;
    private ProxyAMap proxyAMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_amap, container, false);

        bMapView = (MapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        bMapView.onCreate(savedInstanceState);// 此方法必须重写


        if (aMap == null) {

            aMap = bMapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        }

        aMapImpl =  AmapImpl.getInstance(getActivity(), aMap, bMapView);
        proxyAMap = new ProxyAMap(aMapImpl);

        return view;

    }


    public void startLocation(){

        proxyAMap.startLocation();

    }

    public void pauseLocation(){

        proxyAMap.pauseLocation();

    }

    public void closeLocation(){

        proxyAMap.closeLocation();

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        bMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        bMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */


    @Override
    public void onDestroy() {
        super.onDestroy();
        bMapView.onDestroy();
//        proxyAMap.closeLocation();
    }




}
