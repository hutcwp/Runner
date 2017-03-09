package com.hut.cwp.runner.cwp.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.cwp.fragment.ChartHistoryFragment;
import com.hut.cwp.runner.cwp.fragment.MapHistoryFragment;

import java.util.ArrayList;
import java.util.List;


public class HistoryShowActivity extends FragmentActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    List<Fragment> fragments;


    private FragAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        initView();
        initData();
        setting();


    }

    void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

    }

    void initData() {


        fragments = new ArrayList<>();
        fragments.add(new ChartHistoryFragment());
        fragments.add(new MapHistoryFragment());
        //构造适配器
        adapter = new FragAdapter(getSupportFragmentManager(), fragments);


    }

    void setting() {

        //设定适配器
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        tabLayout.setupWithViewPager(viewPager);
    }




    public class FragAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            // TODO Auto-generated constructor stub
            mFragments = fragments;

        }


        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String s;
            switch (position) {
                case 0:
                    s = "图表";
                    break;
                case 1:
                    s = "地图";
                    break;
                default:
                    s = "你猜";
            }
            return s;
        }
    }

}
