package com.aknot.simpletimetracker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.format.DateFormat;
import android.util.Log;

/**
 * @author Aknot
 */
public final class DateTimeUtil {

	private static String dateFormatString = "dd/MM/yyyy";
	private static String timeFormatString = "kk:mm";

	public static String getCurrentWeek() {
		final Calendar calendar = Calendar.getInstance();
		int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		return String.valueOf(weekOfYear);
	}

	public static String getCurrentFormatedDate() {
		return formatDate(new Date());
	}

	public static String formatDate(Date date) {
		return DateFormat.format(dateFormatString, date).toString();
	}

	public static String formatTime(long time) {
		return DateFormat.format(timeFormatString, time).toString();
	}

	public static StringBuilder hrColMinColSec(long time, boolean alwaysIncludeHours) {
		long seconds = (time / 1000) % 60;
		StringBuilder asText = hrColMin(time, alwaysIncludeHours);
		asText.append(":");
		if (seconds < 10) {
			asText.append(0);
		}
		asText.append(seconds);
		return asText;
	}

	public static StringBuilder hrColMin(long time, boolean alwaysIncludeHours) {
		long seconds = time / 1000;
		long minutes = (seconds / 60) % 60;
		long hours = seconds / (60 * 60);

		StringBuilder asText = new StringBuilder();
		if (alwaysIncludeHours || hours > 0) {
			if (hours < 10) {
				asText.append(0);
			}
			asText.append(hours);
			asText.append(":");
		}
		if (minutes > 0) {
			if (minutes < 10) {
				asText.append(0);
			}
			asText.append(minutes);
		} else {
			asText.append("00");
		}
		return asText;
	}

	public static Date getDateForMillis(long tick) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tick);
		return cal.getTime();
	}

	public static long getMinTimeMillisToday(Calendar calendar) {
		Calendar startTotalCalendar = calendar;
		if (startTotalCalendar == null) {
			startTotalCalendar = Calendar.getInstance();
		}
		startTotalCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startTotalCalendar.set(Calendar.MINUTE, 0);
		startTotalCalendar.set(Calendar.SECOND, 0);
		return startTotalCalendar.getTimeInMillis();
	}

	public static long getMinTimeMillisToday() {
		return getMinTimeMillisToday(Calendar.getInstance());
	}

	public static long getMaxTimeMillisToday(Calendar calendar) {
		Calendar endTotalCalendar = calendar;
		if (endTotalCalendar == null) {
			endTotalCalendar = Calendar.getInstance();
		}
		endTotalCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endTotalCalendar.set(Calendar.MINUTE, 59);
		endTotalCalendar.set(Calendar.SECOND, 59);
		return endTotalCalendar.getTimeInMillis();
	}

	public static long getMaxTimeMillisToday() {
		return getMaxTimeMillisToday(Calendar.getInstance());
	}

	public static long getMinTimeMillisWeek() {
		Calendar startTotalCalendar = GregorianCalendar.getInstance();
		startTotalCalendar.set(Calendar.DAY_OF_WEEK, startTotalCalendar.getFirstDayOfWeek());
		startTotalCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startTotalCalendar.set(Calendar.MINUTE, 0);
		startTotalCalendar.set(Calendar.SECOND, 0);
		return startTotalCalendar.getTimeInMillis();
	}

	public static Calendar getCalendarFromString(String date) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("E, MMMM dd, yyyy");
		Date parsedDate = new Date();
		try {
			parsedDate = inputFormat.parse(date);
		} catch (ParseException e) {
			Log.e("DATE_PARSER", "Error: Impossible to parse input string to calendar.", e);
			throw new RuntimeException(e);
		}

		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(parsedDate);

		return currentDate;
	}

	public static long geLongFromString(String date) {
		Calendar calendar = getCalendarFromString(date);
		return calendar.getTimeInMillis();
	}

}
