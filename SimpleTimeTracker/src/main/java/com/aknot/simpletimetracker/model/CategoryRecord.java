package com.aknot.simpletimetracker.model;

import java.io.Serializable;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.CategoryDBAdapter;

/**
 * @author Aknot
 */
public final class CategoryRecord implements Serializable, Comparable<CategoryRecord> {

	private static final long serialVersionUID = 4899523432240132519L;

	private int rowId;

	private String categoryName;
	private String description;
	private int targetHour;

	public int getRowId() {
		return rowId;
	}

	public void setRowId(final int rowId) {
		this.rowId = rowId;
	}

	public String getCategoryName() {
		if (categoryName == null) {
			return "N/A";
		}
		return categoryName;
	}

	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDescription() {
		if (description == null) {
			return "";
		}
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public int getTargetHour() {
		return targetHour;
	}

	public void setTargetHour(final int targetHour) {
		this.targetHour = targetHour;
	}

	public static ArrayAdapter<CategoryRecord> getCategoryAdapter(final Context context) {
		final CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter(context);
		final CategoryRecord[] categories = categoryDBAdapter.fetchAllCategories().toArray(new CategoryRecord[0]);
		final ArrayAdapter<CategoryRecord> arrayAdapter = new ArrayAdapter<CategoryRecord>(context, android.R.layout.simple_spinner_item, categories);
		arrayAdapter.setDropDownViewResource(R.layout.categories_spinner_item);
		return arrayAdapter;
	}

	@Override
	public String toString() {
		return categoryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CategoryRecord other = (CategoryRecord) obj;
		if (categoryName == null) {
			if (other.categoryName != null) {
				return false;
			}
		} else if (!categoryName.equals(other.categoryName)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final CategoryRecord anotherCategory) {
		return this.categoryName.compareTo(anotherCategory.categoryName);
	}

}
