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
 * 
 * @author Aknot
 * 
 */
public class CategoryActivity extends ListActivity {

	private CategoryRecord categoryClicked;

	private final CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter(this);

	private boolean updating;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_categories_list);

		registerForContextMenu(getListView());

		refreshCategoryList();
	}

	private void refreshCategoryList() {
		setListAdapter(CategoryAdapter.getCategoryAdapterFromDB(this, R.layout.manage_categories_row, true));
	}

	public void onEditDialogSave(CategoryRecord category) {
		if (updating) {
			categoryDBAdapter.update(category);
		} else {
			categoryDBAdapter.createCategory(category);
		}
		refreshCategoryList();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		categoryClicked = (CategoryRecord) getListView().getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);

		menu.setHeaderTitle(categoryClicked.getCategoryName());

		menu.add(0, 1, 0, R.string.category_menu_edit);
		menu.add(0, 2, 0, R.string.category_menu_del);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			updating = true;
			CategoryEditDialog categoryEditDialog = new CategoryEditDialog(this);
			categoryEditDialog.buildEditDialog(categoryClicked, this).show();
			return true;
		case 2:
			TimerDBAdapter timerDBAdapter = new TimerDBAdapter(this);
			if (timerDBAdapter.categoryHasTimeSlices(categoryClicked)) {
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

	private void showDeleteWarningDialog() {
		final CharSequence[] items = { "Go ahead and delete it.", "Don't delete it." };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning: Time is already assigned to " + categoryClicked + ":");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					categoryDBAdapter.delete(categoryClicked.getRowId());
					refreshCategoryList();
				}
			}
		});
		builder.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 3, 0, R.string.category_menu_add);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 3:
			updating = false;
			new CategoryEditDialog(this).buildEditDialog(null, this).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
