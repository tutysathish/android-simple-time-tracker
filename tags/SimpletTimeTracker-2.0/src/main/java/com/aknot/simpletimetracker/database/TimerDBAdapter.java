package com.aknot.simpletimetracker.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtils;

/**
 * @author Aknot
 */
public final class TimerDBAdapter {

	private final CategoryDBAdapter categoryDBAdapter;

	public TimerDBAdapter(final Context context) {
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

	public boolean deleteForDateRange(final long startDate, final long endDate) {
		Log.d("DEBUG_QUERY", "Start Time : " + DateTimeUtils.getDateForMillis(startDate));
		Log.d("DEBUG_QUERY", "End Time : " + DateTimeUtils.getDateForMillis(endDate));
		return DatabaseInstance.getDatabase().delete(DatabaseOpenHelper.TIMER_TABLE, "start_time>=" + startDate + " and start_time <=" + endDate, null) > 0;
	}

	public TimerRecord fetchByRowID(final long rowId) throws SQLException {
		Log.d("DEBUG_QUERY", "Row id : " + rowId);
		final Cursor cursor = DatabaseInstance.getDatabase().query(true, DatabaseOpenHelper.TIMER_TABLE, columnList(), "_id=" + rowId, null, null, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		final TimerRecord timerRecord = fillTimerRecordFromCursor(cursor);
		cursor.close();
		return timerRecord;
	}

	public List<TimerRecord> fetchLastTimerRecordsByCategory(final int categoryId) {
		final List<TimerRecord> result = new ArrayList<TimerRecord>();
		final Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(),
				"start_time >= " + DateTimeUtils.getMinTimeMillisWeek() + " and category_id = " + categoryId, null, null, null, "start_time desc");
		while (cursor.moveToNext()) {
			result.add(fillTimerRecordFromCursor(cursor));
		}
		cursor.close();
		return result;
	}

	public Map<String, List<TimerRecord>> fetchAllTimerRecordsByWeek() {
		final Map<String, List<TimerRecord>> allTimerRecord = new LinkedHashMap<String, List<TimerRecord>>();

		String cursorWeek = "";
		List<TimerRecord> result = null;

		// final Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(), null, null, null, null, "start_time desc");

		final long startTime = DateTimeUtils.getMaxTimeMillisToday() - DateTimeUtils.oneMonth;
		final long endTime = DateTimeUtils.getMaxTimeMillisToday();
		final Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(),
				"start_time >= " + startTime + " and end_time <= " + endTime, null, null, null, "start_time desc");

		while (cursor.moveToNext()) {
			final String currentWeek = DateTimeUtils.getWeek(cursor.getLong(cursor.getColumnIndexOrThrow("start_time")));
			if (!cursorWeek.equals(currentWeek)) {
				result = new ArrayList<TimerRecord>();
				allTimerRecord.put(currentWeek, result);
				cursorWeek = currentWeek;
			}
			result.add(fillTimerRecordFromCursor(cursor));
		}

		cursor.close();

		return allTimerRecord;
	}

	public long totalForTodayAndByCategory(final CategoryRecord category) {
		return totalByRangeByCategory(DateTimeUtils.getMinTimeMillisToday(), DateTimeUtils.getMaxTimeMillisToday(), category.getRowId());
	}

	public long totalForWeekAndByCategory(final CategoryRecord category) {
		return totalByRangeByCategory(DateTimeUtils.getMinTimeMillisWeek(), DateTimeUtils.getMaxTimeMillisToday(), category.getRowId());
	}

	private long totalByRangeByCategory(final long startTime, final long endTime, final int categoryRowId) {
		Log.d("DEBUG_QUERY", "Start Time : " + DateTimeUtils.getDateForMillis(startTime));
		Log.d("DEBUG_QUERY", "End Time : " + DateTimeUtils.getDateForMillis(endTime));
		final Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.TIMER_TABLE, columnList(),
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

	private TimerRecord fillTimerRecordFromCursor(final Cursor cursor) {
		final TimerRecord timerRecord = new TimerRecord();
		timerRecord.setRowId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
		timerRecord.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow("start_time")));
		timerRecord.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow("end_time")));
		timerRecord.setCategory(categoryDBAdapter.fetchByRowID(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))));
		return timerRecord;
	}

	private String[] columnList() {
		final List<String> columns = new ArrayList<String>();
		columns.add("_id");
		columns.add("category_id");
		columns.add("start_time");
		columns.add("end_time");
		return columns.toArray(new String[0]);
	}

	private ContentValues timerRecordContentValuesList(final TimerRecord timerRecord) {
		final ContentValues values = new ContentValues();
		values.put("category_id", timerRecord.getCategory().getRowId());
		values.put("start_time", timerRecord.getStartTime());
		values.put("end_time", timerRecord.getEndTime());
		return values;
	}

	public boolean categoryHasTimerRecord(final CategoryRecord category) throws SQLException {
		final Cursor cursor = DatabaseInstance.getDatabase().query(true, DatabaseOpenHelper.TIMER_TABLE, columnList(), "category_id=" + category.getRowId(),
				null, null, null, null, null);
		final boolean hasTimer = false;
		if (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		return hasTimer;
	}
}
