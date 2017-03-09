package com.hut.cwp.runner.cwp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.cwp.clazz.SizeLabel;
import com.hut.cwp.runner.reoger.methords.DrawHistoryPath;

/**
 * Created by Adminis on 2017/3/7.
 */

public class Fragment2 extends Fragment {

    private MapView mapView;

    private BaiduMap baiduMap;

    private DrawHistoryPath historyPath;


    private TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.activity_map_history, container, false);


        initView(view);


        String html = "<font color='black' face='verdana'><size>31.1 </size>公里</font>";
        text.setText(Html.fromHtml(html, null, new SizeLabel(80)));

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String html = "<font color='red' face='verdana'><size>自定义Html标签</size>普通的大小</font>";
//                text.setText(Html.fromHtml(html,null,new SizeLabel(20)));

            }
        });


        return view;
    }


    private void initView(View view) {

        text = (TextView) view.findViewById(R.id.text_mils);
        mapView = (MapView) view.findViewById(R.id.activity_map);


        baiduMap = mapView.getMap();
        historyPath = new DrawHistoryPath(getActivity(), mapView);
        historyPath.startDrawMethord();
    }


//    @RequiresApi(api = Build.VERSION_CODES.N)
//    protected void showDatePickDlg() {
//
//        Calendar calendar = Calendar.getInstance();
//        DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityMapHistory.this, new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                String time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
//                ActivityMapHistory.this.mEditText.setText(time);
//                time = time + " 00:00:00";
//                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                try {
//                    long time1 = dateformat.parse(time).getTime();
//                    Log.d("TAG", "WW " + time1);
//                    historyPath.setHistoryTime((int) (time1/1000));//查看选中日期的轨迹
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                Log.d("TAG", "current" + System.currentTimeMillis());
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.show();
//
//    }


}
