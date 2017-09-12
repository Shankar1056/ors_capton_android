package com.bigappcompany.ors_captain.model;

import android.text.format.DateFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 08 Jun 2017 at 7:40 PM
 */

public class ScheduleModel implements Serializable {
	public static final String STATUS_ACCEPTED = "accepted";
	public static final String STATUS_ON_THE_WAY = "ontheway";
	private static final String STATUS_PENDING = "pending";
	private static final String STATUS_CANCELLED = "cancelled";
	private static final String STATUS_COMPLETED = "completed";
	
	private final int scheduleId;
	private final String startTime;
	private final String endTime;
	private final String name;
	private final String date;
	private final String address;
	private final double longitude;
	private final double latitude;
	private String scheduleStatus;
	private String customerNumber;
	private ArrayList<PickupItemModel> expectedItemList;
	
	public ScheduleModel(JsonObject item) throws ParseException {
		scheduleId = item.get("schedule_id").getAsInt();
		
		SimpleDateFormat sdf = new SimpleDateFormat("H:m:s", Locale.UK);
		Date start = sdf.parse(item.get("slot_start").getAsString());
		Date end = sdf.parse(item.get("slot_end").getAsString());
		startTime = DateFormat.format("hh:mm a", start).toString();
		endTime = DateFormat.format("hh:mm a", end).toString();
		
		name = item.get("name").getAsString();
		
		sdf = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.UK);
		date = DateFormat.format("dd MMM yyyy", sdf.parse(item.get("scheduled_date")
		    .getAsString())).toString();
		
		address = item.get("schedule_address").getAsString();
		scheduleStatus = item.get("schedule_status").getAsString();
		latitude = item.get("pick_up_lat").getAsDouble();
		longitude = item.get("pick_up_long").getAsDouble();
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getCustomerNumber() {
		return customerNumber;
	}
	
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	
	public String getScheduleStatus() {
		return scheduleStatus;
	}
	
	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
	
	public boolean isPending() {
		return scheduleStatus.equalsIgnoreCase(STATUS_PENDING);
	}
	
	public boolean isAccepted() {
		return scheduleStatus.equalsIgnoreCase(STATUS_ACCEPTED);
	}
	
	public boolean isCancelled() {
		return scheduleStatus.equalsIgnoreCase(STATUS_CANCELLED);
	}
	
	public boolean isCompleted() {
		return scheduleStatus.equalsIgnoreCase(STATUS_COMPLETED);
	}
	
	public boolean isOnTheWay() {
		return scheduleStatus.equalsIgnoreCase(STATUS_ON_THE_WAY);
	}
	
	public ArrayList<PickupItemModel> getExpectedItemList() {
		return expectedItemList;
	}
	
	public void setExpectedItemList(ArrayList<PickupItemModel> expectedItemList) {
		this.expectedItemList = expectedItemList;
	}
	
	public JsonObject toJson(ArrayList<PickupItemModel> itemList, String paymentMethod) {
		JsonObject object = toJson();
		object.addProperty("payment_method", paymentMethod);
		JsonArray items = new JsonArray();
		for (PickupItemModel pickupItemModel : itemList) {
			if (pickupItemModel.getQty() > 0) {
				items.add(pickupItemModel.toJson());
			}
		}
		
		object.add("items", items);
		return object;
	}
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("schedule_id", scheduleId);
		return object;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
}
