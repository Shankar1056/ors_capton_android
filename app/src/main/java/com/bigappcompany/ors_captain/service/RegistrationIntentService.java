/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bigappcompany.ors_captain.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
	
	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = {"global"};
	private static final String REGISTRATION_COMPLETE = "registration_complete";
	
	public RegistrationIntentService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		try {
			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
			    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			// [END get_token]
			sendRegistrationToServer(token);
			
			// Subscribe to topic channels
			subscribeTopics(token);
			
			// You should store a boolean that indicates whether the generated token has been
			// sent to your server. If the boolean is false, send the token to your server,
			// otherwise your server should have already received the token.
			// [END register_for_gcm]
		} catch (Exception e) {
			Log.e(TAG, "Failed to complete token refresh", e);
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, this ensures that we'll attempt the update at a later time.
		}
		// Notify UI that registration has completed, so the progress indicator can be hidden.
		Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
	}
	
	/**
	 * Persist registration to third-party servers.
	 * <p/>
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private void sendRegistrationToServer(String token) {
		JsonObject object = new JsonObject();
		object.addProperty("token", token);
		
		ApiTask.builder(this)
		    .setUrl(ApiUrl.GCM_TOKEN_UPDATE)
		    .setRequestBody(object)
		    .exec();
	}
	
	private void subscribeTopics(String token) throws IOException {
		GcmPubSub pubSub = GcmPubSub.getInstance(this);
		for (String topic : TOPICS) {
			pubSub.subscribe(token, "/topics/" + topic, null);
		}
	}
}