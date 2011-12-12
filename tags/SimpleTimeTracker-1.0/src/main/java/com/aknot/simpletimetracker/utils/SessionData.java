package com.aknot.simpletimetracker.utils;

import java.io.Serializable;

import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;

/**
 * This class keep the session data for the timer.
 * 
 * @author Aknot
 * 
 */
public final class SessionData implements Serializable {

	/** Serial UID */
	private static final long serialVersionUID = -1223094842172676534L;

	private long punchInBase;
	private long todayBase;
	private long weekBase;
	private boolean punchedOut = true;

	private TimerRecord currentTimerRecord = new TimerRecord();

	/**
	 * Start a new timer for a given category
	 * 
	 * @param category
	 */
	public void startTimer(CategoryRecord category) {
		currentTimerRecord = new TimerRecord();
		currentTimerRecord.setCategory(category);
		currentTimerRecord.setStartTime(System.currentTimeMillis());
		punchedOut = false;
		punchInBase = 0;
		todayBase = 0;
		weekBase = 0;
	}

	public void stopTimer() {
		currentTimerRecord.setEndTime(System.currentTimeMillis());
		punchedOut = true;
		punchInBase = 0;
		todayBase = 0;
		weekBase = 0;
	}

	public TimerRecord getCurrentTimerRecord() {
		return currentTimerRecord;
	}

	public CategoryRecord getCategory() {
		return currentTimerRecord.getCategory();
	}

	public void setCategory(CategoryRecord category) {
		currentTimerRecord.setCategory(category);
	}

	public long getPunchInBase() {
		return punchInBase;
	}

	public void setPunchInBase(long punchInBase) {
		this.punchInBase = punchInBase;
	}

	public boolean isPunchedOut() {
		return punchedOut;
	}

	public long getTodayBase() {
		return todayBase;
	}

	public void setTodayBase(long todayBase) {
		this.todayBase = todayBase;
	}

	public long getWeekBase() {
		return weekBase;
	}

	public void setWeekBase(long weekBase) {
		this.weekBase = weekBase;
	}

}
