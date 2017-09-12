package com.bigappcompany.ors_captain.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.model.ScheduleModel;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 29 May 2017 at 11:27 AM
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
	private final ArrayList<ScheduleModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	
	public ScheduleAdapter(OnItemClickListener listener) {
		this.mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_schedule, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ScheduleModel item = mItemList.get(position);
		
		holder.startTimeTV.setText(item.getStartTime());
		holder.endTimeTV.setText(item.getEndTime());
		holder.nameTV.setText(item.getName());
		holder.dateTV.setText(item.getDate());
		holder.addressTV.setText(item.getAddress());
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(ScheduleModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public void clear() {
		mItemList.clear();
		notifyDataSetChanged();
	}
	
	public interface OnItemClickListener {
		void onItemClick(ScheduleModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView startTimeTV, endTimeTV, nameTV, dateTV, addressTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			startTimeTV = (TextView) itemView.findViewById(R.id.tv_time_start);
			endTimeTV = (TextView) itemView.findViewById(R.id.tv_time_end);
			nameTV = (TextView) itemView.findViewById(R.id.tv_name);
			dateTV = (TextView) itemView.findViewById(R.id.tv_date);
			addressTV = (TextView) itemView.findViewById(R.id.tv_address);
			
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onItemClick(mItemList.get(getAdapterPosition()));
				}
			});
		}
	}
}
