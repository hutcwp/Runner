package com.hut.cwp.runner.activitys.runningpage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.activitys.runningpage.fragments.AMapShowFragment;
import com.hut.cwp.runner.activitys.runningpage.fragments.RunDataShowFragment;
import com.hut.cwp.runner.map.interfacer.IDataUpdateCallback;
import com.hut.cwp.runner.utils.PermissionUtil;
import com.hut.cwp.runner.utils.StateThemeUtil;


public class RuningActivity extends AppCompatActivity implements RuningContract.View , View.OnClickListener, IDataUpdateCallback {


    private boolean isShow = false;

    private FloatingActionButton fbtn;

    public Button btnPause, btnFinish;

    private RelativeLayout ly_control;

    private Toolbar toolbar;


    public RunDataShowFragment runDataShowFragment;
    public AMapShowFragment aMapShowFragment;


    private RuningContract.Presenter presenter;



    PermissionUtil.PermissionGrant permissionGrant = new PermissionUtil.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtil.CODE_CAMERA:
//                    Toast.makeText(RuningActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_ACCESS_FINE_LOCATION:
//                    Toast.makeText(RuningActivity.this, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_ACCESS_COARSE_LOCATION:
//                    Toast.makeText(HomeActivity.this, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StateThemeUtil.uesState(this);
        setContentView(R.layout.ac_running);

        initFindViewById();
        initData();
        setOnClickListener();

        //默认展示地图界面
        showMapFragment();
        //使用toolbar
        setSupportActionBar(toolbar);
        //定位权限申请
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_ACCESS_FINE_LOCATION, permissionGrant);

    }

    private void initData() {

        presenter = new RunningPresent(this,RuningActivity.this);
        runDataShowFragment = RunDataShowFragment.getInstance();
        //默认展示AMapFragment,即地图页面
        aMapShowFragment = new AMapShowFragment();
        if (!aMapShowFragment.isAdded()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.layout, aMapShowFragment);
            transaction.commit();
        }
        if (!runDataShowFragment.isAdded()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.layout, runDataShowFragment);
            transaction.commit();
        }

    }

    /**
     * 初始化View
     */
    private void initFindViewById() {
        toolbar = (Toolbar) findViewById(R.id.toobar);
        ly_control = (RelativeLayout) findViewById(R.id.layout_contor);
        fbtn = (FloatingActionButton) findViewById(R.id.fbtn_state_change);

        btnPause = (Button) findViewById(R.id.btn_pause);
        btnFinish = (Button) findViewById(R.id.btn_finish);
    }

    /**
     * 为空间设置OnClickListener
     */
    private void setOnClickListener() {
        btnPause.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        fbtn.setOnClickListener(this);
    }

    /**
     * 展示地图界面（aMapShowFragment）
     *
     */
    @Override
    public void showMapFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(aMapShowFragment);
        transaction.hide(runDataShowFragment);
        transaction.commit();
    }

    /**
     * 展示数据界面（runDataShowFragment）
     */
    @Override
    public void showDataFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(runDataShowFragment);
        transaction.hide(aMapShowFragment);
        transaction.commit();
    }

    /**
     * 展示跑步控制器(暂停，恢复和结束跑步的控制界面)
     */
    @Override
    public void showRunningStateControl() {
        if (ly_control.getVisibility() == View.GONE) {
            ly_control.setVisibility(View.VISIBLE);
            btnPause.setAnimation(AnimationUtils.makeInAnimation(RuningActivity.this, true));
            btnFinish.setAnimation(AnimationUtils.makeInAnimation(RuningActivity.this, true));
        }
    }

    /**
     * 关闭跑步控制器(暂停，恢复和结束跑步的控制界面)
     */
    @Override
    public void closeRunningStateControl() {
        if (ly_control.getVisibility() == View.VISIBLE) {
            ly_control.setVisibility(View.GONE);
            btnFinish.setAnimation(AnimationUtils.makeOutAnimation(RuningActivity.this, false));
            btnPause.setAnimation(AnimationUtils.makeOutAnimation(RuningActivity.this, true));
        }
    }

    /**
     * 点击事件监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pause:
                if (btnPause.getText().equals(getString(R.string.pause))) {
                    pauseLocation();
                    btnPause.setText(R.string.resume);
                } else {
                    startLocation();
                    btnPause.setText(R.string.pause);
                }
                break;
            case R.id.btn_finish:
                closeLocationWithSave();
                break;
            case R.id.fbtn_state_change:
                if (ly_control.getVisibility() == View.VISIBLE) {
                    closeRunningStateControl();

                } else {
                    showRunningStateControl();
                }
                break;
            default:
        }

    }

    /**
     * 开始定位
     */
    public void startLocation() {

        aMapShowFragment.startLocation();
    }

    /**
     * 暂停定位
     */
    public void pauseLocation() {

        aMapShowFragment.pauseLocation();
    }

    /**
     * 结束定位
     */
    public void closeLocationWithoutSave( ) {
        aMapShowFragment.closeLocationWithoutSaveRunData();
        this.finish();
        overridePendingTransition(R.anim.anim_inside, R.anim.anim_exit);
    }


    public void closeLocationWithSave( ) {
        aMapShowFragment.closeLocationWithSaveRunData();
        this.finish();
        overridePendingTransition(R.anim.anim_inside, R.anim.anim_exit);
    }


    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_others, menu);
        return true;
    }

    /**
     * 菜单的点击事件监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ablum:
                presenter.openAblum();
                break;
            case R.id.camera:
                if (!(ContextCompat.checkSelfPermission(RuningActivity.this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED)) {
                    presenter.takePhoto();
                } else {
                    //拍照的权限
                    PermissionUtil.requestPermission(this, PermissionUtil.CODE_CAMERA, permissionGrant);
                }
                break;
            case R.id.music:
                presenter.playMusic();
                break;
            case R.id.map_state_change:
                if (isShow) {
                    showMapFragment();
                    isShow = !isShow;
                } else {
                    showDataFragment();
                    isShow = !isShow;
                }
                break;
            default:
        }
        return true;
    }

    /**
     * 重写返回按钮，在Activity销毁之前询问是否需要保存数据
     */
    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(RuningActivity.this);
        dialog.setTitle(R.string.run_finish_text);

        LayoutInflater inflater = LayoutInflater.from(RuningActivity.this);
        View view = inflater.inflate(R.layout.dlg_content, null);

        dialog.setView(view);
        dialog.setCancelable(true);
        dialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //结束运动并保存数据
                closeLocationWithSave();
                RuningActivity.this.finish();
            }
        });
        dialog.setNegativeButton(R.string.without_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //结束运动
                closeLocationWithoutSave();
            }
        });
        dialog.setNeutralButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        dialog.show();

    }


    @Override
    public void setPresenter(RuningContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtil.requestPermissionsResult(this, requestCode, permissions, grantResults, permissionGrant);
    }

    /**
     * 更新速度
     * @param vector      更新的值
     * @param defaultShow 默认的值
     *                    下同
     */
    @Override
    public void upVector(String vector, String defaultShow) {
        if (runDataShowFragment != null) {
            runDataShowFragment.upVector(vector);
        } else if (defaultShow != null) {
            runDataShowFragment.upVector(defaultShow);
        }
    }

    /**
     * 更新里程
     * @param distance
     * @param defaultShow
     */
    @Override
    public void upDistance(String distance, String defaultShow) {
        if (runDataShowFragment != null) {
            runDataShowFragment.upDistance(distance);
        } else if (defaultShow != null) {
            runDataShowFragment.upDistance(defaultShow);
        }
    }

    /**
     * 更新时长
     * @param duration
     * @param defaultShow
     */
    @Override
    public void upDuration(String duration, String defaultShow) {
        if (runDataShowFragment != null) {
            runDataShowFragment.upDuration(duration);
        } else if (defaultShow != null) {
            runDataShowFragment.upDuration(defaultShow);
        }
    }


}
