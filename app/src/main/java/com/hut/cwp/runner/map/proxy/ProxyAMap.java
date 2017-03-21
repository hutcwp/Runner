package com.hut.cwp.runner.map.proxy;

import com.hut.cwp.runner.map.impl.AmapImpl;
import com.hut.cwp.runner.map.sub.SubMap;

/**
 * Created by Adminis on 2017/3/14.
 */

public class ProxyAMap extends SubMap{

    private AmapImpl mAmapImpl ;

    public ProxyAMap(AmapImpl amapImpl) {

        this.mAmapImpl = amapImpl;
        mAmapImpl.init();
    }


    @Override
    public void startLocation() {

        mAmapImpl.startLocation();
    }

    @Override
    public void pauseLocation() {

        mAmapImpl.pauseLocation();
    }

    @Override
    public void closeLocation() {

        mAmapImpl.closeLocation();
    }

    @Override
    public void lookHistory() {

        mAmapImpl.lookHistory();
    }

}
