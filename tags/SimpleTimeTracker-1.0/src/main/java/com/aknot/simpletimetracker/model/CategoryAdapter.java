package com.aknot.simpletimetracker.model;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.database.CategoryDBAdapter;

/**
 * @author Aknot
 */
public final class CategoryAdapter extends ArrayAdapter<CategoryRecord> {

	private final Context context;
	private final boolean withDescription;
	private final int viewId;
	private final List<CategoryRecord> items;

	public CategoryAdapter(Context context, int textViewResourceId, List<CategoryRecord> items, boolean withDescription) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
		this.withDescription = withDescription;
		this.viewId = textViewResourceId;
	}

	public static CategoryAdapter getCategoryAdapterFromDB(Context context, int viewId, boolean withDescription) {
		CategoryDBAdapter CategoryDBAdapter = new CategoryDBAdapter(context);
		List<CategoryRecord> categories = CategoryDBAdapter.fetchAllCategories();
		return new CategoryAdapter(context, viewId, categories, withDescription);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(viewId, null);
		}
		CategoryRecord category = items.get(position);
		if (category != null) {
			TextView categoryNameView = (TextView) view.findViewById(R.id.category_list_view_name_field);
			TextView categoryDescriptionView = (TextView) view.findViewById(R.id.category_list_view_description_field);
			if (withDescription) {
				categoryNameView.setText("Name: " + category.getCategoryName());
				categoryDescriptionView.setText("Description: " + category.getDescription());
			} else {
				categoryNameView.setText(category.getCategoryName());
			}
		}
		return view;
	}
}
