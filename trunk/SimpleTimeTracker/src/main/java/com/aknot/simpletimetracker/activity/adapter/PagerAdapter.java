package com.aknot.simpletimetracker.activity.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Aknot
 */
public class PagerAdapter extends FragmentPagerAdapter {

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
}
