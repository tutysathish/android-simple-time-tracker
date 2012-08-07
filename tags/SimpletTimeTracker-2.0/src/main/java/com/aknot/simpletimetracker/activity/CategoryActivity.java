package com.aknot.simpletimetracker.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.CategoryDBAdapter;
import com.aknot.simpletimetracker.database.TimerDBAdapter;
import com.aknot.simpletimetracker.dialog.CategoryEditDialog;
import com.aknot.simpletimetracker.model.CategoryAdapter;
import com.aknot.simpletimetracker.model.CategoryRecord;

/**
 * This activity give the possibility to manage the categories.
 * 
 * @author Aknot
 * 
 */
public class CategoryActivity extends ListActivity {

	private CategoryRecord categoryClicked;

	private final CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter(this);

	private boolean updating;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_categories_list);

		registerForContextMenu(getListView());

		refreshCategoryList();
	}

	public void onEditDialogSave(final CategoryRecord category) {
		if (updating) {
			categoryDBAdapter.update(category);
		} else {
			categoryDBAdapter.createCategory(category);
		}
		refreshCategoryList();
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		categoryClicked = (CategoryRecord) getListView().getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);

		menu.setHeaderTitle(categoryClicked.getCategoryName());

		menu.add(0, 1, 0, R.string.category_menu_edit);
		menu.add(0, 2, 0, R.string.category_menu_del);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			updating = true;
			final CategoryEditDialog categoryEditDialog = new CategoryEditDialog(this);
			categoryEditDialog.buildEditDialog(categoryClicked, this).show();
			return true;
		case 2:
			final TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);
			if (timerDBAdapter.categoryHasTimerRecord(categoryClicked)) {
				showDeleteWarningDialog();
			} else {
				categoryDBAdapter.delete(categoryClicked.getRowId());
			}
			refreshCategoryList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 3, 0, R.string.category_menu_add);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case 3:
			updating = false;
			new CategoryEditDialog(this).buildEditDialog(null, this).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshCategoryList() {
		setListAdapter(CategoryAdapter.getCategoryAdapterFromDB(this, R.layout.manage_categories_row, true));
	}

	private void showDeleteWarningDialog() {
		final CharSequence[] items = { "Go ahead and delete it.", "Don't delete it." };
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning: Time is already assigned to " + categoryClicked + ":");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int item) {
				if (item == 0) {
					categoryDBAdapter.delete(categoryClicked.getRowId());
					refreshCategoryList();
				}
			}
		});
		builder.create().show();
	}

}
