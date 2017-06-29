package com.cinetpay.sdk.process;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.util.Log;

import com.cinetpay.sdk.CinetPayParser;
import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.MerchantService.ServiceInfo;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.PurchaseTransaction;
import com.cinetpay.sdk.ServiceAccount;
import com.cinetpay.sdk.tool.ToolKit;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
final class CinetPayProcess {
	CinetPayHttp http;
	String TOKEN, TOKEN_STICKY_DATE;
	String VERSION = "V1", LANGUE = "fr", PAYMENT_CONFIG = "SINGLE",
			PAGE_ACTION = "PAYMENT";
	MerchantService mMerchant;

	CinetPayProcess(MerchantService merchant) {
		mMerchant = merchant;
		http = new CinetPayHttp(this);
	}

	public PaymentResponse processPayment(Purchasable purchase)
			throws ClientProtocolException, URISyntaxException, IOException,
			JSONException {
		Log.d("cinetPay_prcs_pay_init", "TOKEN::" + TOKEN);
		refreshToken(purchase);
		if (TOKEN.contains(CinetPayHttp.INDICATOR_TIME_OUT)) {
			return new PaymentResponse("1024", "NETWORK_ERROR");
		}
		PaymentResponse response = CinetPayParser.parseToPaymentResponse(http
				.doPurchase(purchase));
		Log.d("cinetPayP_prcs_pay", "response::" + response);
		TOKEN = null;
		return response;
	}

	public PurchaseTransaction checkPaymentStatus(String trans_id)
			throws ClientProtocolException, JSONException, URISyntaxException,
			IOException {
		return CinetPayParser.parseToPurchaseTransaction(http
				.getPaymentStatus(trans_id));
	}

	public List<PurchaseTransaction> getTransactionHistory(String begginDate,
			String endDate) throws ClientProtocolException, JSONException,
			URISyntaxException, IOException {
		String json=http
		.getTransactionHistoric(begginDate, endDate);
		return CinetPayParser.parseToTransactionList(json);
	}

	public ServiceAccount getCurrentAccount() throws ClientProtocolException,
			JSONException, URISyntaxException, IOException {
		return CinetPayParser.parseToServiceAccount(http
				.getMarchantServiceMoneyAccount());
	}

	public ServiceInfo getServiceInfo() throws ClientProtocolException,
			JSONException, URISyntaxException, IOException {
		return CinetPayParser.parseForGetMerchantService(http.getServiceInfo(),
				getCurrentMerchant()).getInfo();
	}

	public void setVersion(String vERSION) {
		VERSION = vERSION;
	}

	public void setLanguage(String lANGUE) {
		LANGUE = lANGUE;
	}

	public MerchantService getCurrentMerchant() {
		return mMerchant;
	}

	public void setPaymentConfig(String pAYMENT_CONFIG) {
		PAYMENT_CONFIG = pAYMENT_CONFIG;
	}

	public void setPageAction(String pAGE_ACTION) {
		PAGE_ACTION = pAGE_ACTION;
	}

	private String refreshToken(Purchasable purchase)
			throws ClientProtocolException, URISyntaxException, IOException {
		if (TOKEN == null) {
			TOKEN_STICKY_DATE = ToolKit.stickDate();
			TOKEN = http.getToken(purchase);
			return TOKEN;
		} else {
			return TOKEN;
		}
	}

}
