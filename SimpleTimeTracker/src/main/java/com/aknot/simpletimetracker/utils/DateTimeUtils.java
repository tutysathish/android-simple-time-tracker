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
public final class DateTimeUtils {

	private static String dateFormatString = "dd/MM/yyyy";
	private static String timeFormatString = "kk:mm";

	public static final long oneSecond = 1000;
	public static final long oneMinute = oneSecond * 60;
	public static final long oneHour = oneMinute * 60;
	public static final long oneDay = oneHour * 24;
	public static final long oneWeek = oneDay * 7;
	public static final long oneMonth = oneDay * 30;

	public static String getCurrentWeek() {
		return getWeek(Calendar.getInstance());
	}

	public static String getWeek(final Long date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		return getWeek(calendar);
	}

	public static String getWeek(final Calendar calendar) {
		final int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		return String.valueOf(weekOfYear);
	}

	public static String getCurrentFormatedDate() {
		return formatDate(new Date());
	}

	public static String formatDate(final Date date) {
		return DateFormat.format(dateFormatString, date).toString();
	}

	public static String formatTime(final long time) {
		return DateFormat.format(timeFormatString, time).toString();
	}

	public static StringBuilder hrColMinColSec(final long time, final boolean alwaysIncludeHours) {
		final long seconds = (time / 1000) % 60;
		final StringBuilder asText = hrColMin(time, alwaysIncludeHours);
		asText.append(":");
		if (seconds < 10) {
			asText.append(0);
		}
		asText.append(seconds);
		return asText;
	}

	public static StringBuilder hrColMin(final long time, final boolean alwaysIncludeHours) {
		final long seconds = time / 1000;
		final long minutes = (seconds / 60) % 60;
		final long hours = seconds / (60 * 60);

		final StringBuilder asText = new StringBuilder();
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

	public static Date getDateForMillis(final long tick) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tick);
		return cal.getTime();
	}

	public static long getMinTimeMillisToday(final Calendar calendar) {
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

	public static long getMaxTimeMillisToday(final Calendar calendar) {
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
		final Calendar startTotalCalendar = GregorianCalendar.getInstance();
		startTotalCalendar.set(Calendar.DAY_OF_WEEK, startTotalCalendar.getFirstDayOfWeek());
		startTotalCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startTotalCalendar.set(Calendar.MINUTE, 0);
		startTotalCalendar.set(Calendar.SECOND, 0);
		return startTotalCalendar.getTimeInMillis();
	}

	public static Calendar getCalendarFromString(final String date) {
		final SimpleDateFormat inputFormat = new SimpleDateFormat("E, MMMM dd, yyyy");
		Date parsedDate = new Date();
		try {
			parsedDate = inputFormat.parse(date);
		} catch (final ParseException e) {
			Log.e("DATE_PARSER", "Error: Impossible to parse input string to calendar.", e);
			throw new RuntimeException(e);
		}

		final Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(parsedDate);

		return currentDate;
	}

	public static long geLongFromString(final String date) {
		final Calendar calendar = getCalendarFromString(date);
		return calendar.getTimeInMillis();
	}

	public static String timeInMillisToText(final long totalTimeInMillis) {
		final long minutes = (totalTimeInMillis / (1000 * 60)) % 60;
		final long hours = totalTimeInMillis / (1000 * 60 * 60);
		String hoursWord;
		if (hours == 1 || hours == 0) {
			hoursWord = "hour";
		} else {
			hoursWord = "hours";
		}
		String minutesWord;
		if (minutes == 1 || minutes == 0) {
			minutesWord = "minute";
		} else {
			minutesWord = "minutes";
		}
		return hours + " " + hoursWord + ", " + minutes + " " + minutesWord;
	}

}
