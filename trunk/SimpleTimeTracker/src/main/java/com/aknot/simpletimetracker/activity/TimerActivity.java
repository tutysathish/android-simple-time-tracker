package com.aknot.simpletimetracker.activity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.dialog.CategoriesDialog;
import com.aknot.simpletimetracker.dialog.TimerEditDialog;
import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.model.TimerRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtil;
import com.aknot.simpletimetracker.utils.SessionData;

/**
 * @author Aknot
 */
public final class TimerActivity extends Activity {

	private Chronometer chronometer;
	private ProgressBar progressBar;

	private SessionData sessionData = new SessionData();

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_timer);

		progressBar = (ProgressBar) findViewById(R.id.timerProgressBar);
		chronometer = (Chronometer) findViewById(R.id.chron);
	}

	@Override
	public void onResume() {
		super.onResume();

		setupButtons();
		setupChronometer();

		restoreSession();
		setupScreenLabels();
	}

	@Override
	protected void onPause() {
		super.onPause();

		chronometer.stop();
	}

	private void setupButtons() {
		final Button punchInButton = (Button) findViewById(R.id.btnPunchIn);
		punchInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				checkInCategory();
			}
		});
		final Button punchOutButton = (Button) findViewById(R.id.btnPunchOut);
		punchOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				checkOut();
			}
		});
	}

	private void setupScreenLabels() {
		final TextView tvWeek = (TextView) findViewById(R.id.currentWeek);
		tvWeek.setText("Week : " + DateTimeUtil.getCurrentWeek());

		final TextView tvDate = (TextView) findViewById(R.id.currentDate);
		tvDate.setText("Date : " + DateTimeUtil.getCurrentFormatedDate());

		boolean showTv = sessionData.getCurrentTimerRecord() != null;
		showTv &= sessionData.getCurrentTimerRecord().getCategory() != null;
		showTv &= !sessionData.isPunchedOut();

		final TextView labelTv = (TextView) findViewById(R.id.tvTimeInActivity);
		final TextView tvStartTime = (TextView) findViewById(R.id.tvStartTime);
		final TextView tvEndTime = (TextView) findViewById(R.id.tvEndTime);

		if (showTv) {
			labelTv.setText("Time spent : " + sessionData.getCurrentTimerRecord().getCategory().getCategoryName());
			tvStartTime.setText("Start time : " + sessionData.getCurrentTimerRecord().getStartTimeStr());
			tvEndTime.setText("End time : " + sessionData.getCurrentTimerRecord().getEstimatedEndTimeStr(sessionData.getTodayBase()));
		} else {
			labelTv.setText("No current activity");
			tvStartTime.setText("Start time : ");
			tvEndTime.setText("End time : ");
		}

		tvStartTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				showDialog();
			}
		});
	}

	private void showDialog() {
		if (!sessionData.isPunchedOut()) {
			final TimerEditDialog timerEditDialog = new TimerEditDialog(this);
			timerEditDialog.buildEditDialog(sessionData.getCurrentTimerRecord(), this).show();
		}
	}

	public void saveTimer(final TimerRecord timerToSave) {
		chronometer.stop();
		final long newElapsedTime = SystemClock.elapsedRealtime() - (System.currentTimeMillis() - timerToSave.getStartTime());
		sessionData.setPunchInBase(newElapsedTime);
		chronometer.setBase(newElapsedTime);
		chronometer.start();
		setupScreenLabels();
		saveState();
	}

	private void checkInCategory() {
		final CategoriesDialog dialog = new CategoriesDialog(this, R.style.DialogStyle);
		dialog.setCallback(this);
		dialog.show();
	}

	public void checkIn(final CategoryRecord selectedCategory) {
		// If the category is the same, continue timer
		if (((sessionData.getCategory() != null) && !sessionData.getCategory().equals(selectedCategory)) || sessionData.isPunchedOut()) {

			if (!sessionData.isPunchedOut()) {
				sessionData.stopTimer();
				timerDBAdapter.createTimer(sessionData.getCurrentTimerRecord());
			}

			sessionData.startTimer(selectedCategory);
			sessionData.setPunchInBase(SystemClock.elapsedRealtime());

			chronometer.setBase(sessionData.getPunchInBase());
			chronometer.start();

			sessionData.setTodayBase(timerDBAdapter.totalForTodayAndByCategory(selectedCategory));
			sessionData.setWeekBase(timerDBAdapter.totalForWeekAndByCategory(selectedCategory));

			setupScreenLabels();

			((TextView) findViewById(R.id.mainViewChronOutput)).setTextColor(Color.GREEN);

			saveState();
		}
	}

	private void checkOut() {
		if (!sessionData.isPunchedOut()) {

			sessionData.stopTimer();
			timerDBAdapter.createTimer(sessionData.getCurrentTimerRecord());

			chronometer.stop();

			((TextView) findViewById(R.id.mainViewChronOutput)).setTextColor(Color.RED);

			saveState();
		}
	}

	/**
	 * This method create the chronometer and his listener.
	 */
	private void setupChronometer() {
		chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			@Override
			public void onChronometerTick(final Chronometer chronometer) {
				final long tick = SystemClock.elapsedRealtime();
				final long elapsed = tick - sessionData.getPunchInBase();
				// Main Timer
				final TextView mainViewChronOutput = (TextView) findViewById(R.id.mainViewChronOutput);
				mainViewChronOutput.setText(DateTimeUtil.hrColMinColSec(elapsed, false));
				// Today Timer
				final long todayElapsed = (tick - chronometer.getBase()) + sessionData.getTodayBase();
				final TextView todayViewChronOutput = (TextView) findViewById(R.id.todayViewChronOutput);
				todayViewChronOutput.setText("Today : " + DateTimeUtil.hrColMinColSec(todayElapsed, false));
				// Week Timer
				final long weekElapsed = (tick - chronometer.getBase()) + sessionData.getWeekBase();
				final TextView weekViewChronOutput = (TextView) findViewById(R.id.weekViewChronOutput);
				weekViewChronOutput.setText("Week : " + DateTimeUtil.hrColMinColSec(weekElapsed, false));
				// Progress Bar
				final double targetHour = sessionData.getCurrentTimerRecord().getCategory().getTargetHour();
				if (targetHour == 0) {
					progressBar.setProgress(100);
				} else {
					final long progressTarget = (long) (targetHour * 3600000);
					progressBar.setProgress((int) (todayElapsed * 100 / progressTarget));
				}

			}
		});
	}

	private void restoreSession() {
		reloadState();
		if (!sessionData.isPunchedOut()) {
			chronometer.setBase(sessionData.getPunchInBase());
			((TextView) findViewById(R.id.mainViewChronOutput)).setTextColor(Color.GREEN);
			chronometer.start();
		}
	}

	private void saveState() {
		deleteFile("curr_state");
		try {
			final ObjectOutputStream out = new ObjectOutputStream(openFileOutput("curr_state", 0));
			out.writeObject(sessionData);
			out.close();
		} catch (final IOException e) {
			Log.e("TimeTracker", "Error Saving State", e);
		}
	}

	private void reloadState() {
		try {
			final String[] fileList = fileList();
			for (final String fileName : fileList) {
				if (fileName.equals("curr_state")) {
					final ObjectInputStream in = new ObjectInputStream(openFileInput(fileName));
					sessionData = (SessionData) in.readObject();
					in.close();
				}
			}
		} catch (final IOException e) {
			Log.e("TimeTracker", "Error Loading State", e);
		} catch (final ClassNotFoundException e) {
			Log.e("TimeTracker", "Error Loading State", e);
		}
		if (sessionData == null) {
			sessionData = new SessionData();
		}
	}

}
