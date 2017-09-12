package com.bigappcompany.ors_captain.adapter;

import android.content.res.Resources;
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
 * @created on 09 Jun 2017 at 5:11 PM
 */

public class PickupItemAdapter extends RecyclerView.Adapter<PickupItemAdapter.ViewHolder> {
	private final OnItemClickListener mListener;
	private ArrayList<PickupItemModel> mItemList = new ArrayList<>();
	
	public PickupItemAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_pickup, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PickupItemModel item = mItemList.get(position);
		Resources res = holder.itemView.getResources();
		
		holder.titleTV.setText(item.getTitle());
		holder.weightTV.setText(item.getQty() + " " + item.getUnit());
		holder.priceTV.setText(res.getString(R.string.format_price, item.getTotalItemPrice()));
		
		Picasso.with(holder.itemView.getContext())
		    .load(item.getIconUrl())
		    .resizeDimen(R.dimen.medium, R.dimen.medium)
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
	
	public double getTotalPrice() {
		double totalPrice = 0;
		for (PickupItemModel pickupItemModel : mItemList) {
			totalPrice += pickupItemModel.getTotalItemPrice();
		}
		return totalPrice;
	}
	
	public void deleteItem(int position) {
		mItemList.remove(position);
		notifyItemRemoved(position);
	}
	
	public ArrayList<PickupItemModel> getItemList() {
		return mItemList;
	}
	
	public void addItems(ArrayList<PickupItemModel> itemList) {
		mItemList = itemList;
		notifyDataSetChanged();
	}
	
	public interface OnItemClickListener {
		void onItemClick(PickupItemModel item, int position);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView titleTV, weightTV, priceTV;
		ImageView iconIV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			iconIV = (ImageView) itemView.findViewById(R.id.iv_icon);
			iconIV.setColorFilter(Color.WHITE);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			weightTV = (TextView) itemView.findViewById(R.id.tv_weight);
			priceTV = (TextView) itemView.findViewById(R.id.tv_price);
			
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onItemClick(mItemList.get(getAdapterPosition()), getAdapterPosition());
				}
			});
		}
	}
}
