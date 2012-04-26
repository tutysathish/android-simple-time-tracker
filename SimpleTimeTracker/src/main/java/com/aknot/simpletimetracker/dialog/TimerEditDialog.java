package com.aknot.simpletimetracker.dialog;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.fragment.ReportFragment;
import com.aknot.simpletimetracker.fragment.TimerFragment;
import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;

public class TimerEditDialog extends Dialog implements TimePickerDialog.OnTimeSetListener {

	private enum TimerFieldSelected {
		IN, OUT
	}

	private Button inTimer;
	private Button outTimer;

	private TimerFieldSelected selectedTimeField;

	private TimerRecord timerRecord;

	public TimerEditDialog(final Context context) {
		super(context);
	}

	/**
	 * Build the timer edit dialog for the report fragment.
	 */
	public TimerEditDialog buildEditDialog(final int rowId, final long date, final ReportFragment reportFragment) {
		setContentView(R.layout.edit_timer);

		final Spinner catSpinner = (Spinner) findViewById(R.id.spinnerEditCategory);
		catSpinner.setAdapter(CategoryRecord.getCategoryAdapter(reportFragment.getActivity()));

		final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(reportFragment.getActivity());
		if (rowId != TimerRecord.NEW_TIMER) {
			timerRecord = timerDBAdapter.fetchByRowID(rowId);
			for (int position = 0; position < catSpinner.getCount(); position++) {
				if (((CategoryRecord) catSpinner.getItemAtPosition(position)).getRowId() == timerRecord.getCategory().getRowId()) {
					catSpinner.setSelection(position);
					break;
				}
			}
		} else {
			timerRecord = new TimerRecord();
			timerRecord.setStartTime(date);
			timerRecord.setEndTime(date);
			timerRecord.setCategory((CategoryRecord) catSpinner.getAdapter().getItem(0));
			timerRecord.setRowId(TimerRecord.NEW_TIMER);
		}

		setupButton(catSpinner, reportFragment);

		updateLabels();

		return this;
	}

	/**
	 * Build the timer edit dialog for the timer fragment.
	 */
	public TimerEditDialog buildEditDialog(final TimerRecord timerRecord, final TimerFragment timerFragment) {
		setContentView(R.layout.edit_timer);

		this.timerRecord = timerRecord;

		final Spinner catSpinner = (Spinner) findViewById(R.id.spinnerEditCategory);
		catSpinner.setAdapter(CategoryRecord.getCategoryAdapter(timerFragment.getActivity()));

		for (int position = 0; position < catSpinner.getCount(); position++) {
			if (((CategoryRecord) catSpinner.getItemAtPosition(position)).getRowId() == this.timerRecord.getCategory().getRowId()) {
				catSpinner.setSelection(position);
				break;
			}
		}

		setupButton(catSpinner, timerFragment);

		updateLabels();

		return this;
	}

	@Override
	public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
		if (selectedTimeField == TimerFieldSelected.IN) {
			timerRecord.setStartTimeComponent(Calendar.HOUR_OF_DAY, hourOfDay);
			timerRecord.setStartTimeComponent(Calendar.MINUTE, minute);
		} else if (selectedTimeField == TimerFieldSelected.OUT) {
			timerRecord.setEndTimeComponent(Calendar.HOUR_OF_DAY, hourOfDay);
			timerRecord.setEndTimeComponent(Calendar.MINUTE, minute);
		}
		updateLabels();
	}

	/**
	 * This method set the button in the edit dialog for the report activity.
	 * 
	 * @param catSpinner
	 * @param reportActivity
	 */
	private void setupButton(final Spinner catSpinner, final ReportFragment reportFragment) {
		final TimePickerDialog.OnTimeSetListener listener = this;

		inTimer = (Button) findViewById(R.id.EditTimeIn);
		inTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectedTimeField = TimerFieldSelected.IN;
				new TimePickerDialog(reportFragment.getActivity(), listener, timerRecord.getStartTimeComponent(Calendar.HOUR_OF_DAY), timerRecord
						.getStartTimeComponent(Calendar.MINUTE), false).show();
			}
		});

		outTimer = (Button) findViewById(R.id.EditTimeOut);
		outTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectedTimeField = TimerFieldSelected.OUT;
				new TimePickerDialog(reportFragment.getActivity(), listener, timerRecord.getEndTimeComponent(Calendar.HOUR_OF_DAY), timerRecord
						.getEndTimeComponent(Calendar.MINUTE), false).show();
			}
		});

		final Button saveButton = (Button) findViewById(R.id.ButtonSaveTimerRecord);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				timerRecord.setCategory((CategoryRecord) catSpinner.getSelectedItem());
				if (validate(reportFragment)) {
					reportFragment.saveTimer(timerRecord);
				}
				dismiss();
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.ButtonCancelTimerRecord);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				cancel();
			}
		});
	}

	/**
	 * This method set the button in the edit dialog for the timer activity.
	 * 
	 * @param catSpinner
	 * @param timerActivity
	 */
	private void setupButton(final Spinner catSpinner, final TimerFragment timerFragment) {
		final TimePickerDialog.OnTimeSetListener listener = this;

		inTimer = (Button) findViewById(R.id.EditTimeIn);
		inTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectedTimeField = TimerFieldSelected.IN;
				new TimePickerDialog(timerFragment.getActivity(), listener, timerRecord.getStartTimeComponent(Calendar.HOUR_OF_DAY), timerRecord
						.getStartTimeComponent(Calendar.MINUTE), false).show();
			}
		});

		outTimer = (Button) findViewById(R.id.EditTimeOut);

		final Button saveButton = (Button) findViewById(R.id.ButtonSaveTimerRecord);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				timerRecord.setCategory((CategoryRecord) catSpinner.getSelectedItem());
				if (validate(timerFragment)) {
					timerFragment.saveTimer(timerRecord);
				}
				dismiss();
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.ButtonCancelTimerRecord);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				cancel();
			}
		});
	}

	private boolean validate(final ReportFragment reportFragment) {
		if (timerRecord.getEndTime() < timerRecord.getStartTime()) {
			Toast.makeText(reportFragment.getActivity(), "Invalid input: end time must be after start time.", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean validate(final TimerFragment timerFragment) {
		if (timerRecord.getStartTime() > System.currentTimeMillis()) {
			Toast.makeText(timerFragment.getActivity(), "Invalid input: start time must be before current time.", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void updateLabels() {
		setTitle(timerRecord.getStartDateStr());
		inTimer.setText(timerRecord.getStartTimeStr());
		outTimer.setText(timerRecord.getEndTimeStr());
	}

}