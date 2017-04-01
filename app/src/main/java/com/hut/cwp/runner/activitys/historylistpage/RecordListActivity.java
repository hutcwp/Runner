package com.hut.cwp.runner.activitys.historylistpage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.utils.StateThemeUtil;
import com.hut.cwp.runner.dao.bean.PathRecord;
import com.hut.cwp.runner.dao.utils.DBUtil;
import com.hut.cwp.runner.activitys.historyshowpage.HistoryShowActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 所有轨迹list展示activity
 */
public class RecordListActivity extends Activity implements AdapterView.OnItemClickListener {

    private RecordAdapter mAdapter;
    private ListView mAllRecordListView;
    private DBUtil mDataBaseHelper;
    private List<PathRecord> mAllRecord = new ArrayList<>();
    public static final String RECORD_ID = "record_id";

    private Button btn_date, btn_back;

    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout empty_layout;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat fm_mouth = new SimpleDateFormat(
            "yyyy-MM-dd");
    private String date = fm_mouth.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateThemeUtil.uesState(this);


        setContentView(R.layout.ac_recordlist);

        initFindView();
        setOnClickListener();

        mDataBaseHelper = new DBUtil(this);
        mDataBaseHelper.open();

        searchRecordByDate(date);

        mAllRecordListView.setAdapter(mAdapter);
        mAllRecordListView.setOnItemClickListener(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }


    public void refreshData() {

        searchRecordByDate(date);

    }

    private void initFindView() {

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mAllRecordListView = (ListView) findViewById(R.id.recordlist);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_back = (Button) findViewById(R.id.btn_back);

        empty_layout = (RelativeLayout) findViewById(R.id.empty_layout);
    }


    public void setOnClickListener() {

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar now = Calendar.getInstance();
                now.set(mYear, mMonth, mDay);

                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        Calendar temp = Calendar.getInstance();
                        temp.clear();
                        temp.set(year, monthOfYear, dayOfMonth);

                        temp.getTime();

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(
                                "yyyy-MM-dd");
                        String date = formatter.format(temp.getTime());

                        searchRecordByDate(date);
                        Log.i("RecordListActivity", date);

                    }

                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

                dialog.setMaxDate(Calendar.getInstance());
                Calendar minDate = Calendar.getInstance();
                /**
                 * 在java Calendar类中，使用基于0的索引：1月是月0，12月是11月。
                 * 这个约定在java世界中广泛使用，例如原生Android DatePicker。
                 */
                minDate.set(2017, 0, 1);
                dialog.setMinDate(minDate);

                //设置是否在进行选择时对话框振动设备。默认为true。
                dialog.vibrate(false);

                dialog.show(getFragmentManager(), "DatePickerDialog");


            }
        });
    }


    private void searchAllRecordFromDB() {

        mAllRecord = mDataBaseHelper.queryRecordAll();
        mAdapter = new RecordAdapter(this, mAllRecord);
        mAllRecordListView.setAdapter(mAdapter);
        swipeRefresh.setRefreshing(false);

        if (mAllRecord.size() == 0) {
            empty_layout.setVisibility(View.VISIBLE);
        } else {
            empty_layout.setVisibility(View.GONE);
        }

    }


    private void searchRecordByDate(String date) {

        mAllRecord = mDataBaseHelper.queryRecordByDate(date);

        mAdapter = new RecordAdapter(this, mAllRecord);
        mAllRecordListView.setAdapter(mAdapter);
        swipeRefresh.setRefreshing(false);

        if (mAllRecord.size() == 0) {
            empty_layout.setVisibility(View.VISIBLE);
        } else {
            empty_layout.setVisibility(View.GONE);
        }

    }


    public void onBackClick(View view) {
        this.finish();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        PathRecord recorditem = (PathRecord) parent.getAdapter().getItem(
                position);
        Intent intent = new Intent(RecordListActivity.this,
                HistoryShowActivity.class);
        intent.putExtra(RECORD_ID, recorditem.getId());
        startActivity(intent);
    }
}
