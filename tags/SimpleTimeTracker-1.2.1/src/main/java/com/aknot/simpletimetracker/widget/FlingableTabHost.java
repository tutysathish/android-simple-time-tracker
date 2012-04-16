package com.aknot.simpletimetracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

import com.aknot.simpletimetracker.R;

/**
 * 
 * This class extend TabHost to enable fling on tabs.
 * 
 * @author Aknot
 * 
 */
public class FlingableTabHost extends TabHost {

	private static final int MAX_VELOCITY = 60;

	private final GestureDetector mGestureDetector;

	private final Animation mRightInAnimation;
	private final Animation mRightOutAnimation;
	private final Animation mLeftInAnimation;
	private final Animation mLeftOutAnimation;

	public FlingableTabHost(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		mRightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_in);
		mRightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_right_out);
		mLeftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_in);
		mLeftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left_out);

		mGestureDetector = new GestureDetector(new TabGestureDetector());
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent event) {
		// final boolean result = super.onInterceptTouchEvent(event);
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}
		return false;
	}

	private class TabGestureDetector extends SimpleOnGestureListener {

		@Override
		public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {

			final int tabCount = getTabWidget().getTabCount();
			final int currentTab = getCurrentTab();

			final int dx = (int) (e2.getX() - e1.getX());

			if (Math.abs(dx) > MAX_VELOCITY && Math.abs(velocityX) > Math.abs(velocityY)) {
				final boolean right = velocityX < 0;
				final int newTab = constrain(currentTab + (right ? 1 : -1), 0, tabCount - 1);

				if (newTab != currentTab) {
					final View currentView = getCurrentView();
					setCurrentTab(newTab);
					final View newView = getCurrentView();

					newView.startAnimation(!right ? mRightInAnimation : mLeftInAnimation);
					currentView.startAnimation(!right ? mRightOutAnimation : mLeftOutAnimation);
				}
				return true;
			}
			return false;
		}

		/**
		 * Equivalent to Math.max(low, Math.min(high, amount));
		 */
		private int constrain(final int amount, final int low, final int high) {
			return amount < low ? low : amount > high ? high : amount;
		}
	}

	public GestureDetector getGesture() {
		return mGestureDetector;
	}

}
