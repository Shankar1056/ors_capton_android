package com.bigappcompany.ors_captain.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 15 Jun 2017 at 11:55 AM
 */

public class AgentModel {
	private String name;
	private String mobile;
	private String email;
	private String business;
	private LatLng location;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setBusiness(String business) {
		this.business = business;
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("agent_name", name);
		object.addProperty("phone", mobile);
		object.addProperty("email", email);
		object.addProperty("business_name", business);
		object.addProperty("business_long", location.longitude);
		object.addProperty("business_lat", location.latitude);
		return object;
	}
}
