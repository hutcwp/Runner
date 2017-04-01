package com.hut.cwp.runner.activitys.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.activitys.about.About;
import com.hut.cwp.runner.activitys.historylistpage.RecordListActivity;
import com.hut.cwp.runner.activitys.runningpage.RuningActivity;

/***
 * 软件主界面
 */
public class HomeActivity extends AppCompatActivity {

    private Button btnStartRunning;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_home);

        initFindView();
        setOnclick();

    }


    public void initFindView(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);
        btnStartRunning = (Button) findViewById(R.id.btn_start);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);

    }

    /**
     * 设置点击事件
     */
    public void setOnclick(){

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.item_home:
                        break;
                    case R.id.item_about:
                        startActivity(new Intent(HomeActivity.this, About.class));
                        break;
                    case R.id.item_liscense:
                        break;
                    default:
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        btnStartRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RuningActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 展示历史轨迹
     * @param view
     */
    public void showHistoryTrace(View view) {

        Intent intent = new Intent(HomeActivity.this, RecordListActivity.class);
        startActivity(intent);
    }

    /**
     * 呼出抽屉菜单
     * @param view
     */
    public void showNaviMenu(View view) {

       mDrawerLayout.openDrawer(GravityCompat.START);
    }


}
