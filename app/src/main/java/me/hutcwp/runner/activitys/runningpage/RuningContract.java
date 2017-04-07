package me.hutcwp.runner.activitys.runningpage;


import me.hutcwp.runner.mvp.BasePresenter;
import me.hutcwp.runner.mvp.BaseView;

/**
 * Created by Adminis on 2017/3/29.
 */

public class RuningContract {

    interface View extends BaseView<Presenter> {

        void showMapFragment();

        void showDataFragment();

        void showRunningStateControl();

        void closeRunningStateControl();

    }

    interface Presenter extends BasePresenter {

        void stopRunning();

        void starRunning();

        void pauseRunning();

        void playMusic();

        void takePhoto();

        void openAblum();
    }

}
