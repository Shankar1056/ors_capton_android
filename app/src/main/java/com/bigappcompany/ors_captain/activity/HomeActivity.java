package com.bigappcompany.ors_captain.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.adapter.ViewPagerAdapter;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.bigappcompany.ors_captain.fragment.HomeFragment;
import com.bigappcompany.ors_captain.util.Preference;
import com.google.gson.JsonObject;

public class HomeActivity extends BaseActivity implements View.OnClickListener, OnApiListener {
	private static final int RC_BALANCE = 1;
	private static final int RC_UPDATE = 2;
	
	private boolean isBackPressed = false;
	
	private TextView walletTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(HomeFragment.newInstance("pending"), "New Leads");
		adapter.addFragment(HomeFragment.newInstance("accepted"), "Accepted");
		adapter.addFragment(HomeFragment.newInstance("ontheway"), "On My Way");
		adapter.addFragment(HomeFragment.newInstance("completed"), "Completed");
		
		ViewPager homeVP = (ViewPager) findViewById(R.id.vp_home);
		homeVP.setAdapter(adapter);
		
		((TabLayout) findViewById(R.id.tl_home)).setupWithViewPager(homeVP);
		
		// check for app updates
		checkForAppUpdate();
	}
	
	private void checkForAppUpdate() {
		if (new Preference(this).isTimeToCheckForUpdate()) {
			ApiTask.builder(this)
			    .setUrl(ApiUrl.CHECK_FOR_UPDATES)
			    .setResponseListener(this)
			    .setRequestCode(RC_UPDATE)
			    .exec();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		
		FrameLayout badgeLayout = (FrameLayout) menu.findItem(R.id.action_wallet).getActionView();
		badgeLayout.setOnClickListener(this);
		walletTV = (TextView) badgeLayout.findViewById(R.id.tv_wallet_badge);
		walletTV.setText(String.valueOf(0));
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (!isBackPressed) {
			Toast.makeText(this, R.string.tap_to_exit, Toast.LENGTH_SHORT).show();
			isBackPressed = true;
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isBackPressed = false;
				}
			}, 3000);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// get wallet balance
		ApiTask.builder(this)
		    .setUrl(ApiUrl.WALLET_BALANCE)
		    .setRequestCode(RC_BALANCE)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onClick(View v) {
		startActivity(new Intent(this, WalletActivity.class));
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		if (!isFinishing()) {
			switch (requestCode) {
				case RC_BALANCE:
					double balance = response.get("data").getAsDouble();
					walletTV.setText(String.valueOf((int) balance));
					break;
				
				case RC_UPDATE:
					new Preference(this).setLastUpdateCheckedTimeAsNow();
					if (response.get("status").getAsInt() == 404) {
						updateApp();
					}
					break;
			}
		}
	}
	
	private void updateApp() {
		new AlertDialog.Builder(this)
		    .setTitle(R.string.title_update_app)
		    .setMessage(R.string.message_optional_update)
		    .setNegativeButton(R.string.cancel, null)
		    .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    Intent goToMarket = new Intent(Intent.ACTION_VIEW)
					.setData(Uri.parse("market://details?id=" + getPackageName()));
				    startActivity(goToMarket);
			    }
		    })
		    .create()
		    .show();
	}
	
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
