package com.bigappcompany.ors_captain.listener;

import com.bigappcompany.ors_captain.model.AgentModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 Jun 2017 at 11:38 AM
 */

public interface OnAuthListener {
	/**
	 * Auth step callback listener
	 *
	 * @param captain data
	 * @param tag     fragment identifier
	 */
	void onAuth(Object captain, String tag);
	
	/**
	 * Resend OTP trigger
	 */
	void onResendOTP();
	
	/**
	 * Sign up action trigger
	 */
	void onSignUp();
	
	/**
	 * Sign in action trigger
	 */
	void onSignIn();
	
	/**
	 * Callback listener for signing up agent
	 *
	 * @param agent agent details
	 */
	void onSignUp(AgentModel agent);
}
