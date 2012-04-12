package com.aknot.simpletimetracker.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aknot.simpletimetracker.fragment.AbstractFragment;

/**
 * 
 * View pager adapter for all the fragments used by the view pager.
 * 
 * @author aknot
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

	// List of all available fragments
	private final List<AbstractFragment> fragments;

	/**
	 * Constructor
	 */
	public ViewPagerAdapter(final FragmentManager fm, final List<AbstractFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(final int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragments.get(position).getTitle();
	}

}
