package me.hutcwp.runner.map.interfacer;

/**
 * Created by Adminis on 2017/3/15.
 */

public interface IDataUpdateCallback {

    //得到当前速度
    void upVector(String vector,String defaultShow);

    //得到总距离
    void upDistance(String distance,String defaultShow);

    //得到时间
    void upDuration(String duration,String defaultShow);
}
