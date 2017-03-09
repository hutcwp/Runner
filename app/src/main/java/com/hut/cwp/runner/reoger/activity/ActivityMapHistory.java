package com.hut.cwp.runner.reoger.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.hut.cwp.runner.R;
import com.hut.cwp.runner.reoger.methords.DrawHistoryPath;

/**
 * Created by 24540 on 2017/3/5.
 */

public class ActivityMapHistory extends AppCompatActivity {
    private MapView mapView;
    private BaiduMap baiduMap;

    private DrawHistoryPath historyPath;

    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map_history);

        initView();


//        mEditText.setOnTouchListener(new View.OnTouchListener() {
//
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    //showDatePickDlg();
//                    return true;
//                }
//                return false;
//            }
//        });
//        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    //showDatePickDlg();
//                  //  historyPath.setHistoryTime((int) (/1000));//查看选中日期的轨迹
//                }
//            }
//        });
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.activity_map);
        baiduMap = mapView.getMap();
        historyPath = new DrawHistoryPath(this, mapView);
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
