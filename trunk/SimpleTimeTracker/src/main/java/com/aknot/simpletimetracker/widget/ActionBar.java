package com.aknot.simpletimetracker.widget;

import java.util.LinkedList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aknot.simpletimetracker.R;

/**
 * 
 * @author aknot
 * 
 */
public class ActionBar extends RelativeLayout implements OnClickListener {

	private final LinearLayout mActionsView;
	private final LayoutInflater mInflater;
	private final RelativeLayout mBarView;
	// private final RelativeLayout mHomeLayout;
	private final ImageView mLogoView;
	private final TextView mTitleView;
	// private final ImageButton mHomeBtn;
	private final ProgressBar mProgress;

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
		addView(mBarView);

		mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
		// mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
		// mHomeBtn = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);

		mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
		mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

		mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBar);
		CharSequence title = a.getString(R.styleable.ActionBar_title);
		if (title != null) {
			setTitle(title);
		}
		a.recycle();
	}

	public void setHomeLogo(int resId) {
		mLogoView.setImageResource(resId);
		mLogoView.setVisibility(View.VISIBLE);
	}

	public void setTitle(CharSequence title) {
		mTitleView.setText(title);
	}

	public void setTitle(int resid) {
		mTitleView.setText(resid);
	}

	public void setProgressBarVisibility(int visibility) {
		mProgress.setVisibility(visibility);
	}

	public int getProgressBarVisibility() {
		return mProgress.getVisibility();
	}

	public void setOnTitleClickListener(OnClickListener listener) {
		mTitleView.setOnClickListener(listener);
	}

	@Override
	public void onClick(View view) {
		final Object tag = view.getTag();
		if (tag instanceof Action) {
			final Action action = (Action) tag;
			action.performAction(view);
		}
	}

	public void addActions(ActionList actionList) {
		int actions = actionList.size();
		for (int i = 0; i < actions; i++) {
			addAction(actionList.get(i));
		}
	}

	public void addAction(Action action) {
		final int index = mActionsView.getChildCount();
		addAction(action, index);
	}

	public void addAction(Action action, int index) {
		mActionsView.addView(inflateAction(action), index);
	}

	public void removeAllActions() {
		mActionsView.removeAllViews();
	}

	public void removeActionAt(int index) {
		mActionsView.removeViewAt(index);
	}

	public void removeAction(Action action) {
		int childCount = mActionsView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = mActionsView.getChildAt(i);
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

	private View inflateAction(Action action) {
		View view = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);

		ImageButton labelView = (ImageButton) view.findViewById(R.id.actionbar_item);
		labelView.setImageResource(action.getDrawable());

		view.setTag(action);
		view.setOnClickListener(this);
		return view;
	}

	/**
	 * A {@link LinkedList} that holds a list of {@link Action}s.
	 */
	public static class ActionList extends LinkedList<Action> {
	}

	/**
	 * Definition of an action that could be performed, along with a icon to show.
	 */
	public interface Action {
		public int getDrawable();

		public void performAction(View view);
	}

	public static abstract class AbstractAction implements Action {
		final private int mDrawable;

		public AbstractAction(int drawable) {
			mDrawable = drawable;
		}

		@Override
		public int getDrawable() {
			return mDrawable;
		}
	}

	public static class IntentAction extends AbstractAction {
		private final Context mContext;
		private final Intent mIntent;

		public IntentAction(Context context, Intent intent, int drawable) {
			super(drawable);
			mContext = context;
			mIntent = intent;
		}

		@Override
		public void performAction(View view) {
			try {
				mContext.startActivity(mIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext, "Activity Not Found", Toast.LENGTH_SHORT).show();
			}
		}
	}

}