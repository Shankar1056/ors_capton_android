package com.bigappcompany.ors_captain.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.listener.OnAuthListener;
import com.mukesh.OtpView;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 Jun 2017 at 11:42 AM
 */

public class OTPFragment extends Fragment implements View.OnClickListener {
	private OtpView otpView;
	private AppCompatButton submitBtn;
	private OnAuthListener mListener;
	
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
		View view = inflater.inflate(R.layout.fragment_otp, container, false);
		
		otpView = (OtpView) view.findViewById(R.id.otp_view);
		setupOTPView();
		
		submitBtn = (AppCompatButton) view.findViewById(R.id.btn_submit);
		submitBtn.setOnClickListener(this);
		
		ClickableSpan cs = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				mListener.onResendOTP();
			}
		};
		
		SpannableString resend = new SpannableString(getString(R.string.resend_otp));
		resend.setSpan(cs, resend.length() - 11, resend.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		TextView resendTV = (TextView) view.findViewById(R.id.tv_resend);
		resendTV.setText(resend);
		resendTV.setMovementMethod(LinkMovementMethod.getInstance());
		resendTV.setHighlightColor(Color.TRANSPARENT);
		
		return view;
	}
	
	private void setupOTPView() {
		int[] res = new int[]{
		    R.id.otp_one_edit_text, R.id.otp_two_edit_text,
		    R.id.otp_three_edit_text, R.id.otp_four_edit_text
		};
		
		for (int id : res) {
			EditText editText = (EditText) otpView.findViewById(id);
			editText.setHint(String.valueOf(0));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				editText.setTextAppearance(R.style.OTPHint);
			} else {
				editText.setTextAppearance(getContext(), R.style.OTPHint);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (otpView.getOTP().matches("\\d{4}")) {
			mListener.onAuth(otpView.getOTP(), getTag());
		} else {
			Toast.makeText(getContext(), R.string.invalid_otp, Toast.LENGTH_SHORT).show();
		}
	}
}
