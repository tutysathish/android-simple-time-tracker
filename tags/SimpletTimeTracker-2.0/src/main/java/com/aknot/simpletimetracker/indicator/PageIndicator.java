package com.aknot.simpletimetracker.indicator;

import android.support.v4.view.ViewPager;

/**
 * Page indicator interface.
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {

	public void setViewPager(final ViewPager view, final int defaultPage);

	public void setCurrentItem(final int item);

	public void setOnPageChangeListener(final ViewPager.OnPageChangeListener listener);

	public void notifyDataSetChanged();
}
