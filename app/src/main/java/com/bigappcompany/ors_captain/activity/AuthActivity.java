package com.bigappcompany.ors_captain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.bigappcompany.ors_captain.fragment.MobileFragment;
import com.bigappcompany.ors_captain.fragment.OTPFragment;
import com.bigappcompany.ors_captain.fragment.SignInOrSignUpFragment;
import com.bigappcompany.ors_captain.fragment.SignUpFragment;
import com.bigappcompany.ors_captain.listener.OnAuthListener;
import com.bigappcompany.ors_captain.model.AgentModel;
import com.bigappcompany.ors_captain.model.CaptainModel;
import com.bigappcompany.ors_captain.util.Preference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

public class AuthActivity extends BaseActivity implements OnAuthListener, OnSmsCatchListener<String>, OnApiListener {
	private static final String FRAG_SIGN_IN_OR_SIGN_UP = "frag_sign_in_sign_up";
	private static final String FRAG_MOBILE = "frag_mobile";
	private static final String FRAG_OTP = "frag_otp";
	private static final String FRAG_SIGN_UP = "frag_sign_up";
	
	private static final int RC_GENERATE_OTP = 0;
	private static final int RC_VALIDATE_OTP = 1;
	private static final int RC_REGISTER = 2;
	
	private CaptainModel mCaptain;
	private SmsVerifyCatcher mOTPCatcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		
		// new captain model
		mCaptain = new CaptainModel();
		mOTPCatcher = new SmsVerifyCatcher(this, this);
		
		// sign in or sign up
		getSupportFragmentManager().beginTransaction()
		    .replace(R.id.fl_container, new SignInOrSignUpFragment(), FRAG_SIGN_IN_OR_SIGN_UP)
		    .commit();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mOTPCatcher.onStart();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mOTPCatcher.onStop();
	}
	
	@Override
	public void onSmsCatch(String message) {
		String otp = message.split(" ")[0];
		
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_container);
		if (fragment != null && fragment instanceof OTPFragment) {
			onAuth(otp, FRAG_OTP);
		}
	}
	
	@Override
	public void onAuth(Object object, String tag) {
		switch (tag) {
			case FRAG_MOBILE:
				// set mobile number
				mCaptain.setPhone(String.valueOf(object));
				
				// Generate OTP
				ApiTask.builder(this)
				    .setUrl(ApiUrl.GENERATE_OTP)
				    .setRequestBody(new Gson().toJson(mCaptain))
				    .setProgressMessage(R.string.progress_authenticating)
				    .setRequestCode(RC_GENERATE_OTP)
				    .setResponseListener(this)
				    .exec();
				break;
			
			case FRAG_OTP:
				// set OTP
				mCaptain.setOTP(String.valueOf(object));
				
				// Generate OTP
				ApiTask.builder(this)
				    .setUrl(ApiUrl.SUBMIT_OTP)
				    .setRequestBody(new Gson().toJson(mCaptain))
				    .setProgressMessage(R.string.progress_authenticating)
				    .setRequestCode(RC_VALIDATE_OTP)
				    .setResponseListener(this)
				    .exec();
				break;
		}
	}
	
	@Override
	public void onResendOTP() {
		ApiTask.builder(this)
		    .setUrl(ApiUrl.GENERATE_OTP)
		    .setRequestBody(new Gson().toJson(mCaptain))
		    .setProgressMessage(R.string.progress_authenticating)
		    .exec();
	}
	
	@Override
	public void onSignUp() {
		// show sign up fragment
		getSupportFragmentManager().beginTransaction()
		    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
		    .add(R.id.fl_container, new SignUpFragment(), FRAG_SIGN_UP)
		    .addToBackStack(null)
		    .commit();
	}
	
	@Override
	public void onSignIn() {
		// show sign in fragment
		getSupportFragmentManager().beginTransaction()
		    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
		    .add(R.id.fl_container, new MobileFragment(), FRAG_MOBILE)
		    .addToBackStack(null)
		    .commit();
	}
	
	@Override
	public void onSignUp(AgentModel agent) {
		ApiTask.builder(this)
		    .setUrl(ApiUrl.REGISTER)
		    .setRequestBody(agent.toJson())
		    .setProgressMessage(R.string.progress_registering)
		    .setRequestCode(RC_REGISTER)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_GENERATE_OTP:
				if (response.get("status").getAsInt() == 0) {
					// load OTP fragment
					getSupportFragmentManager().beginTransaction()
					    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
					    .add(R.id.fl_container, new OTPFragment(), FRAG_OTP)
					    .addToBackStack(null)
					    .commit();
				} else {
					Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				}
				break;
			
			case RC_VALIDATE_OTP:
				if (response.get("status").getAsInt() == 0) {
					// success, store session id and profile info
					Preference preference = new Preference(this);
					JsonObject data = response.getAsJsonObject("data");
					preference.setMobile(data.get("agent_phone").getAsString());
					preference.setEmail(data.get("agent_email").getAsString());
					preference.setName(data.get("captain_name").getAsString());
					preference.setSessionId(data.get("session_id").getAsString());
					preference.setLoggedIn(true);
					
					// finish activity with success status
					setResult(RESULT_OK);
					finish();
				} else {
					// error, toast messages
					Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				}
				break;
			
			case RC_REGISTER:
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_LONG).show();
				finish();
				break;
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		mOTPCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
