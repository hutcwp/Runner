package com.hut.cwp.runner.cwp.Impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.hut.cwp.runner.cwp.interfaces.ITakePhoto;

import java.io.File;

/**
 * Created by Adminis on 2017/3/2.
 */

public class TakePhotoByOriginal implements ITakePhoto {

    private static int REQUEST_ORIGINAL = 2;// 请求原图信号标识

    //未赋值
    private String sdPath;//SD卡的路径
    private String picPath;//图片存储路径


    @Override
    public void takePhoto(Activity activity) {

        //获取SD卡的路径
        sdPath = Environment.getExternalStorageDirectory().getPath();
        picPath = sdPath + "/" + "temp.png";
        Log.e("sdPath1", sdPath);

        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(new File(picPath));
        //为拍摄的图片指定一个存储的路径
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent2,REQUEST_ORIGINAL );
    }
}
