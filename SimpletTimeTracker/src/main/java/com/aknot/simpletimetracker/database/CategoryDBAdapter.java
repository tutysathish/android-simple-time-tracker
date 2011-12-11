package com.aknot.simpletimetracker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.model.CategoryRecord;

/**
 * @author Aknot
 */
public final class CategoryDBAdapter {

	private final Context context;

	public CategoryDBAdapter(final Context context) {
		super();
		this.context = context;
	}

	public long createCategory(final CategoryRecord category) {
		return DatabaseInstance.getDatabase().insert(DatabaseOpenHelper.CATEGORY_TABLE, null, categoryContentValuesList(category));
	}

	public CategoryRecord fetchByRowID(final long rowId) throws SQLException {
		final Cursor cursor = DatabaseInstance.getDatabase().query(true, DatabaseOpenHelper.CATEGORY_TABLE, columnList(), "_id=" + rowId, null, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		final CategoryRecord category = fillCategoryFromCursor(cursor);
		cursor.close();
		return category;
	}

	public List<CategoryRecord> fetchAllCategories() {
		List<CategoryRecord> result = new ArrayList<CategoryRecord>();
		final Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.CATEGORY_TABLE, columnList(), null, null, null, null, "category_name");
		while (cursor.moveToNext()) {
			final CategoryRecord category = fillCategoryFromCursor(cursor);
			result.add(category);
		}
		if (context != null && result.size() == 0) {
			initialize();
			result = fetchAllCategories();
		}
		cursor.close();
		return result;
	}

	public long update(final CategoryRecord category) {
		return DatabaseInstance.getDatabase().update(DatabaseOpenHelper.CATEGORY_TABLE, this.categoryContentValuesList(category),
				"_id = " + category.getRowId(), null);
	}

	public boolean delete(final long rowId) {
		return DatabaseInstance.getDatabase().delete(DatabaseOpenHelper.CATEGORY_TABLE, "_id=" + rowId, null) > 0;
	}

	private void createCategoryFromName(final String name) {
		final CategoryRecord category = new CategoryRecord();
		category.setCategoryName(name);
		createCategory(category);
	}

	private String[] columnList() {
		final List<String> columns = new ArrayList<String>();
		columns.add("_id");
		columns.add("category_name");
		columns.add("description");
		columns.add("target_hour");
		return columns.toArray(new String[0]);
	}

	private ContentValues categoryContentValuesList(final CategoryRecord category) {
		final ContentValues values = new ContentValues();
		values.put("category_name", category.getCategoryName());
		values.put("description", category.getDescription());
		values.put("target_hour", category.getTargetHour());
		return values;
	}

	private CategoryRecord fillCategoryFromCursor(final Cursor cursor) {
		final CategoryRecord category = new CategoryRecord();
		if (!cursor.isAfterLast()) {
			category.setRowId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
			category.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
			category.setDescription(cursor.getString(cursor.getColumnIndex("description")));
			category.setTargetHour(cursor.getInt(cursor.getColumnIndex("target_hour")));
		}
		return category;
	}

	private void initialize() {
		final Resources resources = context.getResources();
		final String[] catNames = resources.getStringArray(R.array.default_categories);
		for (final String catName : catNames) {
			createCategoryFromName(catName);
		}
	}

}
