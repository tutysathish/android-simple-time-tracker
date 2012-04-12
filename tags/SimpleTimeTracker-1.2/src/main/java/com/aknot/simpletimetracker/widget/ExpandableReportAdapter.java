package com.aknot.simpletimetracker.widget;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.activity.ReportActivity;
import com.aknot.simpletimetracker.model.TimerRecord;

public class ExpandableReportAdapter extends BaseExpandableListAdapter {

	private final LayoutInflater inflater;
	private final ReportActivity reportActivity;

	private final Map<String, List<TimerRecord>> children;

	private String lastStartDate;

	public ExpandableReportAdapter(final ReportActivity reportActivity, final Map<String, List<TimerRecord>> mapTimerRecords) {
		// Create the children
		children = mapTimerRecords;
		// Get inflater
		inflater = LayoutInflater.from(reportActivity);
		// Get the activity
		this.reportActivity = reportActivity;
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		final String key = (String) children.keySet().toArray()[groupPosition];
		return children.get(key).get(childPosition);
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView, final ViewGroup parent) {
		View childView;
		if (convertView == null) {
			childView = inflater.inflate(R.layout.report_child, parent, false);
		} else {
			childView = convertView;
		}

		// Get the timer record for this child
		final TimerRecord timerRecord = (TimerRecord) getChild(groupPosition, childPosition);

		// Get the layout to add the timer
		final LinearLayout linearLayout = (LinearLayout) childView.findViewById(R.id.childLinearLayout);

		linearLayout.removeAllViews();

		// Check if we need to add a header
		if (!lastStartDate.equals(timerRecord.getStartDateStr())) {
			lastStartDate = timerRecord.getStartDateStr();
			// Add a text view for the header
			linearLayout.addView(addDateHeaderView(childView.getContext(), lastStartDate));
		}

		// Add a text view for the timer
		linearLayout.addView(addTimerView(childView.getContext(), timerRecord));

		return childView;
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		if (children.keySet().size() == 0) {
			return 0;
		}
		final String key = (String) children.keySet().toArray()[groupPosition];
		return children.get(key).size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return children.keySet().toArray()[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return children.size();
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parent) {
		View groupView;
		if (convertView == null) {
			groupView = inflater.inflate(R.layout.report_group, parent, false);
		} else {
			groupView = convertView;
		}

		final String group = (String) getGroup(groupPosition);
		final TextView tv = (TextView) groupView.findViewById(R.id.tvGroup);
		tv.setText("Week " + group);

		return groupView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}

	@Override
	public void onGroupExpanded(final int groupPosition) {
		super.onGroupExpanded(groupPosition);
		lastStartDate = "";
	}

	private TextView addDateHeaderView(final Context context, final String dateText) {
		final TextView textView = new TextView(context);

		textView.setText(dateText);
		textView.setTextColor(Color.GREEN);
		textView.setTag("Header");

		reportActivity.registerForContextMenu(textView);

		return textView;
	}

	private TextView addTimerView(final Context context, final TimerRecord timerRecord) {
		final TextView textView = new TextView(context);

		final StringBuilder reportText = new StringBuilder();
		reportText.append("  ").append(timerRecord.getTitleWithDuration());

		textView.setText(reportText.toString(), TextView.BufferType.SPANNABLE);
		textView.setTag("Detail");

		reportActivity.getRowToTimerRecordRowIdMap().put(textView, timerRecord.getRowId());

		reportActivity.registerForContextMenu(textView);

		return textView;
	}
}
