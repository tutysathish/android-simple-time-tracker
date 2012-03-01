package com.aknot.simpletimetracker.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.dialog.TimerEditDialog;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtil;
import com.aknot.simpletimetracker.widget.ExpandableReportAdapter;

/**
 * @author Aknot
 */
public final class ReportActivity extends ExpandableListActivity {

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);
	private final Map<View, Integer> rowToTimerRecordRowIdMap = new HashMap<View, Integer>();

	private int chosenRowId;
	private String dateSelected;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_report);
	}

	@Override
	public void onResume() {
		super.onResume();
		fillInReport();
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		if (view.getTag().equals("Detail")) {
			menu.add(0, 1, 0, getString(R.string.menu_edit));
			menu.add(0, 2, 0, getString(R.string.menu_del));
			chosenRowId = rowToTimerRecordRowIdMap.get(view);
		} else if (view.getTag().equals("Header")) {
			menu.add(0, 3, 0, getString(R.string.menu_add));
			menu.add(0, 4, 0, getString(R.string.menu_del));
			dateSelected = (String) ((TextView) view).getText();
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
			showTimerDialog(TimerRecord.NEW_TIMER, DateTimeUtil.geLongFromString(dateSelected));
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

	public Map<View, Integer> getRowToTimerRecordRowIdMap() {
		return rowToTimerRecordRowIdMap;
	}

	private void fillInReport() {
		// Get the expandable adapter
		final ExpandableReportAdapter reportAdapter = new ExpandableReportAdapter(this, timerDBAdapter.fetchAllTimerRecordsByWeek());
		// Set this list adapter to the list view
		setListAdapter(reportAdapter);
		// Expand first group by default
		getExpandableListView().expandGroup(0);
		// Make expandable listener
		getExpandableListView().setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(final int groupPosition) {
				final int len = reportAdapter.getGroupCount();
				for (int i = 0; i < len; i++) {
					if (i != groupPosition) {
						getExpandableListView().collapseGroup(i);
					}
				}
			}
		});

	}

	private void showTimerDialog(final int rowId, final long date) {
		final TimerEditDialog timerEditDialog = new TimerEditDialog(this);
		timerEditDialog.buildEditDialog(rowId, date, this).show();
	}

	private void buildDeleteRowDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(dateSelected);

		builder.setMessage("Are you sure to delete all timer record for this date?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int id) {
						final Calendar currentDate = DateTimeUtil.getCalendarFromString(dateSelected);

						final long startDate = DateTimeUtil.getMinTimeMillisToday(currentDate);
						final long endDate = DateTimeUtil.getMaxTimeMillisToday(currentDate);

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