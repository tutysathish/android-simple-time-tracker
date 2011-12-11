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
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.dialog.CategoriesDialog;
import com.aknot.simpletimetracker.model.CategoryRecord;
import com.aknot.simpletimetracker.utils.DateTimeUtil;
import com.aknot.simpletimetracker.utils.SessionData;

/**
 * @author Aknot
 */
public final class TimerActivity extends Activity {

	private Chronometer chronometer;
	private SessionData sessionData = new SessionData();
	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_timer);
	}

	@Override
	public void onResume() {
		super.onResume();

		setupButtons();
		createChronometer();

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
				punchInCategory();
			}
		});
		final Button punchOutButton = (Button) findViewById(R.id.btnPunchOut);
		punchOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				punchOut();
			}
		});
	}

	private void setupScreenLabels() {
		final TextView tvWeek = (TextView) findViewById(R.id.currentWeek);
		tvWeek.setText("Week : " + DateTimeUtil.getCurrentWeek());

		final TextView tvDate = (TextView) findViewById(R.id.currentDate);
		tvDate.setText("Date : " + DateTimeUtil.getCurrentFormatedDate());

		final TextView labelTv = (TextView) findViewById(R.id.tvTimeInActivity);
		final TextView tvStartTime = (TextView) findViewById(R.id.tvStartTime);

		boolean showTv = sessionData.getCurrentTimerRecord() != null;
		showTv &= sessionData.getCurrentTimerRecord().getCategory() != null;
		showTv &= !sessionData.isPunchedOut();

		if (showTv) {
			labelTv.setText("Time spent " + sessionData.getCurrentTimerRecord().getCategory().getCategoryName() + ": ");
			tvStartTime.setText("Start time : " + sessionData.getCurrentTimerRecord().getStartTimeStr());
		} else {
			labelTv.setText("No current activity");
			tvStartTime.setText("Start time : ");
		}
	}

	private void punchInCategory() {
		final CategoriesDialog dialog = new CategoriesDialog(this, R.style.DialogStyle);
		dialog.setCallback(this);
		dialog.show();
	}

	public void punchIn(final CategoryRecord selectedCategory) {
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

	private void punchOut() {
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
	private void createChronometer() {
		chronometer = (Chronometer) findViewById(R.id.chron);
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
			ObjectOutputStream out = new ObjectOutputStream(openFileOutput("curr_state", 0));
			out.writeObject(sessionData);
			out.close();
		} catch (IOException e) {
			Log.e("TimeTracker", "Error Saving State", e);
		}
	}

	private void reloadState() {
		try {
			String[] fileList = fileList();
			for (String fileName : fileList) {
				if (fileName.equals("curr_state")) {
					ObjectInputStream in = new ObjectInputStream(openFileInput(fileName));
					sessionData = (SessionData) in.readObject();
					in.close();
				}
			}
		} catch (IOException e) {
			Log.e("TimeTracker", "Error Loading State", e);
		} catch (ClassNotFoundException e) {
			Log.e("TimeTracker", "Error Loading State", e);
		}
		if (sessionData == null) {
			sessionData = new SessionData();
		}
	}

}
