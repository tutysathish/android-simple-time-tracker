package com.aknot.simpletimetracker.action;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

/**
 * 
 * @author Aknot
 * 
 */
public class IntentAction extends AbstractAction {
	private final Context mContext;
	private final Intent mIntent;

	public IntentAction(final Context context, final Intent intent, final int drawable) {
		super(drawable);
		mContext = context;
		mIntent = intent;
	}

	@Override
	public void performAction(final View view) {
		try {
			mContext.startActivity(mIntent);
		} catch (final ActivityNotFoundException e) {
			Toast.makeText(mContext, "Activity Not Found", Toast.LENGTH_SHORT).show();
		}
	}
}
