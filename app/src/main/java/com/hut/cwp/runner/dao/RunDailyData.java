package com.hut.cwp.runner.dao;

/**
 * Created by Adminis on 2017/3/20.
 */

public class RunDailyData {

    /**
     * 运动数据实体类
     * id        序号
     * time      时间
     * spendtime   总时间
     * calorie   卡路里
     * vector    速度
     * distance  路程
     */

    private int id;
    private String time;
    private String date ;
    private float spendtime;
    private float calorie;
    private float vector;
    private float distance;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getSpendtime() {
        return spendtime;
    }

    public void setSpendtime(float spendtime) {
        this.spendtime = spendtime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public float getVector() {
        return vector;
    }

    public void setVector(float vector) {
        this.vector = vector;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
