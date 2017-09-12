package com.bigappcompany.ors_captain.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
 * @created on 09 Jun 2017 at 6:51 PM
 */

public class AddItemAdapter extends RecyclerView.Adapter<AddItemAdapter.ViewHolder> {
	private ArrayList<PickupItemModel> mItemList = new ArrayList<>();
	
	public AddItemAdapter(ArrayList<PickupItemModel> itemList) {
		mItemList = itemList;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_pickup_add, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PickupItemModel item = mItemList.get(position);
		Resources res = holder.itemView.getResources();
		
		holder.titleTV.setText(item.getTitle());
		holder.unitTV.setText(item.getUnit());
		holder.quantityET.setText(String.valueOf(item.getQty()));
		
		Picasso.with(holder.itemView.getContext())
		    .load(item.getIconUrl())
		    .fit()
		    .into(holder.iconIV);
		
		holder.speed0TV.setText(res.getString(R.string.format_unit, 1, item.getUnit()));
		holder.speed1TV.setText(res.getString(R.string.format_unit, 2, item.getUnit()));
		holder.speed2TV.setText(res.getString(R.string.format_unit, 3, item.getUnit()));
		holder.speed3TV.setText(res.getString(R.string.format_unit, 4, item.getUnit()));
		holder.speed4TV.setText(res.getString(R.string.format_unit, 5, item.getUnit()));
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public ArrayList<PickupItemModel> getItemList() {
		return mItemList;
	}
	
	public void addItemList(ArrayList<PickupItemModel> selectedItemList) {
		if (selectedItemList.size() > 0) {
			mItemList.addAll(selectedItemList);
			notifyItemRangeInserted(mItemList.indexOf(selectedItemList.get(0)), mItemList.size() - 1);
		}
	}
	
	public void addItem(PickupItemModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher, View.OnClickListener {
		TextView titleTV, unitTV, speed0TV, speed1TV, speed2TV, speed3TV, speed4TV;
		AppCompatEditText quantityET;
		ImageView iconIV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			unitTV = (TextView) itemView.findViewById(R.id.tv_unit);
			quantityET = (AppCompatEditText) itemView.findViewById(R.id.et_quantity);
			iconIV = (ImageView) itemView.findViewById(R.id.iv_icon);
			iconIV.setColorFilter(Color.WHITE);
			
			speed0TV = (TextView) itemView.findViewById(R.id.tv_speed_0);
			speed1TV = (TextView) itemView.findViewById(R.id.tv_speed_1);
			speed2TV = (TextView) itemView.findViewById(R.id.tv_speed_2);
			speed3TV = (TextView) itemView.findViewById(R.id.tv_speed_3);
			speed4TV = (TextView) itemView.findViewById(R.id.tv_speed_4);
			
			speed0TV.setOnClickListener(this);
			speed1TV.setOnClickListener(this);
			speed2TV.setOnClickListener(this);
			speed3TV.setOnClickListener(this);
			speed4TV.setOnClickListener(this);
			quantityET.addTextChangedListener(this);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			try {
				mItemList.get(getAdapterPosition()).setQty(Double.parseDouble(s.toString()));
			} catch (NumberFormatException e) {
				quantityET.setError(quantityET.getContext().getString(R.string.invalid_qty));
			}
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_speed_0:
				case R.id.tv_speed_1:
				case R.id.tv_speed_2:
				case R.id.tv_speed_3:
				case R.id.tv_speed_4:
					mItemList.get(getAdapterPosition()).setQty(Double.parseDouble(v.getTag().toString()));
					quantityET.setText(v.getTag().toString());
					break;
			}
		}
	}
}
