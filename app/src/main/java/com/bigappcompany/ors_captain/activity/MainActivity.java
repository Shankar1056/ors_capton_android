package com.bigappcompany.ors_captain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.service.RegistrationIntentService;
import com.bigappcompany.ors_captain.util.Preference;

public class MainActivity extends BaseActivity {
	
	private static final int RC_AUTH = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// if the user is logged in, navigate to HomeActivity
				// login otherwise
				if (new Preference(MainActivity.this).isLoggedIn()) {
					startActivity(new Intent(MainActivity.this, HomeActivity.class));
					finish();
				} else {
					startActivityForResult(new Intent(MainActivity.this, AuthActivity.class), RC_AUTH);
				}
			}
		}, 2000);
		
		getWindow().setFlags(
		    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
		    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
		);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// navigate to HomeActivity if the user was able to login successfully.
		if (requestCode == RC_AUTH && resultCode == RESULT_OK) {
			// register/update the gcm token
			startService(new Intent(this, RegistrationIntentService.class));
			
			// navigate to home activity
			startActivity(new Intent(this, HomeActivity.class));
		}
		
		finish();
	}
}
