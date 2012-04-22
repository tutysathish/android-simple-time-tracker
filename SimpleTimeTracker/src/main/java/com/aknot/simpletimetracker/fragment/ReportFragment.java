package com.aknot.simpletimetracker.fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.dialog.TimerEditDialog;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtils;

/**
 * 
 * @author Aknot
 * 
 */
public class ReportFragment extends AbstractFragment {

	private static final String HEADER = "Header";
	private static final String DETAIL = "Detail";

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this.getActivity());
	private final Map<View, Integer> rowToTimerRecordRowIdMap = new HashMap<View, Integer>();

	private int chosenRowId;
	private String dateSelected;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.tab_report, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		fillInReport();
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		if (view.getTag() != null) {
			if (view.getTag().equals(DETAIL)) {
				menu.add(0, 1, 0, getString(R.string.menu_edit));
				menu.add(0, 2, 0, getString(R.string.menu_del));
				chosenRowId = rowToTimerRecordRowIdMap.get(view);
			} else if (view.getTag().equals(HEADER)) {
				menu.add(0, 3, 0, getString(R.string.menu_add));
				menu.add(0, 4, 0, getString(R.string.menu_del));
				dateSelected = (String) ((TextView) view).getText();
			}
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			showTimerDialog(chosenRowId, 0);
			return true;
		case 2:
			buildDeleteRowDialog();
			return true;
		case 3:
			showTimerDialog(TimerRecord.NEW_TIMER, DateTimeUtils.geLongFromString(dateSelected));
			return true;
		case 4:
			buildDeleteRangeDialog();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void saveTimer(final TimerRecord timerToSave) {
		if (timerToSave.getRowId() == TimerRecord.NEW_TIMER) {
			timerDBAdapter.createTimer(timerToSave);
		} else {
			timerDBAdapter.updateTimer(timerToSave);
		}
		fillInReport();
	}

	private void fillInReport() {

		final Map<String, List<TimerRecord>> allRecords = timerDBAdapter.fetchAllTimerRecordsByWeek();

		final LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.linearLayoutReport);
		linearLayout.removeAllViews();

		for (final Entry<String, List<TimerRecord>> entry : allRecords.entrySet()) {
			final List<TimerRecord> reportRows = entry.getValue();

			final TextView headerTextView = new TextView(this.getActivity());
			headerTextView.setText("Week : " + entry.getKey());
			headerTextView.setTextColor(Color.parseColor("#FF6899FF"));
			headerTextView.setTypeface(Typeface.DEFAULT_BOLD);

			linearLayout.addView(headerTextView);

			String previousHeader = "";
			for (final TimerRecord rowCaption : reportRows) {
				if (!previousHeader.equals(rowCaption.getStartDateStr())) {
					final TextView rowTextView = new TextView(this.getActivity());
					rowTextView.setText("    " + rowCaption.getStartDateStr());
					rowTextView.setTextColor(Color.GREEN);
					rowTextView.setTag(HEADER);
					registerForContextMenu(rowTextView);
					linearLayout.addView(rowTextView);
					previousHeader = rowCaption.getStartDateStr();
				}

				final TextView rowTextView = new TextView(this.getActivity());
				rowTextView.setText("        " + rowCaption.getTitleWithDuration());
				rowTextView.setTag(DETAIL);
				rowToTimerRecordRowIdMap.put(rowTextView, rowCaption.getRowId());
				registerForContextMenu(rowTextView);
				linearLayout.addView(rowTextView);
			}
		}
	}

	private void showTimerDialog(final int rowId, final long date) {
		final TimerEditDialog timerEditDialog = new TimerEditDialog(this.getActivity());
		timerEditDialog.buildEditDialog(rowId, date, this).show();
	}

	private void buildDeleteRowDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

		final TimerRecord timerRecord = timerDBAdapter.fetchByRowID(chosenRowId);

		builder.setTitle(timerRecord.getTitle());

		builder.setMessage("Are you sure to delete this timer record?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				timerDBAdapter.delete(chosenRowId);
				fillInReport();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void buildDeleteRangeDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

		builder.setTitle(dateSelected);

		builder.setMessage("Are you sure to delete all timer record for this date?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						final Calendar currentDate = DateTimeUtils.getCalendarFromString(dateSelected);

						final long startDate = DateTimeUtils.getMinTimeMillisToday(currentDate);
						final long endDate = DateTimeUtils.getMaxTimeMillisToday(currentDate);

						timerDBAdapter.deleteForDateRange(startDate, endDate);

						fillInReport();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
