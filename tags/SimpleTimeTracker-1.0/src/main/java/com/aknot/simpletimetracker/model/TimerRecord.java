package com.aknot.simpletimetracker.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.text.format.DateFormat;

import com.aknot.simpletimetracker.utils.DateTimeUtil;

/**
 * @author Aknot
 */
public final class TimerRecord implements Serializable {

	private static final long serialVersionUID = 6586305797483181442L;

	private int rowId;

	private long startTime = 0;
	private long endTime = 0;

	private CategoryRecord category;

	private static Calendar calendar = new GregorianCalendar();

	public static final int NEW_TIMER = -1;

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public long getDurationInMilliseconds() {
		return endTime - startTime;
	}

	public String getStartDateStr() {
		if (startTime == 0) {
			return "";
		} else {
			return DateFormat.format("E, MMMM dd, yyyy", startTime).toString();
		}
	}

	public int getStartTimeComponent(int componentId) {
		calendar.setTimeInMillis(startTime);
		return calendar.get(componentId);
	}

	public void setStartTimeComponent(int componentId, int value) {
		calendar.setTimeInMillis(startTime);
		calendar.set(componentId, value);
		startTime = calendar.getTimeInMillis();
	}

	public int getEndTimeComponent(int componentId) {
		calendar.setTimeInMillis(endTime);
		return calendar.get(componentId);
	}

	public void setEndTimeComponent(int componentId, int value) {
		calendar.setTimeInMillis(endTime);
		calendar.set(componentId, value);
		endTime = calendar.getTimeInMillis();
	}

	public String getStartTimeStr() {
		if (startTime == 0) {
			return "";
		} else {
			return DateTimeUtil.formatTime(startTime).toString();
		}
	}

	public String getEndTimeStr() {
		if (startTime == 0) {
			return "";
		} else {
			return DateTimeUtil.formatTime(endTime).toString();
		}
	}

	public CategoryRecord getCategory() {
		return category;
	}

	public void setCategory(CategoryRecord category) {
		this.category = category;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getTitleWithDuration() {
		return getCategory().getCategoryName() + ": " + getStartTimeStr() + " - " + getEndTimeStr() + " ("
				+ DateTimeUtil.hrColMinColSec(getDurationInMilliseconds(), true) + ")";
	}

	public String getTitle() {
		return getCategory().getCategoryName() + ": " + getStartTimeStr() + " - " + getEndTimeStr();
	}

}
