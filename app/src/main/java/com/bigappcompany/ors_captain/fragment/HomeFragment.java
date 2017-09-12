package com.bigappcompany.ors_captain.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.activity.ScheduleActivity;
import com.bigappcompany.ors_captain.adapter.ScheduleAdapter;
import com.bigappcompany.ors_captain.api.ApiTask;
import com.bigappcompany.ors_captain.api.ApiUrl;
import com.bigappcompany.ors_captain.api.OnApiListener;
import com.bigappcompany.ors_captain.model.ScheduleModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 29 May 2017 at 10:18 AM
 */

@SuppressWarnings("ConstantConditions")
public class HomeFragment extends Fragment implements ScheduleAdapter.OnItemClickListener, OnApiListener, SwipeRefreshLayout.OnRefreshListener {
	private static final String EXTRA_DATA = "extra_data";
	private static final String TAG = "HomeFragment";
	private static final String ARG_CATEGORY = "arg_category";
	private ScheduleAdapter mAdapter;
	private SwipeRefreshLayout scheduleSRL;
	
	public static HomeFragment newInstance(String category) {
		Bundle args = new Bundle();
		args.putString(ARG_CATEGORY, category);
		HomeFragment fragment = new HomeFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		scheduleSRL = (SwipeRefreshLayout) view.findViewById(R.id.srl_schedule);
		scheduleSRL.setOnRefreshListener(this);
		
		mAdapter = new ScheduleAdapter(this);
		RecyclerView homeRV = (RecyclerView) view.findViewById(R.id.rv_schedule);
		homeRV.setAdapter(mAdapter);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		onRefresh();
	}
	
	@Override
	public void onRefresh() {
		scheduleSRL.setRefreshing(true);
		
		JsonObject object = new JsonObject();
		object.addProperty("schedule_status", getArguments().getString(ARG_CATEGORY));
		ApiTask.builder(getContext())
		    .setUrl(ApiUrl.SCHEDULES)
		    .setRequestBody(object)
		    .setResponseListener(this)
		    .exec();
	}
	
	@Override
	public void onItemClick(ScheduleModel item) {
		Intent intent = new Intent(getContext(), ScheduleActivity.class);
		intent.putExtra(EXTRA_DATA, item);
		startActivity(intent);
	}
	
	@Override
	public void onSuccess(JsonObject response, int requestCode, Bundle savedData) {
		if (isAdded()) {
			mAdapter.clear();
			JsonArray data = response.getAsJsonArray("data");
			for (JsonElement schedule : data) {
				try {
					mAdapter.addItem(new ScheduleModel((JsonObject) schedule));
				} catch (ParseException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
			
			scheduleSRL.setRefreshing(false);
			getView().findViewById(R.id.tv_not_found).setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
		}
	}
	
	@Override
	public void onFailure(int requestCode, Bundle savedData) {
		
	}
}
