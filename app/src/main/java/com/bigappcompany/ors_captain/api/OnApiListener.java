package com.bigappcompany.ors_captain.api;

import android.os.Bundle;

import com.google.gson.JsonObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 Jun 2017 at 2:19 PM
 */

public interface OnApiListener {
	void onSuccess(JsonObject response, int requestCode, Bundle savedData);
	
	void onFailure(int requestCode, Bundle savedData);
}
