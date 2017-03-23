package com.hut.cwp.runner.guidepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.homepage.MainActivity;
import com.hut.cwp.runner.test.recordpath3d.RecordActivity;


public class GuideActivity extends AppCompatActivity {

    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        btn_start = (Button) findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void showHistoryTrace(View view) {
//        Intent intent = new Intent(GuideActivity.this, HistoryShowActivity.class);
        Intent intent = new Intent(GuideActivity.this, RecordActivity.class);
        startActivity(intent);
    }


}
