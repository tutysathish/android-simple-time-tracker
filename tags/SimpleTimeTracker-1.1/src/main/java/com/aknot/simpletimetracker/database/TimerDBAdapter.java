package com.aknot.simpletimetracker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtil;

/**
 * @author Aknot
 */
public final class TimerDBAdapter {

	private final CategoryDBAdapter categoryDBAdapter;

	public TimerDBAdapter(Context context) {
		categoryDBAdapter = new CategoryDBAdapter(context);
	}

	public long createTimer(final TimerRecord timerRecord) {
		return DatabaseInstance.getDatabase().insert(DatabaseOpenHelper.TIMER_TABLE, null, timerRecordContentValuesList(timerRecord));
	}

	public long updateTimer(final TimerRecord timerRecord) {
		return DatabaseInstance.getDatabase().update(DatabaseOpenHelper.TIMER_TABLE, timerRecordContentValuesList(timerRecord),
				"_id = " + timerRecord.getRowId(), null);
	}

	public boolean delete(final long rowId) {
		Log.d("DEBUG_QUERY", "Row id : " + rowId);
		return DatabaseInstance.getDatabase().delete(DatabaseOpenHelper.TIMER_TABLE, "_id=" + rowId, null) > 0;
	}

	public boolean deleteAll() {
		return DatabaseInstance.getDatabase().delete(DatabaseOpenHelper.TIMER_TABLE, null, null) > 0;
	}

	public boolean deleteForDateRange(long startDate, long endDate) {
		Log.d("DEBUG_QUERY", "Start Time : " + DateTimeUtil.getDateForMillis(startDate));
		Log.d("DEBUG_QUERY", "End Time : " + DateTimeUtil.getDateForMillis(endDate));
		return DatabaseInstance.getDatabase().delete(DatabaseOpenHelper.TIMER_TABLE, "start_time>=" + startDate + " and start_time <=" + endDate, null) > 0;
	}

	public TimerRecord fetchByRowID(final long rowId) throws SQLException {
		Log.d("DEBUG_QUERY", "Row id : " + rowId);
		Cursor cursor = DatabaseInstance.getDatabase().query(true, DatabaseOpenHelper.TIMER_TABLE, columnList(), "_id=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		TimerRecord timerRecord = fillTimerRecordFromCursor(cursor);
		cursor.close();
		return timerRecord;
	}

	public List<TimerRecord> fetchLastTimerRecordsByCategory(int categoryId) {
		List<TimerRecord> result = new ArrayList<TimerRecord>();
		Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(),
				"start_time >= " + DateTimeUtil.getMinTimeMillisWeek() + " and category_id = " + categoryId, null, null, null, "start_time desc");
		while (cursor.moveToNext()) {
			result.add(fillTimerRecordFromCursor(cursor));
		}
		cursor.close();
		return result;
	}

	public List<TimerRecord> fetchTimerRecordsByDateRange(long startDate, long endDate) {
		List<TimerRecord> result = new ArrayList<TimerRecord>();
		Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(),
				"start_time >= " + startDate + " and end_time <= " + endDate, null, null, null, "start_time desc");
		while (cursor.moveToNext()) {
			result.add(fillTimerRecordFromCursor(cursor));
		}
		cursor.close();
		return result;
	}

	public long totalForTodayAndByCategory(CategoryRecord category) {
		return totalByRangeByCategory(DateTimeUtil.getMinTimeMillisToday(), DateTimeUtil.getMaxTimeMillisToday(), category.getRowId());
	}

	public long totalForWeekAndByCategory(CategoryRecord category) {
		return totalByRangeByCategory(DateTimeUtil.getMinTimeMillisWeek(), DateTimeUtil.getMaxTimeMillisToday(), category.getRowId());
	}

	private long totalByRangeByCategory(long startTime, long endTime, int categoryRowId) {
		Log.d("DEBUG_QUERY", "Start Time : " + DateTimeUtil.getDateForMillis(startTime));
		Log.d("DEBUG_QUERY", "End Time : " + DateTimeUtil.getDateForMillis(endTime));
		Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(),
				"start_time >= " + startTime + " and end_time <= " + endTime, null, null, null, null);
		long totalMillisForCategory = 0;
		while (cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndexOrThrow("category_id")) == categoryRowId) {
				totalMillisForCategory += cursor.getLong(cursor.getColumnIndexOrThrow("end_time")) - cursor.getLong(cursor.getColumnIndexOrThrow("start_time"));
			}
		}
		cursor.close();
		return totalMillisForCategory;
	}

	private TimerRecord fillTimerRecordFromCursor(Cursor cursor) {
		TimerRecord timerRecord = new TimerRecord();
		timerRecord.setRowId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
		timerRecord.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow("start_time")));
		timerRecord.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow("end_time")));
		timerRecord.setCategory(categoryDBAdapter.fetchByRowID(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))));
		return timerRecord;
	}

	private String[] columnList() {
		List<String> columns = new ArrayList<String>();
		columns.add("_id");
		columns.add("category_id");
		columns.add("start_time");
		columns.add("end_time");
		return columns.toArray(new String[0]);
	}

	private ContentValues timerRecordContentValuesList(final TimerRecord timerRecord) {
		ContentValues values = new ContentValues();
		values.put("category_id", timerRecord.getCategory().getRowId());
		values.put("start_time", timerRecord.getStartTime());
		values.put("end_time", timerRecord.getEndTime());
		return values;
	}

	public boolean categoryHasTimeSlices(final CategoryRecord category) throws SQLException {
		Cursor cursor = DatabaseInstance.getDatabase().query(true, DatabaseOpenHelper.TIMER_TABLE, columnList(), "category_id=" + category.getRowId(), null,
				null, null, null, null);
		boolean hasTimer = false;
		if (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		return hasTimer;
	}

}
