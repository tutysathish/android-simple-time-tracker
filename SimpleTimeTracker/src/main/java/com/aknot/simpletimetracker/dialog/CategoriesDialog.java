package com.aknot.simpletimetracker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.fragment.TimerFragment;
import com.aknot.simpletimetracker.model.CategoryAdapter;
import com.aknot.simpletimetracker.model.CategoryRecord;

/**
 * Dialog to show categories.
 * 
 * @author Aknot
 */
public final class CategoriesDialog extends Dialog {

	// List of categories
	private final ListView categories;

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param style
	 */
	public CategoriesDialog(final Context context, final int style) {
		super(context, style);
		categories = new ListView(context);
		categories.setAdapter(CategoryAdapter.getCategoryAdapterFromDB(context, R.layout.check_in_categories, false));
		final LinearLayout contentView = new LinearLayout(context);
		contentView.setOrientation(LinearLayout.VERTICAL);
		contentView.addView(categories);
		setContentView(contentView);
	}

	/**
	 * This method well handle the chosen category and start the timer.
	 * 
	 * @param callback
	 */
	public void setCallback(final TimerFragment callback) {
		categories.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * Call back when category is chosen
			 */
			@Override
			public void onItemClick(final AdapterView<?> adapterView, final View arg1, final int itemPosition, final long l) {
				final CategoryRecord category = (CategoryRecord) categories.getItemAtPosition(itemPosition);
				callback.checkIn(category);
				dismiss();
			}

		});
	}

}
