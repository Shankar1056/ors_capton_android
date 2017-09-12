package com.bigappcompany.ors_captain.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.bigappcompany.ors_captain.model.RechargeModel;
import com.google.gson.JsonObject;
import com.razorpay.PaymentResultListener;

@SuppressWarnings("ConstantConditions")
public class WalletActivity extends BaseActivity implements View.OnClickListener, OnApiListener, PaymentResultListener {
	private static final int RC_PAYMENT_ORDER = 1;
	private static final int RC_PAYMENT = 2;
	private static final int RC_PAYMENT_CANCEL = 4;
	private static final int RC_BALANCE = 0;
	
	private AppCompatButton balanceBtn;
	private AppCompatEditText rechargeET;
	private RechargeModel mRecharge;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		
		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);
		
		findViewById(R.id.btn_recharge).setOnClickListener(this);
		balanceBtn = (AppCompatButton) findViewById(R.id.btn_balance);
		rechargeET = (AppCompatEditText) findViewById(R.id.et_recharge);
		rechargeET.setText(R.string.rs_600);
		
		findViewById(R.id.tv_speed_0).setOnClickListener(this);
		findViewById(R.id.tv_speed_1).setOnClickListener(this);
		findViewById(R.id.tv_speed_2).setOnClickListener(this);
		findViewById(R.id.tv_speed_3).setOnClickListener(this);
		
		mRecharge = new RechargeModel();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// get wallet balance
		ApiTask.builder(this)
		    .setUrl(ApiUrl.WALLET_BALANCE)
		    .setResponseListener(this)
		    .setProgressMessage(R.string.progress_fetching_wallet)
		    .setRequestCode(RC_BALANCE)
		    .exec();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_recharge:
				try {
					String amount = rechargeET.getText().toString().trim();
					amount = amount.replace("\u20B9", "").replace(" ", "");
					double amt = Double.parseDouble(amount);
					
					if (amt > 0) {
						mRecharge.setAmount(amt);
						
						// get the order id from server
						ApiTask.builder(this)
						    .setUrl(ApiUrl.GET_PAYMENT_ORDER)
						    .setRequestBody(mRecharge.toJson())
						    .setResponseListener(this)
						    .setRequestCode(RC_PAYMENT_ORDER)
						    .setProgressMessage(R.string.creating_order)
						    .exec();
					} else {
						throw new NumberFormatException("Invalid amount");
					}
				} catch (NumberFormatException e) {
					Toast.makeText(this, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
					rechargeET.setError(getString(R.string.invalid_amount));
				}
				break;
			
			case R.id.tv_speed_0:
			case R.id.tv_speed_1:
			case R.id.tv_speed_2:
			case R.id.tv_speed_3:
				rechargeET.setText(getString(R.string.format_price, Double.parseDouble(v.getTag().toString())));
				rechargeET.setError(null);
				break;
		}
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_BALANCE:
				double balance = response.get("data").getAsDouble();
				balanceBtn.setText(getString(R.string.format_price, balance));
				break;
			
			case RC_PAYMENT_ORDER:
				if (response.get("status").getAsInt() == 0) {
					mRecharge.setIds(response.getAsJsonObject("data"));
					makePayment(mRecharge.getAmount(), mRecharge.getOrderId());
				}
				break;
			
			case RC_PAYMENT:
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				if (response.get("status").getAsInt() == 0) {
					onSuccess(response, RC_BALANCE, savedData);
				}
				break;
		}
		
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onPaymentSuccess(String s) {
		mRecharge.setTransactionId(s);
		
		// update the recharge with transaction
		ApiTask.builder(this)
		    .setUrl(ApiUrl.MAKE_PAYMENT)
		    .setRequestBody(mRecharge.toJson())
		    .setResponseListener(this)
		    .setRequestCode(RC_PAYMENT)
		    .setProgressMessage(R.string.making_payment)
		    .exec();
	}
	
	@Override
	public void onPaymentError(int i, String s) {
		ApiTask.builder(this)
		    .setUrl(ApiUrl.CANCEL_PAYMENT)
		    .setRequestBody(mRecharge.toJson())
		    .setResponseListener(this)
		    .setRequestCode(RC_PAYMENT_CANCEL)
		    .setProgressMessage(R.string.Cancelling_payment)
		    .exec();
	}
}
