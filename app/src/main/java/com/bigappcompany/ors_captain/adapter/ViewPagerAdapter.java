package com.bigappcompany.ors_captain.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 29 May 2017 at 11:17 AM
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
	private final ArrayList<String> mTitleList = new ArrayList<>();
	
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}
	
	@Override
	public int getCount() {
		return mFragmentList.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		try {
			return mTitleList.get(position);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public void addFragment(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mTitleList.add(title);
		notifyDataSetChanged();
	}
}
