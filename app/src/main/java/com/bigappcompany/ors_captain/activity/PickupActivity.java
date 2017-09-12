package com.bigappcompany.ors_captain.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.adapter.PickupItemAdapter;
import com.bigappcompany.ors_captain.adapter.RateCardAdapter;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.bigappcompany.ors_captain.dialog.PickupDialog;
import com.bigappcompany.ors_captain.model.PickupItemModel;
import com.bigappcompany.ors_captain.model.ScheduleModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class PickupActivity extends BaseActivity
    implements View.OnClickListener, PickupItemAdapter.OnItemClickListener, PickupDialog.OnQuantityListener, OnApiListener {
	private static final int RC_PICK = 0;
	private static final int RC_PAY = 1;
	private static final int RC_ITEM_LIST = 2;
	private static final int RC_WALLET_PAY = 4;
	
	private SlidingUpPanelLayout slidingSPL;
	private TextView totalTV;
	
	// contains all the rate card items
	private ArrayList<PickupItemModel> mPickupItems = new ArrayList<>();
	private PickupItemAdapter mAdapter;
	private ScheduleModel mSchedule;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pickup);
		
		// sliding up panel control
		slidingSPL = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		slidingSPL.setTouchEnabled(false);
		
		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);
		
		// read schedule details from parent activity
		mSchedule = (ScheduleModel) getIntent().getSerializableExtra(EXTRA_DATA);
		
		// pickup item adapter
		mAdapter = new PickupItemAdapter(this);
		for (PickupItemModel item : mSchedule.getExpectedItemList()) {
			mAdapter.addItem(item);
		}
		
		((RecyclerView) findViewById(R.id.rv_pickup_items)).setAdapter(mAdapter);
		
		// edit items
		findViewById(R.id.tv_edit).setOnClickListener(this);
		
		// total
		totalTV = (TextView) findViewById(R.id.tv_total);
		totalTV.setText(getString(R.string.format_price, mAdapter.getTotalPrice()));
		
		findViewById(R.id.tv_pay_by_wallet).setOnClickListener(this);
		findViewById(R.id.tv_pay_cash).setOnClickListener(this);
		
		// sliding panel control
		findViewById(R.id.iv_close).setOnClickListener(this);
		findViewById(R.id.iv_done).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.tv_panel_title)).setText(R.string.rate_card);
		
		// get pickup items
		ApiTask.builder(this)
		    .setUrl(ApiUrl.GET_ITEM_LIST)
		    .setProgressMessage(R.string.getting_schedule_items)
		    .setResponseListener(this)
		    .setRequestCode(RC_ITEM_LIST)
		    .exec();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_pie, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_chart) {
			slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_edit:
				Intent intent = new Intent(this, ChartActivity.class);
				intent.putExtra(EXTRA_DATA, mAdapter.getItemList());
				intent.putExtra(EXTRA_ITEM_LIST, mPickupItems);
				startActivityForResult(intent, RC_PICK);
				break;
			
			case R.id.tv_pay_by_wallet:
				if (mAdapter.getTotalPrice() > 0) {
					intent = new Intent(this, WalletPaymentActivity.class);
					intent.putExtra(EXTRA_DATA, mAdapter.getTotalPrice());
					startActivityForResult(intent, RC_WALLET_PAY);
				} else {
					Toast.makeText(this, R.string.please_add_items_and_quantities, Toast.LENGTH_SHORT).show();
				}
				break;
			
			case R.id.tv_pay_cash:
				if (mAdapter.getTotalPrice() > 0) {
					pay(false);
				} else {
					Toast.makeText(this, R.string.please_add_items_and_quantities, Toast.LENGTH_SHORT).show();
				}
				break;
			
			case R.id.iv_close:
				slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
				break;
		}
	}
	
	private void pay(final boolean isCredit) {
		new AlertDialog.Builder(this)
		    .setTitle(isCredit ? R.string.title_pay_by_wallet : R.string.title_pay_by_cash)
		    .setMessage(
			getString(isCredit ? R.string.msg_pay_by_wallet : R.string.msg_pay_by_cash, mAdapter.getTotalPrice())
		    )
		    .setPositiveButton(isCredit ? R.string.pay_by_wallet : R.string.pay_cash, new DialogInterface
			.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    ApiTask.builder(PickupActivity.this)
					.setUrl(ApiUrl.TRANSACT)
				        .setRequestBody(
					    mSchedule.toJson(mAdapter.getItemList(), isCredit ? "credit" : "cash")
				        )
					.setResponseListener(PickupActivity.this)
					.setRequestCode(RC_PAY)
					.setProgressMessage(R.string.progress_transacting)
					.exec();
			    }
		    })
		    .setNegativeButton(R.string.cancel, null)
		    .create()
		    .show();
	}
	
	@Override
	public void onItemClick(PickupItemModel item, int position) {
		PickupDialog dialog = PickupDialog.newInstance(item, position);
		dialog.show(getSupportFragmentManager(), null);
	}
	
	@Override
	public void onAdd(PickupItemModel item) {
		mAdapter.addItem(item);
		totalTV.setText(getString(R.string.format_price, mAdapter.getTotalPrice()));
	}
	
	@Override
	public void onModify(PickupItemModel item, int position) {
		mAdapter.notifyItemChanged(position);
		totalTV.setText(getString(R.string.format_price, mAdapter.getTotalPrice()));
	}
	
	@Override
	public void onDelete(PickupItemModel item, int position) {
		mAdapter.deleteItem(position);
		totalTV.setText(getString(R.string.format_price, mAdapter.getTotalPrice()));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_PICK && resultCode == RESULT_OK) {
			mAdapter.addItems((ArrayList<PickupItemModel>) data.getSerializableExtra(EXTRA_DATA));
			totalTV.setText(getString(R.string.format_price, mAdapter.getTotalPrice()));
		} else if (requestCode == RC_WALLET_PAY && resultCode == RESULT_OK) {
			pay(true);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (slidingSPL.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_ITEM_LIST:
				parseItemList(response);
				break;
			
			case RC_PAY:
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_LONG).show();
				startActivity(new Intent(this, HomeActivity.class));
				break;
		}
	}
	
	private void parseItemList(JsonObject response) {
		JsonArray data = response.getAsJsonArray("data");
		for (JsonElement element : data) {
			mPickupItems.add(new PickupItemModel((JsonObject) element));
		}
		
		RateCardAdapter adapter = new RateCardAdapter(mPickupItems);
		adapter.setSelectable(false);
		((RecyclerView) findViewById(R.id.rv_rate_card)).setAdapter(adapter);
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
