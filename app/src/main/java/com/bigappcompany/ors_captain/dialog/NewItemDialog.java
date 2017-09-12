package com.bigappcompany.ors_captain.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.model.PickupItemModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 27 Jun 2017 at 10:46 AM
 */

public class NewItemDialog extends DialogFragment implements View.OnClickListener {
	private OnNewItemListener mListener;
	
	private AppCompatEditText nameET, priceET, unitET, quantityET;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppThemeDialog);
		
		try {
			mListener = (OnNewItemListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(
			    getActivity().getLocalClassName() + " must implement OnNewItemListener"
			);
		}
		
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_new_item, container, false);
		
		nameET = (AppCompatEditText) view.findViewById(R.id.et_name);
		priceET = (AppCompatEditText) view.findViewById(R.id.et_price);
		unitET = (AppCompatEditText) view.findViewById(R.id.et_unit);
		quantityET = (AppCompatEditText) view.findViewById(R.id.et_quantity);
		
		view.findViewById(R.id.tv_title).setOnClickListener(this);
		view.findViewById(R.id.btn_add).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_title:
				dismiss();
				break;
			
			case R.id.btn_add:
				PickupItemModel item = getInput();
				if (item != null) {
					dismiss();
					mListener.onNewItem(item);
				}
				break;
		}
	}
	
	private PickupItemModel getInput() {
		String name = nameET.getText().toString().trim();
		String price = priceET.getText().toString().trim();
		String unit = unitET.getText().toString().trim();
		String qty = quantityET.getText().toString().trim();
		
		if (name.length() < 3) {
			nameET.setError(getString(R.string.invalid_name));
			return null;
		} else {
			nameET.setError(null);
		}
		
		try {
			double rate = Double.parseDouble(price);
			
			if (rate <= 0) {
				throw new NumberFormatException();
			}
			
			priceET.setError(null);
		} catch (NumberFormatException e) {
			priceET.setError(getString(R.string.invalid_price));
			return null;
		}
		
		if (unit.length() < 2) {
			unitET.setError(getString(R.string.invalid_unit));
			return null;
		} else {
			unitET.setError(null);
		}
		
		try {
			double quantity = Double.parseDouble(qty);
			if (quantity <= 0) {
				throw new NumberFormatException();
			}
			quantityET.setError(null);
		} catch (NumberFormatException e) {
			quantityET.setError(getString(R.string.invalid_qty));
			return null;
		}
		
		return new PickupItemModel(name, price, unit, qty);
	}
	
	public interface OnNewItemListener {
		void onNewItem(PickupItemModel item);
	}
}
