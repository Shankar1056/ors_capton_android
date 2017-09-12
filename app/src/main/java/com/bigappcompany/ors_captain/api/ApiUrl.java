package com.bigappcompany.ors_captain.api;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 2017-04-11 at 4:15 PM
 */

public final class ApiUrl {
	private static final String URL_BASE = "http://www.ors.online/manage/api_v1/captain/";
	
	public static final String REGISTER = URL_BASE + "registerAgent";
	public static final String GENERATE_OTP = URL_BASE + "sendOTP";
	public static final String SUBMIT_OTP = URL_BASE + "validateOTP";
	public static final String GCM_TOKEN_UPDATE = URL_BASE + "setPushToken";
	public static final String CHECK_FOR_UPDATES = URL_BASE + "checkForUpdates";
	
	public static final String SCHEDULES = URL_BASE + "listSchedules";
	public static final String GET_SCHEDULE_BY_ID = URL_BASE + "getSchedule";
	public static final String ASSIGN_TO_ME = URL_BASE + "assignToMe";
	public static final String START_PROCESS = URL_BASE + "onTheWay";
	public static final String TRANSACT = URL_BASE + "transact";
	public static final String GET_ITEM_LIST = URL_BASE + "getRateCard";
	public static final String WALLET_BALANCE = URL_BASE + "getWalletBalance";
	public static final String GET_PAYMENT_ORDER = URL_BASE + "createPGOrder";
	public static final String MAKE_PAYMENT = URL_BASE + "updateRechargePaymentID";
	public static final String CANCEL_PAYMENT = URL_BASE + "cancelPayment";
}
