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

	public CategoryAdapter(final Context context, final int textViewResourceId, final List<CategoryRecord> items, final boolean withDescription) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
		this.withDescription = withDescription;
		this.viewId = textViewResourceId;
	}

	public static CategoryAdapter getCategoryAdapterFromDB(final Context context, final int viewId, final boolean withDescription) {
		final CategoryDBAdapter CategoryDBAdapter = new CategoryDBAdapter(context);
		final List<CategoryRecord> categories = CategoryDBAdapter.fetchAllCategories();
		return new CategoryAdapter(context, viewId, categories, withDescription);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(viewId, null);
		}
		final CategoryRecord category = items.get(position);
		if (category != null) {
			final TextView categoryNameView = (TextView) view.findViewById(R.id.category_list_view_name_field);
			final TextView categoryDescriptionView = (TextView) view.findViewById(R.id.category_list_view_description_field);
			final TextView catTargetField = (TextView) view.findViewById(R.id.category_list_view_target_hour_field);

			if (withDescription) {
				categoryNameView.setText("Name: " + category.getCategoryName());
				categoryDescriptionView.setText("Description: " + category.getDescription());
				catTargetField.setText("Target Hour: " + category.getTargetHour());
			} else {
				categoryNameView.setText(category.getCategoryName());
			}
		}
		return view;
	}
}
