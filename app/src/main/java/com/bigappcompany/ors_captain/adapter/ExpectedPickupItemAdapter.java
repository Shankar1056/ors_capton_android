package com.bigappcompany.ors_captain.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.model.PickupItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 06 Jun 2017 at 6:21 PM
 */

public class ExpectedPickupItemAdapter extends RecyclerView.Adapter<ExpectedPickupItemAdapter.ViewHolder> {
	private ArrayList<PickupItemModel> mItemList = new ArrayList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_expected_schedule_items, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PickupItemModel item = mItemList.get(position);
		
		holder.titleTV.setText(item.getTitle());
		Picasso.with(holder.itemView.getContext())
		    .load(item.getIconUrl())
		    .resizeDimen(R.dimen.dimen_32, R.dimen.dimen_32)
		    .centerInside()
		    .into(holder.iconIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(PickupItemModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public ArrayList<PickupItemModel> getItemList() {
		return mItemList;
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView titleTV;
		ImageView iconIV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			iconIV = (ImageView) itemView.findViewById(R.id.iv_icon);
			iconIV.setColorFilter(Color.WHITE);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
		}
	}
}
