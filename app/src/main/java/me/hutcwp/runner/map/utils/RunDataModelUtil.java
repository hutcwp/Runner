package me.hutcwp.runner.map.utils;


import android.annotation.SuppressLint;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 运动数据获取类
 */

public class RunDataModelUtil {

    /**
     * 获得卡路里
     * @param distance
     * @return
     */
    public static float getCalorie(float distance) {
        return distance * 14.1f;
    }

    /**
     *得到当前日期
     * @param mStartTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getcueDate(long mStartTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date curDate = new Date(mStartTime);
        String date = formatter.format(curDate);
        return date;
    }

    /**
     * 得到当前月份
     * @param mStartTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getcueMouth(long mStartTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM");
        Date curDate = new Date(mStartTime);
        String mouth = formatter.format(curDate);
        return mouth;
    }

    /**
     * 得到当前时间
     * @param mStartTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getcueTime(long mStartTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "hh:mm:ss");
        Date curDate = new Date(mStartTime);
        String t = formatter.format(curDate);
        return t;
    }

    /**
     * 得到平均速度
     * @param distance
     * @return
     */
    public static float getAverage(float distance, long duration) {

        return distance / duration;
    }

    /**
     * 获得里程
     * @param list
     * @return
     */
    public static float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }


    /**
     * 用于定位
     *
     * @param location
     * @return
     */
    public static synchronized LatLng getLocationLatLng(AMapLocation location) {
        //                    纬    度                经    度
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    public static String timeFormat(long duration) {

        StringBuilder time = new StringBuilder();

        int min = (int) duration / 60;
        int ss = (int) (duration % 60);
        int hh = (int) duration / 3600;

        min = min % 60;
        hh += min / 60;

        DecimalFormat df = new DecimalFormat("00");

        String hour = df.format(hh);
        String minutes = df.format(min);
        String second = df.format(ss);

        if (hh > 0) {
            time.append(hour + ":" + minutes + ":" + second);
        } else {
            time.append(minutes + ":" + second);

        }

        return time.toString();
    }


}
