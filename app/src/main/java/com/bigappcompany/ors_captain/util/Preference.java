package com.bigappcompany.ors_captain.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 25 Mar 2017 at 5:22 PM
 */

public class Preference {
	private static final String PREF_LOGGED_IN = "pref_logged_in";
	private static final String PREF_NAME = "pref_name";
	private static final String PREF_EMAIL = "pref_email";
	private static final String PREF_AVATAR = "pref_avatar";
	private static final String PREF_MOBILE = "pref_mobile";
	private static final String PREF_CITY = "pref_city";
	private static final String PREF_SESSION_KEY = "pref_session_key";
	private static final String PREF_LAST_UPDATE_CHECKED_TIME = "pref_last_update_checked_time";
	
	private SharedPreferences mPref;
	
	public Preference(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean isLoggedIn() {
		return mPref.getBoolean(PREF_LOGGED_IN, false);
	}
	
	public void setLoggedIn(boolean isLoggedIn) {
		mPref.edit().putBoolean(PREF_LOGGED_IN, isLoggedIn).apply();
	}
	
	public String getEmail() {
		return mPref.getString(PREF_EMAIL, null);
	}
	
	public void setEmail(String email) {
		mPref.edit().putString(PREF_EMAIL, email).apply();
	}
	
	public String getMobile() {
		return mPref.getString(PREF_MOBILE, null);
	}
	
	public void setMobile(String mobile) {
		mPref.edit().putString(PREF_MOBILE, mobile).apply();
	}
	
	public String getName() {
		return mPref.getString(PREF_NAME, "");
	}
	
	public void setName(String name) {
		mPref.edit().putString(PREF_NAME, name).apply();
	}
	
	public String getAvatar() {
		return mPref.getString(PREF_AVATAR, "");
	}
	
	public void setAvatar(String avatar) {
		mPref.edit().putString(PREF_AVATAR, avatar).apply();
	}
	
	public String getCity() {
		return mPref.getString(PREF_CITY, "");
	}
	
	public void setCity(String city) {
		mPref.edit().putString(PREF_CITY, city).apply();
	}
	
	public String getSessionKey() {
		return mPref.getString(PREF_SESSION_KEY, null);
	}
	
	public void setSessionId(String sessionKey) {
		mPref.edit().putString(PREF_SESSION_KEY, sessionKey).apply();
	}
	
	public boolean isTimeToCheckForUpdate() {
		long lastUpdate = mPref.getLong(PREF_LAST_UPDATE_CHECKED_TIME, 0);
		long now = Calendar.getInstance().getTimeInMillis();
		
		return (now - lastUpdate) >= 3600000;
	}
	
	public void setLastUpdateCheckedTimeAsNow() {
		mPref.edit()
		    .putLong(PREF_LAST_UPDATE_CHECKED_TIME, Calendar.getInstance().getTimeInMillis())
		    .apply();
	}
}
