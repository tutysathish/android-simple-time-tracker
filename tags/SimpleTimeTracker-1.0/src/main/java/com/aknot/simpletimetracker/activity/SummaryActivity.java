package com.aknot.simpletimetracker.activity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtil;

/**
 * @author Aknot
 */
public final class SummaryActivity extends Activity {

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);

	private long weekTimeElapsed;
	private CategoryRecord category;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_summary);

		if (savedInstanceState != null) {
			category = (CategoryRecord) savedInstanceState.getSerializable("category");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("category", category);
	}

	@Override
	public void onResume() {
		super.onResume();

		final Spinner catSpinner = (Spinner) findViewById(R.id.spinnerSelectCategory);

		catSpinner.setAdapter(CategoryRecord.getCategoryAdapter(this));
		catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				category = (CategoryRecord) catSpinner.getSelectedItem();
				fillInSummary(category);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		if (category != null) {
			for (int position = 0; position < catSpinner.getCount(); position++) {
				if (catSpinner.getItemAtPosition(position).equals(category)) {
					catSpinner.setSelection(position);
				}
			}
		}

		fillInSummary((CategoryRecord) catSpinner.getSelectedItem());
	}

	private void fillInSummary(CategoryRecord categoryRecord) {

		Map<String, Map<String, Long>> summaries = loadData(categoryRecord);

		TextView weekSummary = (TextView) findViewById(R.id.weekSummary);
		weekSummary.setText("Week " + DateTimeUtil.getCurrentWeek() + ": " + timeInMillisToText(weekTimeElapsed));

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutSummary);
		linearLayout.removeAllViews();

		for (Entry<String, Map<String, Long>> entry : summaries.entrySet()) {
			Map<String, Long> summaryRows = entry.getValue();

			TextView headerTextView = new TextView(this);
			headerTextView.setText(entry.getKey());
			headerTextView.setTextColor(Color.GREEN);

			linearLayout.addView(headerTextView);

			for (Entry<String, Long> rowCaption : summaryRows.entrySet()) {
				long totalTimeInMillis = rowCaption.getValue();
				TextView rowTextView = new TextView(this);
				rowTextView.setText("    " + rowCaption.getKey() + ": " + timeInMillisToText(totalTimeInMillis));
				linearLayout.addView(rowTextView);
			}
		}
	}

	private Map<String, Map<String, Long>> loadData(CategoryRecord categoryRecord) {
		List<TimerRecord> timerRecords = timerDBAdapter.fetchLastTimerRecordsByCategory(categoryRecord.getRowId());

		Map<String, Map<String, Long>> summaries = new LinkedHashMap<String, Map<String, Long>>();

		weekTimeElapsed = 0;

		for (TimerRecord timerRecord : timerRecords) {
			String header = timerRecord.getStartDateStr();
			Map<String, Long> group = summaries.get(header);

			if (group == null) {
				group = new TreeMap<String, Long>();
				summaries.put(header, group);
			}

			String reportLine = timerRecord.getCategory().getCategoryName();

			Long timeSum = group.get(reportLine);
			if (timeSum == null) {
				timeSum = Long.valueOf(0);
			}
			long elapsed = timerRecord.getEndTime() - timerRecord.getStartTime();

			weekTimeElapsed += elapsed;

			group.put(reportLine, timeSum + elapsed);
		}

		return summaries;
	}

	private String timeInMillisToText(long totalTimeInMillis) {
		long minutes = (totalTimeInMillis / (1000 * 60)) % 60;
		long hours = totalTimeInMillis / (1000 * 60 * 60);
		String hoursWord;
		if (hours == 1) {
			hoursWord = "hour";
		} else {
			hoursWord = "hours";
		}
		String minutesWord;
		if (minutes == 1) {
			minutesWord = "minute";
		} else {
			minutesWord = "minutes";
		}
		String timeString = hours + " " + hoursWord + ", " + minutes + " " + minutesWord;
		return timeString;
	}

}