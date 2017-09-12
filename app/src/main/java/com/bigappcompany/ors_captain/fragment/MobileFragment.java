package com.bigappcompany.ors_captain.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.listener.OnAuthListener;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 06 Jun 2017 at 11:59 AM
 */

public class MobileFragment extends Fragment implements View.OnClickListener {
	private OnAuthListener mListener;
	
	private AppCompatEditText mobileET;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			mListener = (OnAuthListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(
			    getActivity().getLocalClassName() + " must implement OnAuthListener"
			);
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mobile, container, false);
		
		mobileET = (AppCompatEditText) view.findViewById(R.id.et_mobile);
		view.findViewById(R.id.btn_verify).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		if (getPhoneNumber() != null) {
			mListener.onAuth(getPhoneNumber(), getTag());
		}
	}
	
	private String getPhoneNumber() {
		String mobile = mobileET.getText().toString().trim();
		
		if (!PhoneNumberUtils.isGlobalPhoneNumber(mobile) || mobile.length() != 10) {
			mobileET.setError(getString(R.string.invalid_mobile));
			return null;
		}
		
		mobileET.setError(null);
		return mobile;
	}
}
