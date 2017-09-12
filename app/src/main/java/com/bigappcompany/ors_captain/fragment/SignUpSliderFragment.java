package com.bigappcompany.ors_captain.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.ors_captain.R;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 14 Jun 2017 at 7:43 PM
 */

public class SignUpSliderFragment extends Fragment {
	private static final String ARG_TITLE_RES_ID = "arg_title_res_id";
	private static final String ARG_DESC_RES_ID = "arg_desc_res_id";
	private static final String ARG_IMAGE_RES_ID = "arg_image_res_id";
	
	public static SignUpSliderFragment newInstance(int titleResId, int descResId, int imageId) {
		Bundle args = new Bundle();
		args.putInt(ARG_TITLE_RES_ID, titleResId);
		args.putInt(ARG_DESC_RES_ID, descResId);
		args.putInt(ARG_IMAGE_RES_ID, imageId);
		SignUpSliderFragment fragment = new SignUpSliderFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sign_up_slider, container, false);
		
		((TextView) view.findViewById(R.id.tv_title)).setText(getArguments().getInt(ARG_TITLE_RES_ID));
		((ImageView) view.findViewById(R.id.iv_image)).setImageResource(getArguments().getInt(ARG_IMAGE_RES_ID));
		((TextView) view.findViewById(R.id.tv_desc)).setText(getArguments().getInt(ARG_DESC_RES_ID));
		
		return view;
	}
}
