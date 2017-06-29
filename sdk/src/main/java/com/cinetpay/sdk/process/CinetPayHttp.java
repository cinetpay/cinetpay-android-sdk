package com.cinetpay.sdk.process;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import android.text.TextUtils;
import android.util.Log;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.Purchasable;
import istat.android.network.http.SimpleHttpQuery;
import istat.android.network.utils.ToolKits;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
final class CinetPayHttp extends SimpleHttpQuery {
	static final String ORIGIN = "as_v3.1";
	static final String cinetPayURL_PROD = "https://api.cinetpay.com/v1/",
			cinetPayURL_DEV = "http://api.sandbox.cinetpay.com/v1/",
			METHOD_PROCESS_PAYMENT = "processPayment",
			METHOD_GET_TOKEN = "getSignatureByPost",
			METHOD_CHECK_PAYMENT_STATUS = "checkPayStatus",
			METHOD_CONSULT_SOLD = "getCompteStatus",
			METHOD_SERVICE_INFO = "getServiceDataBySiteId",
			METHOD_GET_TRANS_HISTORY = "getTransHistory";
	public static final String INDICATOR_TIME_OUT = "Time-out";
	CinetPayProcess mProcess;
	private String URL = cinetPayURL_PROD;

	public CinetPayHttp(CinetPayProcess process) {
		mProcess = process;
		setConnectionTimeOut(60000);
		setSoTimeOut(60000);
		setEncoding("UTF-8");
		setClearParamsAfterEachQueryEnable(true);
		if (!mProcess.getCurrentMerchant().isProductionPlatform()) {
			URL = cinetPayURL_DEV;
		}
	}

	String getToken(Purchasable purchase) throws ClientProtocolException,
			URISyntaxException, IOException {

		addParam("method", METHOD_GET_TOKEN);
		addParam("apikey", mProcess.getCurrentMerchant().getApiKey());
		addParam("cpm_amount", purchase.getAmount());
		addParam("cpm_currency", purchase.getCurrency());
		addParam("cpm_trans_id", purchase.getReference());
		addParam("cpm_trans_date", mProcess.TOKEN_STICKY_DATE);
		addParam("cpm_site_id", mProcess.getCurrentMerchant().getBusinessId());
		addParam("cpm_payment_config", mProcess.PAYMENT_CONFIG);
		addParam("cpm_page_action", mProcess.PAGE_ACTION);
		addParam("cpm_version", mProcess.VERSION);
		addParam("cpm_language", mProcess.LANGUE);
		addParam("origine", ORIGIN);
		addParam("plateform", mProcess.getCurrentMerchant().getPlatform());
		Log.d("getToken_params",
				"url: " + URL + "\nmethod: " + METHOD_GET_TOKEN + "\napikey: "
						+ mProcess.getCurrentMerchant().getApiKey()
						+ "\ncpm_amount: " + purchase.getAmount()
						+ "\ncpm_currency: " + purchase.getCurrency()
						+ "\ncpm_trans_id: " + purchase.getReference()
						+ "\ncpm_trans_date: " + mProcess.TOKEN_STICKY_DATE
						+ "\ncpm_site_id: "
						+ mProcess.getCurrentMerchant().getBusinessId()
						+ "\ncpm_payment_config: " + mProcess.PAYMENT_CONFIG
						+ "\ncpm_page_action: " + mProcess.PAGE_ACTION
						+ "\ncpm_version: " + mProcess.VERSION
						+ "\ncpm_language: " + mProcess.LANGUE + "\norigine: "
						+ ORIGIN);

		if (!TextUtils.isEmpty(purchase.getCustom()))
			addParam("cpm_custom", purchase.getCustom());
		if (!TextUtils.isEmpty(purchase.getDescription()))
			addParam("cpm_designation", purchase.getDescription());
		return ToolKits.Stream.streamToString(doPost(URL)).replace("\"", "")
				.trim();
	}

	String doPurchase(Purchasable purchase) throws ClientProtocolException,
			URISyntaxException, IOException {

		String origin = purchase.getOrigin();
		if (!TextUtils.isEmpty(origin)) {
			origin = ORIGIN + "/" + origin;
		} else {
			origin = ORIGIN;
		}
		MerchantService merchant = mProcess.getCurrentMerchant();
		addParam("method", METHOD_PROCESS_PAYMENT);
		addParam("apikey", mProcess.getCurrentMerchant().getApiKey());
		addParam("cel_phone_num", purchase.getCustomer().getPhone());
		addParam("code_otp", purchase.getCustomer().getOtpCode());
		addParam("payment_method", purchase.getPaymentMethod());
		addParam("signature", mProcess.TOKEN);
		addParam("cpm_trans_date", mProcess.TOKEN_STICKY_DATE);
		addParam("cpm_amount", purchase.getAmount());
		addParam("cpm_currency", purchase.getCurrency());
		addParam("cpm_trans_id", purchase.getReference());
		addParam("cpm_site_id", merchant.getSiteId());
		addParam("cpm_payment_config", mProcess.PAYMENT_CONFIG);
		addParam("cpm_page_action", mProcess.PAGE_ACTION);
		addParam("cpm_version", mProcess.VERSION);
		addParam("cpm_language", mProcess.LANGUE);
		addParam("origine", origin);
		addParam("plateform", merchant.getPlatform());
		if (!TextUtils.isEmpty(purchase.getCustom()))
			addParam("cpm_custom", purchase.getCustom());
		if (!TextUtils.isEmpty(purchase.getDescription()))
			addParam("cpm_designation", purchase.getDescription());
		Log.d("cinet_doPurchase", "TOKEN::" + mProcess.TOKEN + "\nTIME::"
				+ mProcess.TOKEN_STICKY_DATE);
		if (merchant.hasNotificationURLConfigured())
			addParam("notify_url", merchant.getNotificationURL());
		String out = ToolKits.Stream.streamToString(doPost(URL));
		// Log.d("DEBUG", out);
		return out;
	}

	public String getMarchantServiceMoneyAccount()
			throws ClientProtocolException, URISyntaxException, IOException {

		addParam("method", METHOD_CONSULT_SOLD);
		addParam("apikey", mProcess.getCurrentMerchant().getApiKey());
		addParam("cpm_site_id", mProcess.getCurrentMerchant().getBusinessId());
		addParam("origine", ORIGIN);
		addParam("plateform", mProcess.getCurrentMerchant().getPlatform());
		return ToolKits.Stream.streamToString(doPost(URL));
	}

	String getTransactionHistoric(String begginDate, String endDate)
			throws ClientProtocolException, URISyntaxException, IOException {

		addParam("method", METHOD_GET_TRANS_HISTORY);
		addParam("apikey", mProcess.getCurrentMerchant().getApiKey());
		addParam("cpm_site_id", mProcess.getCurrentMerchant().getBusinessId());
		if (begginDate != null)
			addParam("beggin_date", begginDate);
		if (begginDate != null)
			addParam("end_date", endDate);
		addParam("origine", ORIGIN);
		addParam("plateform", mProcess.getCurrentMerchant().getPlatform());
		return ToolKits.Stream.streamToString(doPost(URL));
	}

	String getPaymentStatus(String trans_id) throws ClientProtocolException,
			URISyntaxException, IOException {
		addParam("method", METHOD_CHECK_PAYMENT_STATUS);
		addParam("apikey", mProcess.getCurrentMerchant().getApiKey());
		addParam("cpm_site_id", mProcess.getCurrentMerchant().getBusinessId());
		addParam("cpm_trans_id", trans_id);
		addParam("origine", ORIGIN);
		addParam("plateform", mProcess.getCurrentMerchant().getPlatform());
		return ToolKits.Stream.streamToString(doPost(URL));
	}

	String getServiceInfo() throws ClientProtocolException, URISyntaxException,
			IOException {

		Log.d("getServiceInfo", mProcess.getCurrentMerchant().toString()
				+ " URL::" + URL);
		addParam("method", METHOD_SERVICE_INFO);
		addParam("apikey", mProcess.getCurrentMerchant().getApiKey());
		addParam("cpm_site_id", mProcess.getCurrentMerchant().getBusinessId());
		addParam("origine", ORIGIN);
		String out = ToolKits.Stream.streamToString(doPost(URL));
		return out;
	}

}
