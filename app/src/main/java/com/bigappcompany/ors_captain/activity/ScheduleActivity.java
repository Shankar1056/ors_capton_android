package com.bigappcompany.ors_captain.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.adapter.ExpectedPickupItemAdapter;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.bigappcompany.ors_captain.model.PickupItemModel;
import com.bigappcompany.ors_captain.model.ScheduleModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@SuppressWarnings("ConstantConditions")
public class ScheduleActivity extends BaseActivity implements OnApiListener, View.OnClickListener {
	private static final int RC_DETAILS = 0;
	private static final int RC_ASSIGN_TO_ME = 1;
	private static final int RC_START_PICKUP = 2;
	
	private FloatingActionButton callFAB;
	private AppCompatButton assignBtn;
	private ExpectedPickupItemAdapter mAdapter;
	private ScheduleModel mSchedule;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		
		// store the schedule details from the parent activity
		mSchedule = (ScheduleModel) getIntent().getSerializableExtra(EXTRA_DATA);
		
		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(mSchedule.getName());
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);
		
		// pickup items recycler view
		mAdapter = new ExpectedPickupItemAdapter();
		RecyclerView pickupItems = (RecyclerView) findViewById(R.id.rv_pickup_items);
		pickupItems.setAdapter(mAdapter);
		pickupItems.setNestedScrollingEnabled(false);
		
		// controls
		callFAB = (FloatingActionButton) findViewById(R.id.fab_call);
		callFAB.setOnClickListener(this);
		assignBtn = (AppCompatButton) findViewById(R.id.btn_self_assign);
		assignBtn.setOnClickListener(this);
		
		// schedule status
		changeStatus();
		
		// address, date and time
		findViewById(R.id.tv_label_address).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_address)).setText(mSchedule.getAddress());
		((TextView) findViewById(R.id.tv_date)).setText(mSchedule.getDate());
		((TextView) findViewById(R.id.tv_time)).setText(mSchedule.getStartTime() + " - " + mSchedule.getEndTime());
		
		// get pick up item details
		ApiTask.builder(this)
		    .setUrl(ApiUrl.GET_SCHEDULE_BY_ID)
		    .setRequestBody(mSchedule.toJson())
		    .setRequestCode(RC_DETAILS)
		    .setProgressMessage(R.string.progress_schedule_details)
		    .setResponseListener(this)
		    .exec();
	}
	
	private void changeStatus() {
		TextView statusTV = (TextView) findViewById(R.id.tv_schedule_status);
		statusTV.setText(mSchedule.getScheduleStatus());
		statusTV.setTextColor(
		    ContextCompat.getColor(
			this,
			getResources().getIdentifier(mSchedule.getScheduleStatus(), "color", getPackageName())
		    )
		);
		Drawable statusIcon = ContextCompat.getDrawable(
		    this,
		    getResources().getIdentifier(mSchedule.getScheduleStatus(), "drawable", getPackageName())
		);
		statusTV.setCompoundDrawablesWithIntrinsicBounds(statusIcon, null, null, null);
		
		if (mSchedule.isPending() || mSchedule.isCancelled() || mSchedule.isCompleted()) {
			callFAB.setVisibility(View.GONE);
		} else {
			callFAB.setVisibility(View.VISIBLE);
		}
		
		if (mSchedule.isPending()) {
			assignBtn.setText(R.string.assign_to_me);
		} else if (mSchedule.isAccepted()) {
			assignBtn.setText(R.string.start_process);
		} else if (mSchedule.isOnTheWay()) {
			assignBtn.setText(R.string.start_pickup);
		} else if (mSchedule.isCompleted()) {
			assignBtn.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		switch (requestCode) {
			case RC_DETAILS:
				JsonArray items = response.getAsJsonObject("data").getAsJsonArray("selected_items");
				for (JsonElement item : items) {
					mAdapter.addItem(new PickupItemModel((JsonObject) item));
				}
				break;
			
			case RC_ASSIGN_TO_ME:
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				if (response.get("status").getAsInt() == 0) {
					mSchedule.setScheduleStatus(ScheduleModel.STATUS_ACCEPTED);
					changeStatus();
					
					mSchedule.setCustomerNumber(
					    ((JsonObject) response.get("data")).get("customer_phone").toString()
					);
					
					callFAB.setVisibility(View.VISIBLE);
				}
				break;
			
			case RC_START_PICKUP:
				Toast.makeText(this, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
				if (response.get("status").getAsInt() == 0) {
					mSchedule.setScheduleStatus(ScheduleModel.STATUS_ON_THE_WAY);
					changeStatus();
					callFAB.setVisibility(View.VISIBLE);
				}
				break;
			
		}
		
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_label_address:
				Intent mapIntent = new Intent(this, MapsActivity.class);
				mapIntent.putExtra(EXTRA_DATA, mSchedule);
				startActivity(mapIntent);
				break;
				
			case R.id.btn_self_assign:
				if (mSchedule.isPending()) {
					selfAssign();
				} else if (mSchedule.isAccepted()) {
					startProcess();
				} else if (mSchedule.isOnTheWay()) {
					Intent intent = new Intent(this, PickupActivity.class);
					mSchedule.setExpectedItemList(mAdapter.getItemList());
					intent.putExtra(EXTRA_DATA, mSchedule);
					startActivity(intent);
				}
				break;
			
			case R.id.fab_call:
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + mSchedule.getCustomerNumber()));
				startActivity(intent);
				break;
		}
		
	}
	
	private void startProcess() {
		new AlertDialog.Builder(this)
		    .setTitle(R.string.title_start_pick)
		    .setMessage(R.string.start_pickup_confirm)
		    .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    ApiTask.builder(ScheduleActivity.this)
					.setUrl(ApiUrl.START_PROCESS)
					.setRequestCode(RC_START_PICKUP)
					.setRequestBody(mSchedule.toJson())
					.setProgressMessage(R.string.starting_pickup)
					.setResponseListener(ScheduleActivity.this)
					.exec();
			    }
		    })
		    .setNegativeButton(R.string.cancel, null)
		    .create()
		    .show();
	}
	
	private void selfAssign() {
		new AlertDialog.Builder(this)
		    .setTitle(R.string.title_assign_to_me)
		    .setMessage(R.string.assign_to_me_confirm)
		    .setPositiveButton(R.string.assign_to_me, new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
				    ApiTask.builder(ScheduleActivity.this)
					.setUrl(ApiUrl.ASSIGN_TO_ME)
					.setRequestCode(RC_ASSIGN_TO_ME)
					.setRequestBody(mSchedule.toJson())
					.setProgressMessage(R.string.progress_assigning)
					.setResponseListener(ScheduleActivity.this)
					.exec();
			    }
		    })
		    .setNegativeButton(R.string.cancel, null)
		    .create()
		    .show();
	}
}
