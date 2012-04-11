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
import com.aknot.simpletimetracker.adapter.PagerAdapter;
import com.aknot.simpletimetracker.database.DatabaseInstance;
import com.aknot.simpletimetracker.fragment.ReportFragment;
import com.aknot.simpletimetracker.fragment.SummaryFragment;
import com.aknot.simpletimetracker.fragment.TimerFragment;
import com.aknot.simpletimetracker.indicator.TitlePageIndicator;

public class ViewPagerActivity extends FragmentActivity {

	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.view_pager);

		// Initialize the database.
		DatabaseInstance.initialize(this);

		initialisePaging();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
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
	protected void onPause() {
		super.onPause();
		DatabaseInstance.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		DatabaseInstance.open();
	}

	private void initialisePaging() {
		final List<Fragment> fragments = new Vector<Fragment>();

		fragments.add(Fragment.instantiate(this, SummaryFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, TimerFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ReportFragment.class.getName()));

		this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);

		final ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setAdapter(this.mPagerAdapter);

		final TitlePageIndicator indicator = (TitlePageIndicator) super.findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

}
