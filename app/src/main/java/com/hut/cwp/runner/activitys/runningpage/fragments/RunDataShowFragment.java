package com.hut.cwp.runner.activitys.runningpage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hut.cwp.runner.R;

/**
 * Created by Adminis on 2017/3/15.
 */

public class RunDataShowFragment extends Fragment {

    static RunDataShowFragment runDataShowFragment;

    private TextView text_time;
    private TextView text_miles;
    private TextView text_vector;


    public RunDataShowFragment() {

    }

    public static RunDataShowFragment getInstance() {

        if (runDataShowFragment == null) {
            runDataShowFragment = new RunDataShowFragment();
        }
        return runDataShowFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_show_rundata, container, false);
        initFindViewById(view);
        return view;
    }

    private void initFindViewById(View view) {

        text_miles = (TextView) view.findViewById(R.id.text_distance);
        text_vector = (TextView) view.findViewById(R.id.text_vector);
        text_time = (TextView) view.findViewById(R.id.text_time);
    }

    public void upVector(String vector) {

        String content = vector + getString(R.string.vector_unit);
        text_vector.setText(content);
    }

    public void upDistance(String distance) {

        String content = distance + getString(R.string.distance_unit);
        text_miles.setText(content);
    }

    public void upDuration(String duration) {

        text_time.setText(duration);
    }


}
