package com.hut.cwp.runner.test.recordpath3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.history.HistoryShowActivity;
import com.hut.cwp.runner.test.database.DbAdapter;
import com.hut.cwp.runner.test.record.PathRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有轨迹list展示activity
 *
 */
public class RecordActivity extends Activity implements AdapterView.OnItemClickListener {

	private RecordAdapter mAdapter;
	private ListView mAllRecordListView;
	private DbAdapter mDataBaseHelper;
	private List<PathRecord> mAllRecord = new ArrayList<>();
	public static final String RECORD_ID = "record_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordlist);

		mAllRecordListView = (ListView) findViewById(R.id.recordlist);

		mDataBaseHelper = new DbAdapter(this);
		mDataBaseHelper.open();
		searchAllRecordFromDB();

		mAdapter = new RecordAdapter(this, mAllRecord);
		mAllRecordListView.setAdapter(mAdapter);
		mAllRecordListView.setOnItemClickListener(this);
	}

	private void searchAllRecordFromDB() {
		mAllRecord = mDataBaseHelper.queryRecordAll();
	}

	public void onBackClick(View view) {
		this.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		PathRecord recorditem = (PathRecord) parent.getAdapter().getItem(
				position);
		Intent intent = new Intent(RecordActivity.this,
				HistoryShowActivity.class);
		intent.putExtra(RECORD_ID, recorditem.getId());
		startActivity(intent);
	}
}
