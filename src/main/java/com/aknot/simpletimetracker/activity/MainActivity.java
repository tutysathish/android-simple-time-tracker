package com.aknot.simpletimetracker.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.DatabaseInstance;

/**
 * This is the main class of the application.
 * 
 * @author Aknot
 */
public final class MainActivity extends TabActivity {

	/**
	 * Create the main tabbed view.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialize the database.
		DatabaseInstance.initialize(this);

		Resources res = getResources(); // Resource object to get Drawables.
		TabHost tabHost = getTabHost(); // The activity TabHost.
		TabHost.TabSpec spec; // Resusable TabSpec for each tab.
		Intent intent; // Reusable Intent for each tab.

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

		tabHost.setCurrentTab(0);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 0, R.string.categories_label);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Call Menu activity
		Intent intent = new Intent(this, CategoryActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

}
