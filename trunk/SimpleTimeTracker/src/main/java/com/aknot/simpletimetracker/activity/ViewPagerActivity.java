package com.aknot.simpletimetracker.activity;

import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.adapter.ViewPagerAdapter;
import com.aknot.simpletimetracker.database.DatabaseInstance;
import com.aknot.simpletimetracker.fragment.AbstractFragment;
import com.aknot.simpletimetracker.fragment.ReportFragment;
import com.aknot.simpletimetracker.fragment.SummaryFragment;
import com.aknot.simpletimetracker.fragment.TimerFragment;
import com.aknot.simpletimetracker.indicator.TitlePageIndicator;
import com.aknot.simpletimetracker.widget.ActionBar;

/**
 * 
 * Main activity that handle fragments through a view pager.
 * 
 * @author aknot
 * 
 */
public class ViewPagerActivity extends FragmentActivity {

	private ViewPagerAdapter viewPagerAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.view_pager);

		// Initialise the database.
		DatabaseInstance.initialize(this);

		initializeActionBar();
		initializePaging();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Get the menu
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Call category activity
		final Intent intent = new Intent(this, CategoryActivity.class);
		startActivity(intent);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Close the database
		DatabaseInstance.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// re-open the database
		DatabaseInstance.open();
	}

	/**
	 * This method initialize the action bar.
	 */
	private void initializeActionBar() {
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		// actionBar.setHomeLogo(R.drawable.action_bar_icon);
		actionBar.setTitle(getString(R.string.app_name));
	}

	/**
	 * This method initialize the view pager and the title indicator.
	 */
	private void initializePaging() {
		final List<AbstractFragment> fragments = new Vector<AbstractFragment>();

		fragments.add((SummaryFragment) Fragment.instantiate(this, SummaryFragment.class.getName()));
		fragments.add((TimerFragment) Fragment.instantiate(this, TimerFragment.class.getName()));
		fragments.add((ReportFragment) Fragment.instantiate(this, ReportFragment.class.getName()));

		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);

		final ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(this.viewPagerAdapter);

		final TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager, 1);
	}
}
