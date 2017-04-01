package com.hut.cwp.runner.dao.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hut.cwp.runner.dao.bean.PathRecord;
import com.hut.cwp.runner.dao.bean.RunData;
import com.hut.cwp.runner.map.utils.TraceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库相关操作，用于存取轨迹记录
 * 
 */
public class DBUtil {

	private static final String RECORD_TABLE = "record";
	public static final String RECORD_KEY_ROWID = "id";
	public static final String RECORD_KEY_DISTANCE = "distance";
	public static final String RECORD_KEY_DURATION = "duration";
	public static final String RECORD_KEY_SPEED = "speed";
	public static final String RECORD_KEY_LINE = "pathline";
	public static final String RECORD_KEY_STRAT = "stratpoint";
	public static final String RECORD_KEY_END = "endpoint";
	public static final String RECORD_KEY_TIME = "time";
	public static final String RECORD_KEY_DATE = "date";
	public static final String RECORD_KEY_CALORIE = "calorie";


	private static final String RUNDATA_TABLE = "rundata";
	public static final String RUNDATA_DATE_MOUTH = "date_mouth";
	public static final String RUNDATA_KEY_ROWID = "id";
	public static final String RUNDATA_KEY_DATE = "date";
	public static final String RUNDATA_KEY_COLORIE = "calorie";
	public static final String RUNDATA_KEY_DISTANCE = "distance";
	public static final String RUNDATA_KEY_DURATION = "duration";
	public static final String RUNDATA_KEY_SPEED = "speed";

	private Context mContext = null;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private static final int DATABASE_VERSION = 1;

	private final static String DATABASE_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/recordPath";

	static final String DATABASE_NAME = DATABASE_PATH + "/" + "runner_record.db";


	private static final String RECORD_CREATE = "create table if not exists record("
			+ RECORD_KEY_ROWID
			+ " integer primary key autoincrement,"
			+ "stratpoint STRING,"
			+ "endpoint STRING,"
			+ "pathline STRING,"
			+ "distance float,"
			+ "duration float,"
			+ "speed float,"
			+ "calorie float,"
			+ "time String,"
			+ "date STRING" + ");";

	public static final String RUNDATA_CREATE = "create table if not exists rundata ("
			+"id integer primary key autoincrement,"
			+"date text,"
			+"date_mouth text,"
			+"duration float,"
			+"calorie float,"
			+"speed float,"
			+"distance float)";


	public DBUtil(Context ctx) {
		this.mContext = ctx;
		dbHelper = new DatabaseHelper(mContext);
	}


	public DBUtil open() throws SQLException {

		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}


//	public Cursor getall() {
//		return db.rawQuery("SELECT * FROM record", null);
//	}
//
//	// remove an entry
//	public boolean delete(long rowId) {
//
//		return db.delete(RECORD_TABLE, "id=" + rowId, null) > 0;
//	}

	/**
	 * 数据库存入一条轨迹
	 *
	 */
	public long createRecord(PathRecord record ,String pathline ,String stratpoint ,String endpoint ) {

		ContentValues args = new ContentValues();

		args.put("distance", record.getDistance());
		args.put("calorie", record.getCalorie());
		args.put("duration", record.getDuration());
		args.put("speed", record.getAveragespeed());
		args.put("pathline", pathline);
		args.put("stratpoint", stratpoint);
		args.put("endpoint", endpoint);
		args.put("date", record.getDate());
		args.put("time", record.getTime());

		return db.insert(RECORD_TABLE, null, args);
	}

	/**
	 * 查询所有轨迹记录
	 * 
	 * @return
	 */
	public List<PathRecord> queryRecordAll() {
		List<PathRecord> allRecord = new ArrayList<>();

		Cursor allRecordCursor = db.query(RECORD_TABLE, getColumns(), null,
				null, null, null, null);
		while (allRecordCursor.moveToNext()) {
			PathRecord record = new PathRecord();

			record.setId(allRecordCursor.getInt(allRecordCursor
					.getColumnIndex(RECORD_KEY_ROWID)));

			record.setDistance(allRecordCursor.getFloat(allRecordCursor
					.getColumnIndex(RECORD_KEY_DISTANCE)));

			record.setDuration(allRecordCursor.getFloat(allRecordCursor
					.getColumnIndex(RECORD_KEY_DURATION)));

			record.setDate(allRecordCursor.getString(allRecordCursor
					.getColumnIndex(RECORD_KEY_DATE)));

			record.setCalorie(allRecordCursor.getFloat(allRecordCursor
					.getColumnIndex(RECORD_KEY_CALORIE)));

			record.setTime(allRecordCursor.getString(allRecordCursor
					.getColumnIndex(RECORD_KEY_TIME)));

			record.setAveragespeed(allRecordCursor.getFloat(allRecordCursor
					.getColumnIndex(RECORD_KEY_SPEED)));

			String lines = allRecordCursor.getString(allRecordCursor
					.getColumnIndex(RECORD_KEY_LINE));

			record.setPathline(TraceUtil.parseLocations(lines));

			record.setStartpoint(TraceUtil.parseLocation(allRecordCursor
					.getString(allRecordCursor
							.getColumnIndex(RECORD_KEY_STRAT))));
			record.setEndpoint(TraceUtil.parseLocation(allRecordCursor
					.getString(allRecordCursor
							.getColumnIndex(RECORD_KEY_END))));
			allRecord.add(record);
		}
		// 使用Reverse方法可以根据元素的自然顺序 对指定列表按降序进行排序。
		Collections.reverse(allRecord);
		return allRecord;
	}

	/**
	 * 按照id查询
	 * 
	 * @param mRecordItemId
	 * @return
	 */
	public PathRecord queryRecordById(int mRecordItemId) {

		String where = RECORD_KEY_ROWID + "=?";
		String[] selectionArgs = new String[] { String.valueOf(mRecordItemId) };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where,
				selectionArgs, null, null, null);
		PathRecord record = new PathRecord();
		if (cursor.moveToNext()) {
			record.setId(cursor.getInt(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_ROWID)));
			record.setDistance(cursor.getFloat(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_DISTANCE)));
			record.setDuration(cursor.getFloat(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_DURATION)));
			record.setCalorie(cursor.getFloat(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_CALORIE)));
			record.setDate(cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_DATE)));
			record.setTime(cursor.getString(cursor
					.getColumnIndex(RECORD_KEY_TIME)));
			record.setAveragespeed(cursor.getFloat(cursor
					.getColumnIndex(RECORD_KEY_SPEED)));

			String lines = cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_LINE));

			record.setPathline(TraceUtil.parseLocations(lines));

			record.setStartpoint(TraceUtil.parseLocation(cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_STRAT))));

			record.setEndpoint(TraceUtil.parseLocation(cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_END))));

		}
		cursor.close();
		return record;
	}


	public List<PathRecord> queryRecordByDate(String date) {

		List<PathRecord> allRecord = new ArrayList<>();

		String where = RECORD_KEY_DATE + "=?";
		String[] selectionArgs = new String[] { String.valueOf(date) };
		Cursor cursor = db.query(RECORD_TABLE, getColumns(), where,
				selectionArgs, null, null, null);
		while (cursor.moveToNext()) {
			PathRecord record = new PathRecord();
			record.setId(cursor.getInt(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_ROWID)));
			record.setDistance(cursor.getFloat(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_DISTANCE)));
			record.setDuration(cursor.getFloat(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_DURATION)));
			record.setCalorie(cursor.getFloat(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_CALORIE)));
			record.setDate(cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_DATE)));
			record.setTime(cursor.getString(cursor
					.getColumnIndex(RECORD_KEY_TIME)));
			record.setAveragespeed(cursor.getFloat(cursor
					.getColumnIndex(RECORD_KEY_SPEED)));

			String lines = cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_LINE));

			record.setPathline(TraceUtil.parseLocations(lines));

			record.setStartpoint(TraceUtil.parseLocation(cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_STRAT))));

			record.setEndpoint(TraceUtil.parseLocation(cursor.getString(cursor
					.getColumnIndex(DBUtil.RECORD_KEY_END))));

			allRecord.add(record);
		}
		cursor.close();
		return allRecord;
	}



	private String[] getColumns() {

		return new String[] {
				RECORD_KEY_ROWID, RECORD_KEY_DISTANCE,
				RECORD_KEY_DURATION, RECORD_KEY_SPEED,
				RECORD_KEY_LINE, RECORD_KEY_STRAT, RECORD_KEY_END,
				RECORD_KEY_DATE, RECORD_KEY_CALORIE,RECORD_KEY_TIME};
	}

	/**
	 * 通过匹配Date更新今日跑步数据
	 * @param data
	 * 如果存在 数据库中存在Date为data.Date的数据就更新
	 * 否则，创建一条Date为data.Date的数据记录
	 */
	public void upRunDataByDate(RunData data) {
		Cursor cursor = db.query(RUNDATA_TABLE, null, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String date = cursor.getString(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DATE));
				if (data.getDate().equals(date)) {
					int id = cursor.getInt(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_ROWID));

					float durtion = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DURATION)) + data.getDuration();
					float calorie = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_COLORIE)) + data.getCalorie();
					float distance = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DISTANCE)) + data.getDistance();
					float vector = distance / durtion;

					ContentValues values = new ContentValues();
					values.put(DBUtil.RUNDATA_KEY_DURATION, durtion);
					values.put(DBUtil.RUNDATA_KEY_COLORIE, calorie);
					values.put(DBUtil.RUNDATA_KEY_SPEED, vector);
					values.put(DBUtil.RUNDATA_KEY_DISTANCE, distance);
					db.update(DBUtil.RUNDATA_TABLE, values, "id=?", new String[]{id + ""});

					return;
				}

			} while (cursor.moveToNext());
		}
		cursor.close();
		createNewRunData(data);

	}

	/**
	 * 创建新的RunData记录
	 * @param data
	 */
	private void createNewRunData(RunData data) {
		if (data != null) {
			ContentValues values = new ContentValues();
			values.put(DBUtil.RUNDATA_DATE_MOUTH,data.getDate_mouth());
			values.put(DBUtil.RUNDATA_KEY_DATE, data.getDate());
			values.put(DBUtil.RUNDATA_KEY_DURATION, data.getDuration());
			values.put(DBUtil.RUNDATA_KEY_COLORIE, data.getCalorie());
			values.put(DBUtil.RUNDATA_KEY_SPEED, data.getVector());
			values.put(DBUtil.RUNDATA_KEY_DISTANCE, data.getDistance());
			db.insert(RUNDATA_TABLE, null, values);
		}
	}


	/**
	 * 查询出数据库中所有的RunData记录，以List的方式返回
	 * @return
	 */
	public List<RunData> queryRunDataByDate(String date) {

		String where = RUNDATA_KEY_DATE + "=?";
		String[] selectionArgs = new String[] { String.valueOf(date) };

		List<RunData> datas = new ArrayList<>();
		Cursor cursor = db.query(RUNDATA_TABLE, null, where, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			do {

				int id = cursor.getInt(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_ROWID));
				Float duration = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DURATION));
				Float calorie = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_COLORIE));
				Float vector = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_SPEED));
				Float distance = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DISTANCE));

				RunData runData = new RunData();
				runData.setId(id);
				runData.setDate(date);
				runData.setDuration(duration);
				runData.setCalorie(calorie);
				runData.setVector(vector);
				runData.setDistance(distance);

				datas.add(runData);


			} while (cursor.moveToNext());
		}
		cursor.close();
		return datas;
	}

	/**
	 * 查询当月的记录
	 * @param mouth
	 * @return
	 */
	public List<RunData> queryRundataWithMouth(String mouth) {

		String where = RUNDATA_DATE_MOUTH + "=?";
		String[] selectionArgs = new String[] { String.valueOf(mouth) };

		List<RunData> datas = new ArrayList<>();
		Cursor cursor = db.query(RUNDATA_TABLE, null, where, selectionArgs, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_ROWID));
				Float duration = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DURATION));
				Float calorie = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_COLORIE));
				Float vector = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_SPEED));
				Float distance = cursor.getFloat(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DISTANCE));
				String date = cursor.getString(cursor.getColumnIndex(DBUtil.RUNDATA_KEY_DATE));

				RunData runData = new RunData();
				runData.setId(id);
				runData.setDate(date);
				runData.setDuration(duration);
				runData.setCalorie(calorie);
				runData.setVector(vector);
				runData.setDistance(distance);
				runData.setDate_mouth(mouth);

				datas.add(runData);

			} while (cursor.moveToNext());
		}
		cursor.close();

		return datas;
	}


	/**
	 * DatabaseHelper类
	 */

	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(RECORD_CREATE);
			db.execSQL(RUNDATA_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}


}
