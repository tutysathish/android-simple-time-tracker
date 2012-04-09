package com.aknot.simpletimetracker.activity;

import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.adapter.PagerAdapter;
import com.aknot.simpletimetracker.fragment.ReportFragment;
import com.aknot.simpletimetracker.fragment.SummaryFragment;
import com.aknot.simpletimetracker.fragment.TimerFragment;

public class ViewPagerActivity extends FragmentActivity {

	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.main);
		this.initialisePaging();
	}

	private void initialisePaging() {
		final List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, TimerFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, SummaryFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, ReportFragment.class.getName()));

		this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);

		final ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setAdapter(this.mPagerAdapter);

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(final int arg0) {
			}

			@Override
			public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(final int arg0) {
			}
		});
	}

}
