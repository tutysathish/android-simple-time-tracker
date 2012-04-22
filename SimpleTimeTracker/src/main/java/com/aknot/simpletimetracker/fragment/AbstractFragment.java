package com.aknot.simpletimetracker.fragment;

import android.support.v4.app.Fragment;

/**
 * 
 * @author Aknot
 * 
 */
public abstract class AbstractFragment extends Fragment {

	// Constant
	private static final String EMPTY = "";

	// Title of the fragment
	private String title = EMPTY;

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

}
