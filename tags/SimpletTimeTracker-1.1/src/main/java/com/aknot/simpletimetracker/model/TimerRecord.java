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

	public void setRowId(final int rowId) {
		this.rowId = rowId;
	}

	public long getDurationInMilliseconds() {
		return endTime - startTime;
	}

	public int getStartTimeComponent(final int componentId) {
		calendar.setTimeInMillis(startTime);
		return calendar.get(componentId);
	}

	public void setStartTimeComponent(final int componentId, final int value) {
		calendar.setTimeInMillis(startTime);
		calendar.set(componentId, value);
		startTime = calendar.getTimeInMillis();
	}

	public int getEndTimeComponent(final int componentId) {
		calendar.setTimeInMillis(endTime);
		return calendar.get(componentId);
	}

	public void setEndTimeComponent(final int componentId, final int value) {
		calendar.setTimeInMillis(endTime);
		calendar.set(componentId, value);
		endTime = calendar.getTimeInMillis();
	}

	public String getStartDateStr() {
		if (startTime == 0) {
			return "";
		} else {
			return DateFormat.format("E, MMMM dd, yyyy", startTime).toString();
		}
	}

	public String getStartTimeStr() {
		if (startTime == 0) {
			return "";
		} else {
			return DateTimeUtil.formatTime(startTime).toString();
		}
	}

	public String getEstimatedEndTimeStr(final long totalToday) {
		if (startTime == 0) {
			return "";
		} else {
			final long estimatedTime = startTime + category.getTargetHour() * 3600000 - totalToday;
			return DateTimeUtil.formatTime(estimatedTime).toString();
		}
	}

	public String getEndTimeStr() {
		if (endTime == 0) {
			return "";
		} else {
			return DateTimeUtil.formatTime(endTime).toString();
		}
	}

	public CategoryRecord getCategory() {
		return category;
	}

	public void setCategory(final CategoryRecord category) {
		this.category = category;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(final long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(final long endTime) {
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
