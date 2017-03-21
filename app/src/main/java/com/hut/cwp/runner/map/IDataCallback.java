package com.hut.cwp.runner.map;

/**
 * Created by Adminis on 2017/3/15.
 */

public interface IDataCallback {

    //得到当前速度
    public abstract void upVector(float vector);

    //得到总距离
    public abstract void upDistance(float distance);
}
