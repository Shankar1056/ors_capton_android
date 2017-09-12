package com.bigappcompany.ors_captain.model;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 06 Jun 2017 at 12:11 PM
 */

public class CaptainModel implements Serializable {
	private String phone;
	private String otp;
	
	public void setOTP(String otp) {
		this.otp = otp;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
