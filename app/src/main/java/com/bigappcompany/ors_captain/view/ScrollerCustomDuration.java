package com.bigappcompany.ors_captain.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 2017-02-16 at 6:19 PM
 * @signed_off_by Samuel Robert <sam@spotsoon.com>
 */

public class ScrollerCustomDuration extends Scroller {
	
	private double mScrollFactor = 1;
	
	public ScrollerCustomDuration(Context context) {
		super(context);
	}
	
	public ScrollerCustomDuration(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}
	
	@SuppressLint("NewApi")
	public ScrollerCustomDuration(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}
	
	/**
	 * Set the factor by which the duration will change
	 */
	public void setScrollDurationFactor(double scrollFactor) {
		mScrollFactor = scrollFactor;
	}
	
	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, (int) (duration * mScrollFactor));
	}
	
}