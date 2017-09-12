package com.bigappcompany.ors_captain.model;

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 09 Jun 2017 at 1:02 PM
 */

public class PickupItemModel implements Serializable {
	private final int itemId;
	private final String title;
	private final String unit;
	private final double unitPrice;
	private String iconUrl;
	private double qty = 0.0;
	private boolean isSelected = false;
	
	public PickupItemModel(JsonObject item) {
		itemId = item.get("item_id").getAsInt();
		title = item.get("item_name").getAsString();
		iconUrl = item.get("icon_url").getAsString();
		unit = item.get("unit").getAsString();
		unitPrice = item.get("item_rate").getAsDouble();
	}
	
	public PickupItemModel() {
		itemId = 0;
		title = "Other";
		iconUrl = null;
		unit = "";
		unitPrice = 0;
	}
	
	public PickupItemModel(String name, String price, String unit, String qty) {
		itemId = 0;
		title = name;
		this.unit = unit;
		this.unitPrice = Double.parseDouble(price);
		this.qty = Double.parseDouble(qty);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public double getUnitPrice() {
		return unitPrice;
	}
	
	public double getQty() {
		return qty;
	}
	
	public void setQty(double qty) {
		this.qty = qty;
	}
	
	public double getTotalItemPrice() {
		return unitPrice * qty;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean selected) {
		isSelected = selected;
	}
	
	JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("item_id", itemId);
		object.addProperty("quantity", qty);
		
		// new custom item
		if (itemId == 0) {
			object.addProperty("rate", unitPrice);
			object.addProperty("unit", unit);
			object.addProperty("description", title);
		}
		
		return object;
	}
	
	@Override
	public int hashCode() {
		return itemId;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PickupItemModel && ((PickupItemModel) obj).itemId == itemId;
	}
}
