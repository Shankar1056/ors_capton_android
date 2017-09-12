package com.bigappcompany.ors_captain.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.adapter.AddItemAdapter;
import com.bigappcompany.ors_captain.adapter.RateCardAdapter;
import com.bigappcompany.ors_captain.dialog.NewItemDialog;
import com.bigappcompany.ors_captain.model.PickupItemModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class ChartActivity extends BaseActivity
    implements View.OnClickListener, RateCardAdapter.OnAddItemListener, NewItemDialog.OnNewItemListener {
	private AddItemAdapter mAddItemAdapter;
	private RateCardAdapter mItemAdapter;
	private ArrayList<PickupItemModel> mPickedItemList, mItemList;
	private SlidingUpPanelLayout slidingSPL;
	
	@Override
	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		
		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);
		
		// sliding up panel control
		slidingSPL = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		slidingSPL.setTouchEnabled(false);
		findViewById(R.id.iv_close).setOnClickListener(this);
		findViewById(R.id.iv_done).setOnClickListener(this);
		
		// Recycler View
		mPickedItemList = (ArrayList<PickupItemModel>) getIntent().getSerializableExtra(EXTRA_DATA);
		mAddItemAdapter = new AddItemAdapter(mPickedItemList);
		((RecyclerView) findViewById(R.id.rv_picked_up_items)).setAdapter(mAddItemAdapter);
		
		// add items
		findViewById(R.id.fab_add).setOnClickListener(this);
		
		// pickup items
		mItemList = (ArrayList<PickupItemModel>) getIntent().getSerializableExtra(EXTRA_ITEM_LIST);
		mItemAdapter = new RateCardAdapter(mItemList);
		mItemAdapter.setListener(this);
		((RecyclerView) findViewById(R.id.rv_rate_card)).setAdapter(mItemAdapter);
		mItemAdapter.removeSelectedItems(mPickedItemList);
		mItemAdapter.addItem(new PickupItemModel());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_done, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_done) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DATA, mAddItemAdapter.getItemList());
			setResult(RESULT_OK, intent);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fab_add:
				slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
				break;
			
			case R.id.iv_done:
				mAddItemAdapter.addItemList(mItemAdapter.getSelectedItemList());
				mItemAdapter.removeSelectedItems();
			case R.id.iv_close:
				slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
				break;
			
		}
	}
	
	@Override
	public void onBackPressed() {
		if (slidingSPL.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
			return;
		}
		
		super.onBackPressed();
	}
	
	@Override
	public void onAddNewItem() {
		NewItemDialog dialog = new NewItemDialog();
		dialog.show(getSupportFragmentManager(), null);
	}
	
	@Override
	public void onNewItem(PickupItemModel item) {
		mAddItemAdapter.addItem(item);
		slidingSPL.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
	}
}
