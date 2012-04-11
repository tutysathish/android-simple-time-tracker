package com.aknot.simpletimetracker.indicator;

import android.support.v4.view.ViewPager;

/**
 * Page indicator interface.
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {

	public void setViewPager(ViewPager view);

	public void setCurrentItem(int item);

	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

	public void notifyDataSetChanged();
}
