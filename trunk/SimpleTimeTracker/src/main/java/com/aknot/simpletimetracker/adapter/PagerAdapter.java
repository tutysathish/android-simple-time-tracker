package com.aknot.simpletimetracker.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aknot.simpletimetracker.indicator.TitleProvider;

/**
 * 
 * Pager adapter for all the fragments used by the view pager.
 * 
 * @author Aknot
 */
public class PagerAdapter extends FragmentPagerAdapter implements TitleProvider {

	private static final String[] CONTENT = new String[] { "Summary", "Timer", "Report" };

	private final List<Fragment> fragments;

	public PagerAdapter(final FragmentManager fm, final List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(final int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	@Override
	public String getTitle(int position) {
		return CONTENT[position];
	}
}
