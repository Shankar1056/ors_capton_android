package com.bigappcompany.ors_captain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.google.gson.JsonObject;

@SuppressWarnings("ConstantConditions")
public class WalletPaymentActivity extends BaseActivity implements OnApiListener, View.OnClickListener {
	private double mWalletBalance = 0;
	private AppCompatButton balanceBtn;
	private double mPayableAmount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet_payment);
		
		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);
		
		mPayableAmount = getIntent().getDoubleExtra(EXTRA_DATA, 0);
		((TextView) findViewById(R.id.tv_payable_amount))
		    .setText(getString(R.string.format_price, mPayableAmount));
		
		balanceBtn = (AppCompatButton) findViewById(R.id.btn_balance);
		findViewById(R.id.btn_pay).setOnClickListener(this);
		findViewById(R.id.tv_recharge_now).setOnClickListener(this);
		
		// get wallet balance
		ApiTask.builder(this)
		    .setUrl(ApiUrl.WALLET_BALANCE)
		    .setResponseListener(this)
		    .setProgressMessage(R.string.progress_fetching_wallet)
		    .exec();
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		// read balance from the response
		mWalletBalance = response.get("data").getAsDouble();
		balanceBtn.setText(getString(R.string.format_price, mWalletBalance));
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_pay:
				setResult(RESULT_OK);
				finish();
				break;
			
			case R.id.tv_recharge_now:
				Intent intent = new Intent(this, WalletActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	/*private void insufficientBalance() {
		final AlertDialog dialog = new AlertDialog.Builder(this)
		    .setView(R.layout.dialog_insuffient_balance)
		    .create();
		dialog.show();
		dialog.findViewById(R.id.tv_recharge_now).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	}*/
}
