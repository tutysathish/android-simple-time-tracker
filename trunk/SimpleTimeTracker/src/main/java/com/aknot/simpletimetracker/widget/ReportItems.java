package com.aknot.simpletimetracker.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;

/**
 * 
 * @author aknot
 * 
 */
public class ReportItems extends LinearLayout {

	// Constant
	private static final String HEADER = "Header";
	private static final String DETAIL = "Detail";

	private final LayoutInflater mInflater;
	private final LinearLayout mReport;

	private LinearLayout mReportItem = null;

	public ReportItems(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mReport = new LinearLayout(context);
		mReport.setOrientation(LinearLayout.VERTICAL);
		mReport.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		addView(mReport);
	}

	@Override
	public void removeAllViews() {
		mReport.removeAllViews();
	}

	public void addItem() {
		mReportItem = (LinearLayout) mInflater.inflate(R.layout.report_item, null);
		mReport.addView(mReportItem);
	}

	public void putHeader(final String headerLabel) {
		if (mReportItem == null) {
			addItem();
		}

		final TextView reportWeek = (TextView) mReportItem.findViewById(R.id.report_week);
		reportWeek.setText(headerLabel);
	}

	public TextView putDay(final String lineLabel) {
		if (mReportItem == null) {
			addItem();
		}

		final TextView rowTextView = new TextView(this.getContext());
		rowTextView.setText("  " + lineLabel);
		rowTextView.setTextColor(Color.GREEN);
		rowTextView.setTag(HEADER);

		final LinearLayout reportLines = (LinearLayout) mReportItem.findViewById(R.id.report_lines);
		reportLines.addView(rowTextView);

		return rowTextView;
	}

	public TextView putHours(final String lineLabel) {
		if (mReportItem == null) {
			addItem();
		}

		final TextView rowTextView = new TextView(this.getContext());
		rowTextView.setText("    " + lineLabel);
		rowTextView.setTextColor(Color.WHITE);
		rowTextView.setTag(DETAIL);

		final LinearLayout reportLines = (LinearLayout) mReportItem.findViewById(R.id.report_lines);
		reportLines.addView(rowTextView);

		return rowTextView;
	}

}
