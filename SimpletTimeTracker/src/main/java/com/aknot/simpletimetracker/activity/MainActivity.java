package com.aknot.simpletimetracker.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TabHost;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.DatabaseInstance;
import com.aknot.simpletimetracker.widget.FlingableTabHost;

/**
 * This is the main class of the application.
 * 
 * @author Aknot
 */
public final class MainActivity extends TabActivity {

	private GestureDetector gestureDetector;

	/**
	 * Create the main tabbed view.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialize the database.
		DatabaseInstance.initialize(this);

		// Resource object to get Drawables.
		final Resources res = getResources();
		// The activity TabHost.
		final FlingableTabHost tabHost = (FlingableTabHost) getTabHost();
		// Resusable TabSpec for each tab.
		TabHost.TabSpec spec;
		// Reusable Intent for each tab.
		Intent intent;

		// Initialize a TabSpec for each tab and add it to the TabHost
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, TimerActivity.class);
		spec = tabHost.newTabSpec("tab1").setIndicator(res.getString(R.string.timer_label), res.getDrawable(R.drawable.ic_tab_timer)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, SummaryActivity.class);
		spec = tabHost.newTabSpec("tab2").setIndicator(res.getString(R.string.summary_label), res.getDrawable(R.drawable.ic_tab_sumary)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ReportActivity.class);
		spec = tabHost.newTabSpec("tab3").setIndicator(res.getString(R.string.report_label), res.getDrawable(R.drawable.ic_tab_report)).setContent(intent);
		tabHost.addTab(spec);

		if (savedInstanceState != null) {
			tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		} else {
			tabHost.setCurrentTab(0);
		}

		gestureDetector = tabHost.getGesture();
	}

	/**
	 * On pause - Close the database.
	 */
	@Override
	protected void onPause() {
		super.onPause();

		DatabaseInstance.close();
	}

	/**
	 * On resume - Open the database.
	 */
	@Override
	public void onResume() {
		super.onResume();

		DatabaseInstance.open();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", getTabHost().getCurrentTabTag());
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 0, R.string.categories_label);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Call Menu activity
		final Intent intent = new Intent(this, CategoryActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}

}
