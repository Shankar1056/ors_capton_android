package com.bigappcompany.ors_captain.app;

import android.app.Application;

import com.bigappcompany.ors_captain.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 29 May 2017 at 11:47 AM
 */

public class App extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
		    .setDefaultFontPath("fonts/lato-regular.ttf")
		    .setFontAttrId(R.attr.fontPath)
		    .build()
		);
	}
}
