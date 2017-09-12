package com.bigappcompany.ors_captain.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
 * @created on 12 Jun 2017 at 11:37 AM
 */

public class RateCardAdapter extends RecyclerView.Adapter<RateCardAdapter.ViewHolder> {
	private final ArrayList<PickupItemModel> mItemList;
	private boolean isSelectable = true;
	private OnAddItemListener mListener;
	
	public RateCardAdapter(ArrayList<PickupItemModel> itemList) {
		mItemList = itemList;
	}
	
	public void setListener(OnAddItemListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_rate_card, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		PickupItemModel item = mItemList.get(position);
		Resources res = holder.itemView.getResources();
		Context context = holder.itemView.getContext();
		
		holder.itemView.setBackgroundColor(
		    ContextCompat.getColor(
			context,
			item.isSelected() ? R.color.transparent_green : R.color.white
		    )
		);
		holder.titleTV.setText(item.getTitle());
		holder.priceTV.setText(res.getString(R.string.format_price, item.getUnitPrice()));
		Picasso.with(context)
		    .load(item.getIconUrl())
		    .fit()
		    .into(holder.iconIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void setSelectable(boolean selectable) {
		isSelectable = selectable;
	}
	
	public void removeSelectedItems() {
		mItemList.removeAll(getSelectedItemList());
		notifyDataSetChanged();
	}
	
	public ArrayList<PickupItemModel> getSelectedItemList() {
		ArrayList<PickupItemModel> selectedItemList = new ArrayList<>();
		for (PickupItemModel item : mItemList) {
			if (item.isSelected()) {
				selectedItemList.add(item);
			}
		}
		return selectedItemList;
	}
	
	public void removeSelectedItems(ArrayList<PickupItemModel> itemList) {
		mItemList.removeAll(itemList);
		notifyDataSetChanged();
	}
	
	public void addItem(PickupItemModel itemModel) {
		mItemList.add(itemModel);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public interface OnAddItemListener {
		void onAddNewItem();
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView iconIV;
		TextView titleTV, priceTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			iconIV = (ImageView) itemView.findViewById(R.id.iv_icon);
			iconIV.setColorFilter(Color.WHITE);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			priceTV = (TextView) itemView.findViewById(R.id.tv_price);
			
			if (isSelectable) {
				itemView.setOnClickListener(this);
			}
		}
		
		@Override
		public void onClick(View v) {
			if (getAdapterPosition() < mItemList.size() - 1) {
				PickupItemModel item = mItemList.get(getAdapterPosition());
				item.setSelected(!item.isSelected());
				notifyItemChanged(getAdapterPosition());
			} else {
				if (mListener != null) {
					mListener.onAddNewItem();
				}
			}
		}
	}
}
