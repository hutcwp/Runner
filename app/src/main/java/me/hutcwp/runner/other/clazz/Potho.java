package me.hutcwp.runner.other.clazz;


import android.app.Activity;
import android.content.Intent;

import me.hutcwp.runner.other.Impl.CheckPhotoBySystem;
import me.hutcwp.runner.other.Impl.TakePhotoByScale;
import me.hutcwp.runner.other.interfaces.ICheckPhoto;
import me.hutcwp.runner.other.interfaces.ISavePhoto;
import me.hutcwp.runner.other.interfaces.ITakePhoto;

/**
 * Created by Adminis on 2017/3/2.
 */

public class Potho {

    private Activity activity;

    ISavePhoto mISavePhoto = new PhotoSave();
    ITakePhoto mITakePhoto = new TakePhotoByScale();
    ICheckPhoto mICheckPhoto = new CheckPhotoBySystem();


    public Potho(Activity activity) {
        this.activity = activity;
    }

    /**
     * takePotho 拍照
     */
    public void takePhoto() {

        mITakePhoto.takePhoto(activity);

    }


    public void setmITakePhoto(ITakePhoto mITakePhoto) {

        this.mITakePhoto = mITakePhoto;
    }


    public void savePhoto(int requestCode, int resultCode, Intent data) {

        mISavePhoto.savePhoto(requestCode,resultCode,data);
    }

    /**
     * openPotho 查看照片
     */
    public void checkPhoto() {

        mICheckPhoto.checkPhoto(activity);

    }


}
