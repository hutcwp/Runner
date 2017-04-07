package me.hutcwp.runner.dao.bean;


import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于记录一条轨迹，
 * 起点、		 mStartPoint
 * 终点、		 mEndPoint
 * 轨迹中间点、	 mPathLinePoints
 * 距离、		 mDistance
 * 耗时、		 mDuration
 * 平均速度、	 mAveragespeed
 * 时间  		 mDate
 * 
 */
public class PathRecord {

	private AMapLocation mStartPoint;
	private AMapLocation mEndPoint;

	private String mDate;
	private int mId = 0;

	private String time;

	private List<AMapLocation> mPathLinePoints = new ArrayList<>();
	private float mDistance;
	private float mDuration;
	private float mAveragespeed;
	private float mCalorie;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}



	public PathRecord() {

	}

	public float getCalorie() {
		return mCalorie;
	}

	public void setCalorie(float mCalorie) {
		this.mCalorie = mCalorie;
	}


	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public AMapLocation getStartpoint() {
		return mStartPoint;
	}

	public void setStartpoint(AMapLocation startpoint) {
		this.mStartPoint = startpoint;
	}

	public AMapLocation getEndpoint() {
		return mEndPoint;
	}

	public void setEndpoint(AMapLocation endpoint) {
		this.mEndPoint = endpoint;
	}

	public List<AMapLocation> getPathline() {
		return mPathLinePoints;
	}

	public void setPathline(List<AMapLocation> pathline) {
		this.mPathLinePoints = pathline;
	}

	public float getDistance() {
		return mDistance;
	}

	public void setDistance(float mDistance) {
		this.mDistance = mDistance;
	}

	public float getDuration() {
		return mDuration;
	}

	public void setDuration(float mDuration) {
		this.mDuration = mDuration;
	}

	public float getAveragespeed() {
		return mAveragespeed;
	}

	public void setAveragespeed(float mAveragespeed) {
		this.mAveragespeed = mAveragespeed;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}

	public void addpoint(AMapLocation point) {
		mPathLinePoints.add(point);
	}

	@Override
	public String toString() {

		StringBuilder record = new StringBuilder();
//		record.append("recordSize:" + getPathline().size() + ", ");
		record.append("距离:" + getDistance() + "m   ");
		record.append("时长:" + getDuration() + "s");
		return record.toString();
	}
}
