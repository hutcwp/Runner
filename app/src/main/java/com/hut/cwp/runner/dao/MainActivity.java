package com.hut.cwp.runner.dao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.history.HistoryShowActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_insert, btn_select,btn_history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_insert = (Button) findViewById(R.id.btn_insert_db);
        btn_select = (Button) findViewById(R.id.btn_select_db);
        btn_history = (Button) findViewById(R.id.btn_history);

        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, HistoryShowActivity.class));
            }
        });


        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DBUtils.getInstance(MainActivity.this).selectFromDaily();
            }
        });


        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RunDailyData runDailyData = new RunDailyData();

                runDailyData.setDate("2017-02-21");
                runDailyData.setTime("08:20");
                runDailyData.setSpendtime(1);
                runDailyData.setCalorie(105.1f);
                runDailyData.setVector(1.4f);
                runDailyData.setDistance(14.1f);

                DBUtils.getInstance(MainActivity.this).insertToDailyTable(runDailyData);
            }
        });


    }




}
