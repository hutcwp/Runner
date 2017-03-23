package com.hut.cwp.runner.homepage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.homepage.Impl.PlayMusicBySystem;
import com.hut.cwp.runner.homepage.Impl.TakePhotoByScale;
import com.hut.cwp.runner.homepage.MainActivity;
import com.hut.cwp.runner.homepage.clazz.Music;
import com.hut.cwp.runner.homepage.clazz.Potho;
import com.hut.cwp.runner.homepage.interfaces.IPlayMusic;
import com.hut.cwp.runner.homepage.interfaces.ITakePhoto;

/**
 * Created by Adminis on 2017/3/15.
 */

public class DataFragment extends Fragment implements View.OnClickListener {


    static DataFragment dataFragment;

    private IPlayMusic mIPlayMusic;
    private ITakePhoto mITakePhoto;

    private Potho mPhoto;
    private Music mMusic;


    private Button btn_camera, btn_photo, btn_music;


    private TextView text_time, text_miles, text_vector;
    private TextView text_step;


    private MainActivity mActivity;

    public DataFragment() {

    }

    public static DataFragment getInstance() {

        if (dataFragment == null) {
            dataFragment = new DataFragment();
        }
        return dataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_data, container, false);

        mActivity = (MainActivity) getActivity();

        initFindViewById(view);
        setOnClickListener();

        Log.e("MyTAG", "activity" + (getActivity() == null));

        return view;

    }


    private void initFindViewById(View view) {

        btn_camera = (Button) view.findViewById(R.id.btn_camera);
        btn_photo = (Button) view.findViewById(R.id.btn_photo);
        btn_music = (Button) view.findViewById(R.id.btn_music);



        text_miles = (TextView) view.findViewById(R.id.text_distance);
        text_vector = (TextView) view.findViewById(R.id.text_vector);
        text_step = (TextView) view.findViewById(R.id.text_step);
        text_time = (TextView) view.findViewById(R.id.text_time);

        mIPlayMusic = new PlayMusicBySystem();
        mITakePhoto = new TakePhotoByScale();

        mPhoto = new Potho(getActivity());
        mMusic = new Music(getActivity());

    }

    private void setOnClickListener() {

        mMusic.setiPlayMusic(mIPlayMusic);
        mPhoto.setmITakePhoto(mITakePhoto);

        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_music.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_camera://第一种方法，获取压缩图

                mPhoto.takePhoto();
                break;

            case R.id.btn_photo://第二种方法，获取原图

                mPhoto.checkPhoto();
                break;

            case R.id.btn_music:

                mMusic.playMusic();
                break;

            default:
                break;
        }
    }


    public void upVector(float vector) {

        text_vector.setText(vector + " m/s");
//        Log.e("MyTAG", "V change");
    }

    public void upDistance(float distance) {

        text_miles.setText(distance + " m");
//        Log.e("MyTAG", "D change");
    }


}
