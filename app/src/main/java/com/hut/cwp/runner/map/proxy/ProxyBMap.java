package com.hut.cwp.runner.map.proxy;

import com.hut.cwp.runner.map.impl.BMapImpl;
import com.hut.cwp.runner.map.sub.SubMap;

/**
 * Created by Adminis on 2017/3/16.
 */

public class ProxyBMap extends SubMap{

    private BMapImpl bMapImpl;

    public ProxyBMap(BMapImpl bMapImpl) {

        this.bMapImpl = bMapImpl;
    }

    @Override
    public void startLocation() {

        bMapImpl.startLocation();
    }

    @Override
    public void pauseLocation() {

        bMapImpl.pauseLocation();
    }

    @Override
    public void closeLocation() {

        bMapImpl.closeLocation();
    }

    @Override
    public void lookHistory() {

        bMapImpl.lookHistory();
    }


}
