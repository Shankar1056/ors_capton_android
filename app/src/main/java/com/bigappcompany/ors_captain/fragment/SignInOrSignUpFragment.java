package com.bigappcompany.ors_captain.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.adapter.ViewPagerAdapter;
import com.bigappcompany.ors_captain.listener.OnAuthListener;
import com.bigappcompany.ors_captain.view.ViewPagerCustomDuration;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 14 Jun 2017 at 7:19 PM
 */

public class SignInOrSignUpFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = "SignInOrSignUpFragment";
	private ViewPagerHelper mSliderHelper;
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_in_or_sign_up, container, false);
		
		ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
		ViewPagerCustomDuration sliderVP = (ViewPagerCustomDuration) view.findViewById(R.id.vp_slider);
		sliderVP.setAdapter(adapter);
		((TabLayout) view.findViewById(R.id.tab_layout)).setupWithViewPager(sliderVP);
		
		adapter.addFragment(
		    SignUpSliderFragment.newInstance(
			R.string.sign_up_less_than,
			R.string.hint_sign_up,
			R.drawable.art_otp_verify_200dp
		    ),
		    null
		);
		
		adapter.addFragment(
		    SignUpSliderFragment.newInstance(
			R.string.talk_to_our_support_team,
			R.string.hint_talk_to_support,
			R.drawable.art_support_team_268dp
		    ),
		    null
		);
		
		adapter.addFragment(
		    SignUpSliderFragment.newInstance(
			R.string.get_pick_up_request,
			R.string.hint_pickup_request,
			R.drawable.art_connect_pickup_268dp
		    ),
		    null
		);
		
		view.findViewById(R.id.btn_sign_in).setOnClickListener(this);
		view.findViewById(R.id.btn_sign_up).setOnClickListener(this);
		
		mSliderHelper = new ViewPagerHelper(sliderVP);
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mSliderHelper.startCallback();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		mSliderHelper.stopCallback();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_sign_in:
				((OnAuthListener) getActivity()).onSignIn();
				break;
			
			case R.id.btn_sign_up:
				((OnAuthListener) getActivity()).onSignUp();
				break;
		}
	}
	
	private class ViewPagerHelper implements Runnable, ViewPager.OnPageChangeListener {
		private ViewPagerCustomDuration mViewPager;
		private ViewPagerAdapter mAdapter;
		private Handler mHandler;
		private boolean isAscending = true, userScrollChange = false;
		private int prevPos = 0;
		private int previousState = ViewPager.SCROLL_STATE_IDLE;
		
		ViewPagerHelper(ViewPagerCustomDuration viewPager) {
			mViewPager = viewPager;
			mViewPager.addOnPageChangeListener(this);
			mAdapter = (ViewPagerAdapter) viewPager.getAdapter();
			mHandler = new Handler();
		}
		
		void startCallback() {
			mHandler.postDelayed(this, 3000);
		}
		
		void stopCallback() {
			mHandler.removeCallbacks(this);
		}
		
		/**
		 * throws divided by zero {@link ArithmeticException} if the #getChildCount() returns 0
		 */
		@Override
		public void run() {
			try {
				int nextPos;
				
				if (isAscending) {
					nextPos = (mViewPager.getCurrentItem() + 1) % mAdapter.getCount();
					isAscending = nextPos < mAdapter.getCount() - 1;
				} else {
					nextPos = (mViewPager.getCurrentItem() - 1) % mAdapter.getCount();
					isAscending = nextPos < 1;
				}
				
				mViewPager.setCurrentItem(nextPos, true);
				mHandler.postDelayed(this, 5000);
			} catch (ArithmeticException e) {
				Log.w(TAG, e.getMessage(), e);
			}
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
		}
		
		@Override
		public void onPageSelected(int position) {
			if (userScrollChange) {
				isAscending = prevPos < position && position < mAdapter.getCount() - 1 && position > 0;
			}
			
			prevPos = position;
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			if (previousState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_SETTLING) {
				userScrollChange = true;
			} else if (previousState == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE) {
				userScrollChange = false;
			}
			
			previousState = state;
		}
	}
}
