package com.aknot.simpletimetracker.action;

/**
 * 
 * @author Aknot
 * 
 */
public abstract class AbstractAction implements Action {

	final private int mDrawable;

	public AbstractAction(final int drawable) {
		mDrawable = drawable;
	}

	@Override
	public int getDrawable() {
		return mDrawable;
	}

}
