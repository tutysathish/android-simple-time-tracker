package com.aknot.simpletimetracker.fragment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.TreeMap;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtils;

/**
 * This fragment represent the summary page.
 * 
 * @author Aknot
 * 
 */
public class SummaryFragment extends AbstractFragment {

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this.getActivity());

	private long weekTimeElapsed;

	private CategoryRecord category;

	private Spinner catSpinner;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.tab_summary, container, false);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (savedInstanceState != null) {
			category = (CategoryRecord) savedInstanceState.getSerializable("category");
		}

		catSpinner = (Spinner) view.findViewById(R.id.spinnerSelectCategory);

		catSpinner.setAdapter(CategoryRecord.getCategoryAdapter(this.getActivity()));
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
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
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

		final TextView weekSummary = (TextView) getView().findViewById(R.id.tvWeekSummary);
		weekSummary.setText(DateTimeUtils.timeInMillisToText(weekTimeElapsed));

		final TextView targetHour = (TextView) getView().findViewById(R.id.tvTargetHour);
		if (category != null) {
			targetHour.setText(DateTimeUtils.doubleToText(category.getTargetHour()));
		} else {
			targetHour.setText(getString(R.string.summary_no_target));
		}

		final LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.linearLayoutSummary);
		linearLayout.removeAllViews();

		for (final Entry<String, Map<String, Long>> entry : summaries.entrySet()) {
			final Map<String, Long> summaryRows = entry.getValue();

			final TextView headerTextView = new TextView(this.getActivity());
			headerTextView.setText(entry.getKey());
			headerTextView.setTextColor(Color.GREEN);
			headerTextView.setTextSize(18);

			linearLayout.addView(headerTextView);

			for (final Entry<String, Long> rowCaption : summaryRows.entrySet()) {
				final long totalTimeInMillis = rowCaption.getValue();
				final TextView rowTextView = new TextView(this.getActivity());
				rowTextView.setText("    " + rowCaption.getKey() + ": " + DateTimeUtils.timeInMillisToText(totalTimeInMillis));
				rowTextView.setTextSize(18);
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

	@Override
	public void update(Observable observable, Object data) {
		fillInSummary((CategoryRecord) catSpinner.getSelectedItem());
	}

}
