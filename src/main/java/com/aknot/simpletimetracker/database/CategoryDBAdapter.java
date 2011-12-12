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

	public CategoryDBAdapter(Context context) {
		super();
		this.context = context;
	}

	public long createCategory(final CategoryRecord category) {
		return DatabaseInstance.getDatabase().insert(DatabaseOpenHelper.CATEGORY_TABLE, null, categoryContentValuesList(category));
	}

	public CategoryRecord fetchByRowID(final long rowId) throws SQLException {
		Cursor cursor = DatabaseInstance.getDatabase().query(true, DatabaseOpenHelper.CATEGORY_TABLE, columnList(), "_id=" + rowId, null, null, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		CategoryRecord category = fillCategoryFromCursor(cursor);
		cursor.close();
		return category;
	}

	public List<CategoryRecord> fetchAllCategories() {
		List<CategoryRecord> result = new ArrayList<CategoryRecord>();
		Cursor cursor = DatabaseInstance.getDatabase().query(DatabaseOpenHelper.CATEGORY_TABLE, columnList(), null, null, null, null, "category_name");
		while (cursor.moveToNext()) {
			CategoryRecord category = fillCategoryFromCursor(cursor);
			result.add(category);
		}
		if (result.size() == 0) {
			initialize();
			result = fetchAllCategories();
		}
		cursor.close();
		return result;
	}

	public long update(final CategoryRecord timeSliceCategory) {
		return DatabaseInstance.getDatabase().update(DatabaseOpenHelper.CATEGORY_TABLE, this.categoryContentValuesList(timeSliceCategory),
				"_id = " + timeSliceCategory.getRowId(), null);
	}

	public boolean delete(final long rowId) {
		return DatabaseInstance.getDatabase().delete(DatabaseOpenHelper.CATEGORY_TABLE, "_id=" + rowId, null) > 0;
	}

	private void createCategoryFromName(String name) {
		CategoryRecord category = new CategoryRecord();
		category.setCategoryName(name);
		createCategory(category);
	}

	private String[] columnList() {
		List<String> columns = new ArrayList<String>();
		columns.add("_id");
		columns.add("category_name");
		columns.add("description");
		return columns.toArray(new String[0]);
	}

	private ContentValues categoryContentValuesList(final CategoryRecord category) {
		ContentValues values = new ContentValues();
		values.put("category_name", category.getCategoryName());
		values.put("description", category.getDescription());
		return values;
	}

	private CategoryRecord fillCategoryFromCursor(Cursor cursor) {
		CategoryRecord category = new CategoryRecord();
		if (!cursor.isAfterLast()) {
			category.setRowId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
			category.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
			category.setDescription(cursor.getString(cursor.getColumnIndex("description")));
		}
		return category;
	}

	private void initialize() {
		Resources resources = context.getResources();
		String[] catNames = resources.getStringArray(R.array.default_categories);
		for (String catName : catNames) {
			createCategoryFromName(catName);
		}
	}

}
