package com.bigappcompany.ors_captain.fragment;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.activity.MapActivity;
import com.bigappcompany.ors_captain.listener.OnAuthListener;
import com.bigappcompany.ors_captain.model.AgentModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.bigappcompany.ors_captain.activity.BaseActivity.EXTRA_DATA;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 Jun 2017 at 3:56 PM
 */

public class SignUpFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
	private static final int RC_MAP = 0;
	private AppCompatEditText nameET, mobileET, emailET, businessET, addressET;
	private AgentModel mAgent;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_submit:
				if (isInputValid()) {
					((OnAuthListener) getActivity()).onSignUp(mAgent);
				}
				break;
		}
	}
	
	private boolean isInputValid() {
		boolean isValid = true;
		String name = nameET.getText().toString().trim();
		String mobile = mobileET.getText().toString().trim();
		String email = emailET.getText().toString().trim();
		String business = businessET.getText().toString().trim();
		String address = addressET.getText().toString().trim();
		
		if (name.length() < 3) {
			nameET.setError(getString(R.string.invalid_name));
			isValid = false;
		} else {
			nameET.setError(null);
			mAgent.setName(name);
		}
		
		if (mobile.length() != 10 || !PhoneNumberUtils.isGlobalPhoneNumber(mobile)) {
			mobileET.setError(getString(R.string.invalid_mobile));
			isValid = false;
		} else {
			mobileET.setError(null);
			mAgent.setMobile(mobile);
		}
		
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			emailET.setError(getString(R.string.invalid_email));
			isValid = false;
		} else {
			emailET.setError(null);
			mAgent.setEmail(email);
		}
		
		if (business.length() < 3) {
			businessET.setError(getString(R.string.invalid_business));
			isValid = false;
		} else {
			businessET.setError(null);
			mAgent.setBusiness(business);
		}
		
		if (mAgent.getLocation() == null) {
			addressET.setError(getString(R.string.please_choose_location));
			isValid = false;
		} else {
			addressET.setError(null);
		}
		
		return isValid;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			case RC_MAP:
				if (resultCode == RESULT_OK) {
					saveMapData(data);
				}
				break;
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mAgent = new AgentModel();
		
		View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
		nameET = (AppCompatEditText) view.findViewById(R.id.et_name);
		mobileET = (AppCompatEditText) view.findViewById(R.id.et_mobile);
		emailET = (AppCompatEditText) view.findViewById(R.id.et_email);
		businessET = (AppCompatEditText) view.findViewById(R.id.et_business);
		addressET = (AppCompatEditText) view.findViewById(R.id.et_address);
		addressET.setOnTouchListener(this);
		view.findViewById(R.id.btn_submit).setOnClickListener(this);
		
		return view;
	}
	
	private void saveMapData(Intent data) {
		List<Address> addressList = data.getParcelableArrayListExtra(EXTRA_DATA);
		Address add = addressList.get(0);
		String address = add.getAddressLine(0) + ", " + add.getAddressLine(1) + ", " + add.getAddressLine(2);
		addressET.setText(address);
		addressET.setError(null);
		mAgent.setLocation(new LatLng(add.getLatitude(), add.getLongitude()));
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getRawX() >= (addressET.getRight() - addressET.getCompoundDrawables()[2].getBounds().width())) {
				Intent intent = new Intent(getContext(), MapActivity.class);
				startActivityForResult(intent, RC_MAP);
				return true;
			}
		}
		
		return false;
	}
}
