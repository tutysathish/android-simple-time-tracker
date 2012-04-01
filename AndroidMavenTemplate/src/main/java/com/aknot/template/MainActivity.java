package com.aknot.template;

import android.app.Activity;
import android.os.Bundle;

/**
 * This is the main class of the application.
 * 
 * @author Aknot
 */
public final class MainActivity extends Activity {

	/**
	 * Create the main view.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

}
