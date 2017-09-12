package com.bigappcompany.ors_captain.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.model.PickupItemModel;
import com.squareup.picasso.Picasso;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Jun 2017 at 4:06 PM
 */

@SuppressWarnings("ConstantConditions")
public class PickupDialog extends DialogFragment implements View.OnClickListener {
	private static final String EXTRA_DATA = "extra_data";
	private static final String EXTRA_POSITION = "extra_position";
	
	@IdRes
	private final int speedIds[] = new int[]{
		R.id.tv_speed_0, R.id.tv_speed_1, R.id.tv_speed_2, R.id.tv_speed_3, R.id.tv_speed_4
	};
	
	private AppCompatEditText quantityET;
	private OnQuantityListener mListener;
	private PickupItemModel mPickupItem;
	private int position;
	
	public static PickupDialog newInstance(PickupItemModel item, int position) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATA, item);
		args.putInt(EXTRA_POSITION, position);
		PickupDialog fragment = new PickupDialog();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			mPickupItem = (PickupItemModel) getArguments().getSerializable(EXTRA_DATA);
			position = getArguments().getInt(EXTRA_POSITION, -1);
			mListener = (OnQuantityListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(
			    getActivity().getLocalClassName() + " must implement OnQuantityListener"
			);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Dialog dialog = getDialog();
		if (dialog != null) {
			dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_pickup, container, false);
		
		((TextView) view.findViewById(R.id.tv_title)).setText(mPickupItem.getTitle());
		((TextView) view.findViewById(R.id.tv_unit)).setText(mPickupItem.getUnit());
		
		quantityET = (AppCompatEditText) view.findViewById(R.id.et_quantity);
		quantityET.setText(String.valueOf(mPickupItem.getQty()));
		
		for (int i = 0; i < speedIds.length; i++) {
			TextView speedTV = (TextView) view.findViewById(speedIds[i]);
			speedTV.setText(getString(R.string.format_unit, (i + 1), mPickupItem.getUnit()));
			speedTV.setOnClickListener(this);
		}
		
		view.findViewById(R.id.btn_delete).setOnClickListener(this);
		TextView addOrModifyTV = (TextView) view.findViewById(R.id.btn_add_or_modify);
		addOrModifyTV.setText(position == -1 ? R.string.add : R.string.save);
		addOrModifyTV.setOnClickListener(this);
		
		ImageView iconIV = (ImageView) view.findViewById(R.id.iv_icon);
		iconIV.setColorFilter(Color.WHITE);
		Picasso.with(getContext())
		    .load(mPickupItem.getIconUrl())
		    .fit()
		    .into(iconIV);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_speed_0:
			case R.id.tv_speed_1:
			case R.id.tv_speed_2:
			case R.id.tv_speed_3:
			case R.id.tv_speed_4:
				quantityET.setText(v.getTag().toString());
				break;
			
			case R.id.btn_delete:
				dismiss();
				mListener.onDelete(mPickupItem, position);
				break;
			
			case R.id.btn_add_or_modify:
				if (position == -1) {
					dismiss();
					
					mListener.onAdd(mPickupItem);
				} else {
					try {
						double qty = Double.parseDouble(quantityET.getText().toString());
						if (qty == 0) throw new NumberFormatException("0 Quantity");
						
						dismiss();
						
						mPickupItem.setQty(qty);
						mListener.onModify(mPickupItem, position);
					} catch (NumberFormatException e) {
						quantityET.setError(getString(R.string.invalid_qty));
					}
				}
				break;
		}
	}
	
	public interface OnQuantityListener {
		void onAdd(PickupItemModel item);
		
		void onModify(PickupItemModel item, int position);
		
		void onDelete(PickupItemModel item, int position);
	}
}
