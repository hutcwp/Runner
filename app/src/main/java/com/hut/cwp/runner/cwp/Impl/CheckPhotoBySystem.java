package com.hut.cwp.runner.cwp.Impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.hut.cwp.runner.cwp.interfaces.ICheckPhoto;

import java.io.File;

/**
 * Created by Adminis on 2017/3/2.
 */

public class CheckPhotoBySystem implements ICheckPhoto {


    @Override
    public void checkPhoto(Activity activity) {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "output_iamge.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();

            }

            outputImage.createNewFile();

        } catch (Exception e) {

            e.printStackTrace();
        }
        Uri imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, 2);
    }
}
