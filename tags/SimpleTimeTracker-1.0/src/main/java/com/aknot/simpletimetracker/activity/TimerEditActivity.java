package com.aknot.simpletimetracker.activity;

import java.util.Calendar;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;

public class TimerEditActivity extends Activity implements TimePickerDialog.OnTimeSetListener {

	private enum TimeFieldSelected {
		IN, OUT
	}

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);

	private TimeFieldSelected selectedTimeField;

	private TimerRecord timerRecord;

	private Button inTimer;
	private Button outTimer;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_timer);

		final int rowId = getIntent().getIntExtra("row_id", 0);
		final long date = getIntent().getLongExtra("date", 0);

		initialize(rowId, date);
	}

	@Override
	public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
		if (selectedTimeField == TimeFieldSelected.IN) {
			timerRecord.setStartTimeComponent(Calendar.HOUR_OF_DAY, hourOfDay);
			timerRecord.setStartTimeComponent(Calendar.MINUTE, minute);
		} else if (selectedTimeField == TimeFieldSelected.OUT) {
			timerRecord.setEndTimeComponent(Calendar.HOUR_OF_DAY, hourOfDay);
			timerRecord.setEndTimeComponent(Calendar.MINUTE, minute);
		}
		updateLabels();
	}

	private void initialize(final int rowId, final long date) {
		final Spinner catSpinner = (Spinner) findViewById(R.id.spinnerEditCategory);
		catSpinner.setAdapter(CategoryRecord.getCategoryAdapter(this));

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

		final TimePickerDialog.OnTimeSetListener listener = this;
		final Context context = this;

		inTimer = (Button) findViewById(R.id.EditTimeIn);
		inTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectedTimeField = TimeFieldSelected.IN;
				new TimePickerDialog(context, listener, timerRecord.getStartTimeComponent(Calendar.HOUR_OF_DAY), timerRecord
						.getStartTimeComponent(Calendar.MINUTE), false).show();
			}
		});

		outTimer = (Button) findViewById(R.id.EditTimeOut);
		outTimer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				selectedTimeField = TimeFieldSelected.OUT;
				new TimePickerDialog(context, listener, timerRecord.getEndTimeComponent(Calendar.HOUR_OF_DAY),
						timerRecord.getEndTimeComponent(Calendar.MINUTE), false).show();
			}
		});

		final Button saveButton = (Button) findViewById(R.id.ButtonSaveTimerRecord);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				timerRecord.setCategory((CategoryRecord) catSpinner.getSelectedItem());
				if (validate()) {
					final Intent intent = new Intent();
					final Bundle bundle = new Bundle();
					bundle.putSerializable("timer_record", timerRecord);
					intent.putExtra("data", bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.ButtonCancelTimerRecord);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				finish();
			}
		});

		updateLabels();
	}

	private boolean validate() {
		if (timerRecord.getEndTime() < timerRecord.getStartTime()) {
			Toast.makeText(getApplicationContext(), "Invalid input: end time must be after start time.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void updateLabels() {
		setTitle(timerRecord.getStartDateStr());
		outTimer.setText(timerRecord.getEndTimeStr());
		inTimer.setText(timerRecord.getStartTimeStr());
	}

}
