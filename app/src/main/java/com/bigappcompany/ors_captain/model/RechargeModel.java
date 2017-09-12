package com.bigappcompany.ors_captain.model;

import com.google.gson.JsonObject;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 28 Jun 2017 at 7:06 PM
 */

public class RechargeModel {
	private double amount;
	private String orderId;
	private String rechargeId;
	private String transactionId;
	
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("amount", amount);
		object.addProperty("recharge_id", rechargeId);
		object.addProperty("pg_tr_id", transactionId);
		return object;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	public void setIds(JsonObject data) {
		orderId = data.get("pg_order_id").getAsString();
		rechargeId = data.get("recharge_id").getAsString();
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
