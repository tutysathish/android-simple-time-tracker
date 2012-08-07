package com.aknot.simpletimetracker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.activity.CategoryActivity;
import com.aknot.simpletimetracker.filter.DecimalDigitsInputFilter;
import com.aknot.simpletimetracker.model.CategoryRecord;

public class CategoryEditDialog extends Dialog {

	public CategoryEditDialog(final Context context) {
		super(context);
	}

	public CategoryEditDialog buildEditDialog(final CategoryRecord category, final CategoryActivity categoryActivity) {
		setContentView(R.layout.edit_category);

		final EditText catNameField = (EditText) findViewById(R.id.edit_time_category_name_field);
		final EditText catDescField = (EditText) findViewById(R.id.edit_time_category_desc_field);

		final EditText catTargetField = (EditText) findViewById(R.id.edit_target_hours_field);
		catTargetField.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(1) });

		final Button saveButton = (Button) findViewById(R.id.edit_time_category_save_button);
		final Button cancelButton = (Button) findViewById(R.id.edit_time_category_cancel_button);

		final CategoryRecord currentCategory;
		if (category == null) {
			currentCategory = new CategoryRecord();
			setTitle("Creating a New Category");
		} else {
			currentCategory = category;
			setTitle("Editing " + category.getCategoryName());
		}

		catNameField.setText(currentCategory.getCategoryName());
		catDescField.setText(currentCategory.getDescription());
		catTargetField.setText(String.valueOf(currentCategory.getTargetHour()));

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				currentCategory.setCategoryName(catNameField.getText().toString());
				currentCategory.setDescription(catDescField.getText().toString());
				currentCategory.setTargetHour(Double.valueOf(catTargetField.getText().toString()));
				categoryActivity.onEditDialogSave(currentCategory);
				dismiss();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				cancel();
			}
		});

		return this;
	}
}
