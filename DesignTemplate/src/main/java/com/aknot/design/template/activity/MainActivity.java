package com.aknot.design.template.activity;

import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.aknot.design.template.R;
import com.aknot.design.template.adapter.PagerAdapter;
import com.aknot.design.template.fragment.TabOneFragment;
import com.aknot.design.template.fragment.TabThreeFragment;
import com.aknot.design.template.fragment.TabTwoFragment;

/**
 * @author Aknot
 */
public class MainActivity extends FragmentActivity {

	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.main);
		this.initialisePaging();
	}

	private void initialisePaging() {
		final List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, TabOneFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, TabTwoFragment.class.getName()));
		fragments.add(Fragment.instantiate(this, TabThreeFragment.class.getName()));

		this.mPagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);

		final ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setAdapter(this.mPagerAdapter);
	}
}
