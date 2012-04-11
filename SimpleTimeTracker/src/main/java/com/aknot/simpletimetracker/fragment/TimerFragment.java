package com.aknot.simpletimetracker.fragment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TimerFragment extends AbstractFragment {

	private Chronometer chronometer;
	private ProgressBar progressBar;

	private SessionData sessionData = new SessionData();

	private final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(getActivity());

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		return inflater.inflate(R.layout.tab_timer, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		progressBar = (ProgressBar) view.findViewById(R.id.timerProgressBar);
		chronometer = (Chronometer) view.findViewById(R.id.chron);
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
	public String getTitle() {
		return getResources().getString(R.string.timer_title);
	}

	private void setupButtons() {
		final Button punchInButton = (Button) getView().findViewById(R.id.btnPunchIn);
		punchInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				checkInCategory();
			}
		});
		final Button punchOutButton = (Button) getView().findViewById(R.id.btnPunchOut);
		punchOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				checkOut();
			}
		});
	}

	private void checkInCategory() {
		final CategoriesDialog dialog = new CategoriesDialog(getActivity(), R.style.DialogStyle);
		dialog.setCallback(this);
		dialog.show();
	}

	public void checkIn(final CategoryRecord selectedCategory) {
		// If the category is the same, continue timer
		if (((sessionData.getCategory() != null) && !sessionData.getCategory().equals(selectedCategory))
				|| sessionData.isPunchedOut()) {

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

			((TextView) getView().findViewById(R.id.mainViewChronOutput)).setTextColor(Color.GREEN);

			saveState();
		}
	}

	private void checkOut() {
		if (!sessionData.isPunchedOut()) {

			sessionData.stopTimer();
			timerDBAdapter.createTimer(sessionData.getCurrentTimerRecord());

			chronometer.stop();

			((TextView) getView().findViewById(R.id.mainViewChronOutput)).setTextColor(Color.RED);

			saveState();
		}
	}

	private void saveState() {
		getActivity().deleteFile("curr_state");
		try {
			final ObjectOutputStream out = new ObjectOutputStream(getActivity().openFileOutput("curr_state", 0));
			out.writeObject(sessionData);
			out.close();
		} catch (final IOException e) {
			Log.e("TimeTracker", "Error Saving State", e);
		}
	}

	private void restoreSession() {
		reloadState();
		if (!sessionData.isPunchedOut()) {
			chronometer.setBase(sessionData.getPunchInBase());
			((TextView) getView().findViewById(R.id.mainViewChronOutput)).setTextColor(Color.GREEN);
			chronometer.start();
		}
	}

	private void reloadState() {
		try {
			final String[] fileList = getActivity().fileList();
			for (final String fileName : fileList) {
				if (fileName.equals("curr_state")) {
					final ObjectInputStream in = new ObjectInputStream(getActivity().openFileInput(fileName));
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

	private void showDialog() {
		if (!sessionData.isPunchedOut()) {
			final TimerEditDialog timerEditDialog = new TimerEditDialog(getActivity());
			timerEditDialog.buildEditDialog(sessionData.getCurrentTimerRecord(), this).show();
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
				final TextView mainViewChronOutput = (TextView) getView().findViewById(R.id.mainViewChronOutput);
				mainViewChronOutput.setText(DateTimeUtil.hrColMinColSec(elapsed, false));
				// Today Timer
				final long todayElapsed = (tick - chronometer.getBase()) + sessionData.getTodayBase();
				final TextView todayViewChronOutput = (TextView) getView().findViewById(R.id.todayViewChronOutput);
				todayViewChronOutput.setText("Today : " + DateTimeUtil.hrColMinColSec(todayElapsed, false));
				// Week Timer
				final long weekElapsed = (tick - chronometer.getBase()) + sessionData.getWeekBase();
				final TextView weekViewChronOutput = (TextView) getView().findViewById(R.id.weekViewChronOutput);
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

	/**
	 * This method create the labels on screen.
	 */
	private void setupScreenLabels() {
		final TextView tvWeek = (TextView) getView().findViewById(R.id.currentWeek);
		tvWeek.setText("Week : " + DateTimeUtil.getCurrentWeek());

		final TextView tvDate = (TextView) getView().findViewById(R.id.currentDate);
		tvDate.setText("Date : " + DateTimeUtil.getCurrentFormatedDate());

		boolean showTv = sessionData.getCurrentTimerRecord() != null;
		showTv &= sessionData.getCurrentTimerRecord().getCategory() != null;
		showTv &= !sessionData.isPunchedOut();

		final TextView labelTv = (TextView) getView().findViewById(R.id.tvTimeInActivity);
		final TextView tvStartTime = (TextView) getView().findViewById(R.id.tvStartTime);
		final TextView tvEndTime = (TextView) getView().findViewById(R.id.tvEndTime);

		if (showTv) {
			labelTv.setText("Time spent : " + sessionData.getCurrentTimerRecord().getCategory().getCategoryName());
			tvStartTime.setText("Start time : " + sessionData.getCurrentTimerRecord().getStartTimeStr());
			tvEndTime.setText("End time : "
					+ sessionData.getCurrentTimerRecord().getEstimatedEndTimeStr(sessionData.getTodayBase()));
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

	public void saveTimer(final TimerRecord timerToSave) {
		chronometer.stop();
		final long newElapsedTime = SystemClock.elapsedRealtime()
				- (System.currentTimeMillis() - timerToSave.getStartTime());
		sessionData.setPunchInBase(newElapsedTime);
		chronometer.setBase(newElapsedTime);
		chronometer.start();
		setupScreenLabels();
		saveState();
	}

}
