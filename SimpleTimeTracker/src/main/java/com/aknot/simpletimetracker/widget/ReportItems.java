package com.aknot.simpletimetracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.aknot.simpletimetracker.R;

/**
 * 
 * @author aknot
 * 
 */
public class ReportItems extends LinearLayout {

	// Constant
	// private static final String HEADER = "Header";

	// private static final String DETAIL = "Detail";

	private final LayoutInflater mInflater;
	private final LinearLayout mReport;

	// private LinearLayout mReportItem = null;

	public ReportItems(Context context, AttributeSet attrs) {
		super(context, attrs);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mReport = new LinearLayout(context);
		mReport.setOrientation(LinearLayout.VERTICAL);

		addView(mReport);

		// mReport = (LinearLayout) mInflater.inflate(R.layout.report, null);
		// addView(mReport);
	}

	@Override
	public void removeAllViews() {
		mReport.removeAllViews();
	}

	public void addItem(final String itemLabel) {
		LinearLayout mReportItem = (LinearLayout) mInflater.inflate(R.layout.report_item, null);
		mReport.addView(mReportItem);
	}

	public void addHeader(final String headerLabel) {

		// mReportItem = (LinearLayout) mInflater.inflate(R.layout.report_item, null);
		// mReport.addView(mReportItem);

		// final TextView headerTextView = new TextView(this.getActivity());
		// headerTextView.setText("Week : " + entry.getKey());
		// headerTextView.setTextColor(Color.parseColor("#FF6899FF"));
		// headerTextView.setTypeface(Typeface.DEFAULT_BOLD);
		//
		// linearLayout.addView(headerTextView);
	}

	public void addLine(final String lineLabel) {
		// TextView rowTextView = new TextView(this.getContext());
		// rowTextView.setText("    " + lineLabel);
		// rowTextView.setTextColor(Color.GREEN);
		// rowTextView.setTag(HEADER);
		//
		// LinearLayout view = (LinearLayout) mReportItem.findViewById(R.id.report_lines);
		// view.addView(rowTextView);
	}

}
