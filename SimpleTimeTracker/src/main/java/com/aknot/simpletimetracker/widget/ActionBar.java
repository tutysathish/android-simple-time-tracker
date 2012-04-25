package com.aknot.simpletimetracker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aknot.simpletimetracker.R;
import com.aknot.simpletimetracker.action.Action;
import com.aknot.simpletimetracker.action.ActionList;

/**
 * 
 * This widget contain/implements the layout for an action bar.
 * 
 * @author aknot
 * 
 */
public class ActionBar extends RelativeLayout implements OnClickListener {

	private final LayoutInflater mInflater;
	private final RelativeLayout mBarView;
	private final ImageView mLogoView;
	private final TextView mTitleView;
	private final LinearLayout mActionsView;

	public ActionBar(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
		addView(mBarView);

		mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);

		mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
		mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar);

		final CharSequence title = a.getString(R.styleable.ActionBar_title);
		if (title != null) {
			setTitle(title);
		}

		final Boolean showLogo = a.getBoolean(R.styleable.ActionBar_showLogo, false);
		if (showLogo != null) {
			setShowLogo(showLogo);
		}

		a.recycle();
	}

	@Override
	public void onClick(final View view) {
		final Object tag = view.getTag();
		if (tag instanceof Action) {
			final Action action = (Action) tag;
			action.performAction(view);
		}
	}

	public void addActions(final ActionList actionList) {
		final int actions = actionList.size();
		for (int i = 0; i < actions; i++) {
			addAction(actionList.get(i));
		}
	}

	public void addAction(final Action action) {
		final int index = mActionsView.getChildCount();
		addAction(action, index);
	}

	public void addAction(final Action action, final int index) {
		mActionsView.addView(inflateAction(action), index);
	}

	public void removeAllActions() {
		mActionsView.removeAllViews();
	}

	public void removeActionAt(final int index) {
		mActionsView.removeViewAt(index);
	}

	public void removeAction(final Action action) {
		final int childCount = mActionsView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View view = mActionsView.getChildAt(i);
			if (view != null) {
				final Object tag = view.getTag();
				if (tag instanceof Action && tag.equals(action)) {
					mActionsView.removeView(view);
				}
			}
		}
	}

	public int getActionCount() {
		return mActionsView.getChildCount();
	}

	private void setShowLogo(final boolean showLogo) {
		mLogoView.setImageResource(R.drawable.action_bar_icon);
		mLogoView.setVisibility(View.VISIBLE);
	}

	private void setTitle(final CharSequence title) {
		mTitleView.setText(title);
	}

	private View inflateAction(final Action action) {
		final View view = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);

		final ImageButton labelView = (ImageButton) view.findViewById(R.id.actionbar_item);
		labelView.setImageResource(action.getDrawable());

		view.setTag(action);
		view.setOnClickListener(this);
		return view;
	}

}
