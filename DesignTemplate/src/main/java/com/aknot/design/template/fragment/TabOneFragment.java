package com.aknot.design.template.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aknot.design.template.R;

public class TabOneFragment extends Fragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final LinearLayout tabView = (LinearLayout) inflater.inflate(R.layout.tab_fragment, container, false);

		final TextView twTabTitle = (TextView) tabView.findViewById(R.id.twTabTitle);
		twTabTitle.setText("This is custom tab one");

		return tabView;
	}

}
