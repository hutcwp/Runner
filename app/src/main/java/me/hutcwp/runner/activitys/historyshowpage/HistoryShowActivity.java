package me.hutcwp.runner.activitys.historyshowpage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import me.hutcwp.runner.R;
import me.hutcwp.runner.activitys.historyshowpage.fragments.ChartFragment;
import me.hutcwp.runner.activitys.historyshowpage.fragments.MapFragment;
import me.hutcwp.runner.utils.StateThemeUtil;


public class HistoryShowActivity extends FragmentActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragments;

    private FragAdapter adapter;

    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateThemeUtil.uesState(this);
        setContentView(R.layout.ac_history);

        initView();
        initData();
        setting();

    }

    void initView() {

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btn_back = (Button) findViewById(R.id.btn_back);

    }

    void initData() {

        fragments = new ArrayList<>();
        fragments.add(new MapFragment());
        fragments.add(new ChartFragment());

        //构造适配器
        adapter = new FragAdapter(getSupportFragmentManager(), fragments);

    }

    void setting() {
        //设定适配器
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HistoryShowActivity.this.finish();
            }
        });
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
                    s = getString(R.string.vp_title_text);
                    break;
                case 1:
                    s = getString(R.string.vp_title_chart);
                    break;
                default:
                    s = getString(R.string.vp_title_other);
            }
            return s;
        }
    }

}
