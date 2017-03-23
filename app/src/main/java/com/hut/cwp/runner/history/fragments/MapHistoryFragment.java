package com.hut.cwp.runner.history.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.dao.DBUtils;
import com.hut.cwp.runner.dao.RunDailyData;
import com.hut.cwp.runner.test.database.DbAdapter;
import com.hut.cwp.runner.test.record.PathRecord;
import com.hut.cwp.runner.test.recordpath3d.RecordActivity;
import com.hut.cwp.runner.test.recordutil.Util;
import com.hut.cwp.runner.test.tracereplay.TraceRePlay;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Adminis on 2017/3/7.
 */

public class MapHistoryFragment extends Fragment implements
        AMap.OnMapLoadedListener, TraceListener, View.OnClickListener {


    private TextView text_distance, text_calorie, text_alltime, text_vector;

    private final static int AMAP_LOADED = 2;

    private RadioButton mOriginRadioButton, mGraspRadioButton;
    private ToggleButton mDisplaybtn;


    private AMap mAMap;
    private MapView mMapView = null;
    private Marker mOriginStartMarker, mOriginEndMarker, mOriginRoleMarker;
    private Marker mGraspStartMarker, mGraspEndMarker, mGraspRoleMarker;
    private Polyline mOriginPolyline;
    private Polyline mGraspPolyline;

    private int mRecordItemId;
    private List<LatLng> mOriginLatLngList;  //原始轨迹
    private List<LatLng> mGraspLatLngList;   //轨迹纠偏

    private boolean mGraspChecked = false;  //原始轨迹选定
    private boolean mOriginChecked = true;  //纠偏轨迹选定

    private ExecutorService mThreadPool;  //线程池，用于轨迹播放
    private TraceRePlay mRePlay;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AMAP_LOADED:
                    setupRecord();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_history, container, false);

        initView(view);

        mMapView.onCreate(savedInstanceState);

        setting();

        initMap();

        return view;
    }


    /**
     * 点击事件处理
     * @param v
     */

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.text_distance :
                readDataFromDB();
                break;
            case R.id.displaybtn:
                if (mDisplaybtn.isChecked()) {
                    startMove();
                    mDisplaybtn.setClickable(false);
                }
                break;
            case R.id.record_show_activity_grasp_radio_button:
                mGraspChecked = true;
                mOriginChecked = false;
                mGraspRadioButton.setChecked(true);
                mOriginRadioButton.setChecked(false);
                setGraspEnable(true);
                setOriginEnable(false);
                mDisplaybtn.setChecked(false);
                resetGraspRole();
                break;
            case R.id.record_show_activity_origin_radio_button:
                mOriginChecked = true;
                mGraspChecked = false;
                mGraspRadioButton.setChecked(false);
                mOriginRadioButton.setChecked(true);
                setGraspEnable(false);
                setOriginEnable(true);
                mDisplaybtn.setChecked(false);
                resetOriginRole();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        //关闭线程池
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
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


    /**
     * 设置点击事件
     */
    public void setting() {
        text_distance.setOnClickListener(this);
        mDisplaybtn.setOnClickListener(this);
        mOriginRadioButton.setOnClickListener(MapHistoryFragment.this);
        mGraspRadioButton.setOnClickListener(MapHistoryFragment.this);

    }

    /**
     * 从数据库中读取数据
     */
    public void readDataFromDB() {

        List<RunDailyData> list = DBUtils.getInstance(getActivity()).selectFromDaily();

        if (list.size() > 0) {

            upDateTextData(list.get(0));
        }

    }

    /**
     * 更新数据展示板
     * @param data
     */
    public void upDateTextData(RunDailyData data) {

        text_distance.setText(data.getDistance() + " m");
        text_alltime.setText(data.getSpendtime() + " h");
        text_calorie.setText(data.getCalorie() + " ka");
        text_vector.setText(data.getVector() + " m/s");

    }

    /**
     * 初始化View
     * @param view
     */
    private void initView(View view) {

        mMapView = (MapView) view.findViewById(R.id.amap);

        text_distance = (TextView) view.findViewById(R.id.text_distance);
        text_alltime = (TextView) view.findViewById(R.id.text_alltime);
        text_calorie = (TextView) view.findViewById(R.id.text_calorie);
        text_vector = (TextView) view.findViewById(R.id.text_vector);

        mGraspRadioButton = (RadioButton) view.findViewById(R.id.record_show_activity_grasp_radio_button);
        mOriginRadioButton = (RadioButton) view.findViewById(R.id.record_show_activity_origin_radio_button);

        mDisplaybtn = (ToggleButton) view.findViewById(R.id.displaybtn);

    }

    /**
     * 初始化地图和轨迹
     */
    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            //设置地图加载完成监听接口
            mAMap.setOnMapLoadedListener(this);
        }

        Intent recordIntent = getActivity().getIntent();

        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 3;

        mThreadPool = Executors.newFixedThreadPool(threadPoolSize);

        if (recordIntent != null) {
            mRecordItemId = recordIntent.getIntExtra(RecordActivity.RECORD_ID,
                    -1);
        }

    }

    /**
     * 人物开始移动
     */
    private void startMove() {

        if (mRePlay != null) {
            mRePlay.stopTrace();
        }
        if (mOriginChecked) {
            mRePlay = rePlayTrace(mOriginLatLngList, mOriginRoleMarker);
        } else if (mGraspChecked) {
            mRePlay = rePlayTrace(mGraspLatLngList, mGraspRoleMarker);
        }
    }

    /**
     * 轨迹回放方法
     */
    private TraceRePlay rePlayTrace(List<LatLng> list, final Marker updateMarker) {
        TraceRePlay replay = new TraceRePlay(list, 100,
                new TraceRePlay.TraceRePlayListener() {

                    @Override
                    public void onTraceUpdating(LatLng latLng) {
                        if (updateMarker != null) {
                            updateMarker.setPosition(latLng); // 更新小人实现轨迹回放
                        }
                    }

                    @Override
                    public void onTraceUpdateFinish() {
                        mDisplaybtn.setChecked(false);
                        mDisplaybtn.setClickable(true);
                    }
                });
        mThreadPool.execute(replay);
        return replay;
    }

    /**
     * 将纠偏后轨迹小人设置到起点
     */
    private void resetGraspRole() {
        if (mGraspLatLngList == null) {
            return;
        }
        LatLng startLatLng = mGraspLatLngList.get(0);
        if (mGraspRoleMarker != null) {
            mGraspRoleMarker.setPosition(startLatLng);
        }
    }

    /**
     * 将原始轨迹小人设置到起点
     */
    private void resetOriginRole() {
        if (mOriginLatLngList == null) {
            return;
        }
        LatLng startLatLng = mOriginLatLngList.get(0);
        if (mOriginRoleMarker != null) {
            mOriginRoleMarker.setPosition(startLatLng);
        }
    }

    /**
     * 控制地图上轨迹显示范围
     * 存在定位失效问题，进去后一片空白，定位到欧洲
     * @return
     */
    private LatLngBounds getBounds() {

        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            Log.i("mTAG", "mOriginLatLngList == null");
            return b.build();
        }
        for (int i = 0; i < mOriginLatLngList.size(); i++) {
            Log.i("mTAG", "mOriginLatLngList != null");
            b.include(mOriginLatLngList.get(i));
        }
        return b.build();

    }

    /**
     * 轨迹数据初始化
     */
    private void setupRecord() {

        // 轨迹纠偏初始化
        LBSTraceClient mTraceClient = new LBSTraceClient(
                getActivity().getApplicationContext());

        DbAdapter dbhelper = new DbAdapter(getActivity().getApplicationContext());

        dbhelper.open();
        PathRecord mRecord = dbhelper.queryRecordById(mRecordItemId);
        Log.i("mTAG", "mRecord==null " + (mRecord == null) + " " + mRecord);

        dbhelper.close();

        if (mRecord != null) {
            List<AMapLocation> recordList = mRecord.getPathline();
            AMapLocation startLoc = mRecord.getStartpoint();
            AMapLocation endLoc = mRecord.getEndpoint();
            if (recordList == null || startLoc == null || endLoc == null) {
                Log.i("mTAG", "recordList==null " + (recordList == null) + " " + recordList);
                Log.i("mTAG", "startLoc==null " + (startLoc == null) + " " + startLoc);
                Log.i("mTAG", "endLoc==null " + (endLoc == null) + " " + endLoc);
                return;
            }
            LatLng startLatLng = new LatLng(startLoc.getLatitude(),
                    startLoc.getLongitude());
            LatLng endLatLng = new LatLng(endLoc.getLatitude(),
                    endLoc.getLongitude());
            mOriginLatLngList = Util.parseLatLngList(recordList);

            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);

            List<TraceLocation> mGraspTraceLocationList = Util
                    .parseTraceLocationList(recordList);
            // 调用轨迹纠偏，将mGraspTraceLocationList进行轨迹纠偏处理
            mTraceClient.queryProcessedTrace(1, mGraspTraceLocationList,
                    LBSTraceClient.TYPE_AMAP, this);
        } else {
            Log.i("mTAG", "mRecord == null");
        }

    }

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     * @param startPoint 开始位置
     * @param endPoint 结束位置
     * @param originList 途径集合
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint,
                                List<LatLng> originList) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(
                Color.BLUE).addAll(originList));
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(
                endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.end)));
        try {

            mAMap.animateCamera(CameraUpdateFactory.newLatLng(startPoint));
            mAMap.setMinZoomLevel(15);
            /**
             * 下面的方法会出现定位错误
             */
//            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(),
//                    50));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("mTAG", "Exception " + e.getMessage());
        }

        mOriginRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.walk))));
    }

    /**
     * 设置是否显示原始轨迹
     *
     * @param enable
     */
    private void setOriginEnable(boolean enable) {
        mDisplaybtn.setClickable(true);
        if (mOriginPolyline == null || mOriginStartMarker == null
                || mOriginEndMarker == null || mOriginRoleMarker == null) {
            return;
        }
        if (enable) {
            mOriginPolyline.setVisible(true);
            mOriginStartMarker.setVisible(true);
            mOriginEndMarker.setVisible(true);
            mOriginRoleMarker.setVisible(true);
        } else {
            mOriginPolyline.setVisible(false);
            mOriginStartMarker.setVisible(false);
            mOriginEndMarker.setVisible(false);
            mOriginRoleMarker.setVisible(false);
        }
    }

    /**
     * 地图上添加纠偏后轨迹线路及起终点、轨迹动画小人
     */
    private void addGraspTrace(List<LatLng> graspList, boolean mGraspChecked) {
        if (graspList == null || graspList.size() < 2) {
            return;
        }
        LatLng startPoint = graspList.get(0);
        LatLng endPoint = graspList.get(graspList.size() - 1);
        mGraspPolyline = mAMap.addPolyline(new PolylineOptions()
                .setCustomTexture(
                        BitmapDescriptorFactory
                                .fromResource(R.drawable.grasp_trace_line))
                .width(40).addAll(graspList));
        mGraspStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start)));
        mGraspEndMarker = mAMap.addMarker(new MarkerOptions()
                .position(endPoint).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.end)));
        mGraspRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.walk))));
        if (!mGraspChecked) {
            mGraspPolyline.setVisible(false);
            mGraspStartMarker.setVisible(false);
            mGraspEndMarker.setVisible(false);
            mGraspRoleMarker.setVisible(false);
        }
    }

    /**
     * 设置是否显示纠偏后轨迹
     *
     * @param enable
     */
    private void setGraspEnable(boolean enable) {
        mDisplaybtn.setClickable(true);
        if (mGraspPolyline == null || mGraspStartMarker == null
                || mGraspEndMarker == null || mGraspRoleMarker == null) {
            return;
        }
        if (enable) {
            mGraspPolyline.setVisible(true);
            mGraspStartMarker.setVisible(true);
            mGraspEndMarker.setVisible(true);
            mGraspRoleMarker.setVisible(true);
        } else {
            mGraspPolyline.setVisible(false);
            mGraspStartMarker.setVisible(false);
            mGraspEndMarker.setVisible(false);
            mGraspRoleMarker.setVisible(false);
        }
    }

    /**
     * 地图加载监听器，用于在地图加载的时候画上历史轨迹
     */
    @Override
    public void onMapLoaded() {
        Message msg = handler.obtainMessage();
        msg.what = AMAP_LOADED;
        handler.sendMessage(msg);
    }

    /**
     * 轨迹纠偏完成数据回调
     */
    @Override
    public void onFinished(int arg0, List<LatLng> list, int arg2, int arg3) {
        addGraspTrace(list, mGraspChecked);
        mGraspLatLngList = list;
    }

    @Override
    public void onRequestFailed(int arg0, String arg1) {
        Toast.makeText(getActivity().getApplicationContext(), "轨迹纠偏失败:" + arg1,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTraceProcessing(int arg0, int arg1, List<LatLng> arg2) {

    }


}
