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

	private Spinner catSpinner;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_summary);

		if (savedInstanceState != null) {
			category = (CategoryRecord) savedInstanceState.getSerializable("category");
		}

		catSpinner = (Spinner) findViewById(R.id.spinnerSelectCategory);

		catSpinner.setAdapter(CategoryRecord.getCategoryAdapter(this));
		catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parentView, final View selectedItemView, final int position, final long id) {
				category = (CategoryRecord) catSpinner.getSelectedItem();
				fillInSummary(category);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parentView) {
			}
		});
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putSerializable("category", category);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (category != null) {
			for (int position = 0; position < catSpinner.getCount(); position++) {
				if (catSpinner.getItemAtPosition(position).equals(category)) {
					catSpinner.setSelection(position);
				}
			}
		}

		fillInSummary((CategoryRecord) catSpinner.getSelectedItem());
	}

	private void fillInSummary(final CategoryRecord categoryRecord) {

		final Map<String, Map<String, Long>> summaries = loadData(categoryRecord);

		final TextView weekSummary = (TextView) findViewById(R.id.tvWeekSummary);
		weekSummary.setText("Week " + DateTimeUtil.getCurrentWeek() + " : " + timeInMillisToText(weekTimeElapsed));

		final TextView targetHour = (TextView) findViewById(R.id.tvTargetHour);
		if (category != null) {
			targetHour.setText("Target : " + intToText(category.getTargetHour()));
		} else {
			targetHour.setText("Target :");
		}

		final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutSummary);
		linearLayout.removeAllViews();

		for (final Entry<String, Map<String, Long>> entry : summaries.entrySet()) {
			final Map<String, Long> summaryRows = entry.getValue();

			final TextView headerTextView = new TextView(this);
			headerTextView.setText(entry.getKey());
			headerTextView.setTextColor(Color.GREEN);

			linearLayout.addView(headerTextView);

			for (final Entry<String, Long> rowCaption : summaryRows.entrySet()) {
				final long totalTimeInMillis = rowCaption.getValue();
				final TextView rowTextView = new TextView(this);
				rowTextView.setText("    " + rowCaption.getKey() + ": " + timeInMillisToText(totalTimeInMillis));
				linearLayout.addView(rowTextView);
			}
		}
	}

	private Map<String, Map<String, Long>> loadData(final CategoryRecord categoryRecord) {
		final List<TimerRecord> timerRecords = timerDBAdapter.fetchLastTimerRecordsByCategory(categoryRecord.getRowId());

		final Map<String, Map<String, Long>> summaries = new LinkedHashMap<String, Map<String, Long>>();

		weekTimeElapsed = 0;

		for (final TimerRecord timerRecord : timerRecords) {
			final String header = timerRecord.getStartDateStr();
			Map<String, Long> group = summaries.get(header);

			if (group == null) {
				group = new TreeMap<String, Long>();
				summaries.put(header, group);
			}

			final String reportLine = timerRecord.getCategory().getCategoryName();

			Long timeSum = group.get(reportLine);
			if (timeSum == null) {
				timeSum = Long.valueOf(0);
			}
			final long elapsed = timerRecord.getEndTime() - timerRecord.getStartTime();

			weekTimeElapsed += elapsed;

			group.put(reportLine, timeSum + elapsed);
		}

		return summaries;
	}

	private String intToText(final int hours) {
		String hoursWord;
		if (hours == 1 || hours == 0) {
			hoursWord = "hour";
		} else {
			hoursWord = "hours";
		}
		return hours + " " + hoursWord;
	}

	private String timeInMillisToText(final long totalTimeInMillis) {
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