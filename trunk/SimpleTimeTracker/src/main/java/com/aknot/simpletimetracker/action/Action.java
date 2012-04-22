package com.aknot.simpletimetracker.action;

import android.view.View;

/**
 * 
 * @author Aknot
 * 
 */
public interface Action {
	public int getDrawable();

	public void performAction(View view);
}
