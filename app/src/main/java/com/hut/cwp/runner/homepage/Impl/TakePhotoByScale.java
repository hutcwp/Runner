package com.hut.cwp.runner.homepage.Impl;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import com.hut.cwp.runner.homepage.interfaces.ITakePhoto;

/**
 * Created by Adminis on 2017/3/2.
 */

public class TakePhotoByScale implements ITakePhoto {

    private static int REQUEST_THUMBNAIL = 1;// 请求缩略图信号标识

    @Override
    public void takePhoto(Activity activity) {

        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 启动相机
        activity.startActivityForResult(intent1, REQUEST_THUMBNAIL);
    }
}
