package com.bigappcompany.ors_captain.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 2017-03-22 at 6:39 PM
 */

public class ViewPagerCustomDuration extends ViewPager {
	
	private ScrollerCustomDuration mScroller = null;
	
	public ViewPagerCustomDuration(Context context) {
		super(context);
		postInitViewPager();
	}
	
	/**
	 * Override the Scroller instance with our own class so we can change the
	 * duration
	 */
	private void postInitViewPager() {
		try {
			Field scroller = ViewPager.class.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);
			
			mScroller = new ScrollerCustomDuration(getContext(),
			    (Interpolator) interpolator.get(null));
			scroller.set(this, mScroller);
		} catch (Exception e) {
		}
	}
	
	public ViewPagerCustomDuration(Context context, AttributeSet attrs) {
		super(context, attrs);
		postInitViewPager();
	}
	
	/**
	 * Set the factor by which the duration will change
	 */
	public void setScrollDurationFactor(double scrollFactor) {
		mScroller.setScrollDurationFactor(scrollFactor);
	}
	
}