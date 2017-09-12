package com.bigappcompany.ors_captain.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bigappcompany.ors_captain.util.Preference;
import com.razorpay.Checkout;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 29 May 2017 at 11:45 AM
 */

public abstract class BaseActivity extends AppCompatActivity {
	public static final String EXTRA_DATA = "extra_data";
	static final String EXTRA_ITEM_LIST = "extra_item_list";
	static final String EXTRA_LAT = "extra_lat";
	static final String EXTRA_LONG = "extra_long";
	private static final String TAG = "BaseActivity";
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	protected void makePayment(double amount, String orderId) {
		final Checkout co = new Checkout();
		final Preference preference = new Preference(this);
		
		try {
			JSONObject options = new JSONObject();
			options.put("name", "ORS");
			options.put("description", "ORS Wallet recharge");
			options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
			options.put("currency", "INR");
			options.put("amount", amount * 100);
			options.put("order_id", orderId);
			
			JSONObject preFill = new JSONObject();
			preFill.put("email", preference.getEmail());
			preFill.put("contact", preference.getMobile());
			
			options.put("prefill", preFill);
			
			co.open(this, options);
		} catch (Exception e) {
			Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage(), e);
		}
		
	}
}
