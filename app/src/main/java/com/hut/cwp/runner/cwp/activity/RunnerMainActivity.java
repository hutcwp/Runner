package com.hut.cwp.runner.cwp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hut.cwp.runner.R;


public class RunnerMainActivity extends AppCompatActivity {

    TextView map_text = (TextView) findViewById(R.id.map);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_main);




    }



}
