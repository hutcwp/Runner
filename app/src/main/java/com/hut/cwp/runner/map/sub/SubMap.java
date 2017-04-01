package com.hut.cwp.runner.map.sub;

/**
 * Map的抽象类
 */

public abstract class SubMap {


    //开启定位
    public abstract void startLocation();

    //暂停定位
    public abstract void pauseLocation();

    //关闭定位
    public abstract void closeLocation();

    //查看历史轨迹
    public abstract void lookHistory();

}
