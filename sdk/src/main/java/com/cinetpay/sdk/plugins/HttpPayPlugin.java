package com.cinetpay.sdk.plugins;

import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.process.CinetPay.PayCallBack;
import com.cinetpay.sdk.ui.CinetPayUI;
import com.cinetpay.sdk.ui.PayPlugin;

public class HttpPayPlugin extends PayPlugin {

	public HttpPayPlugin(CinetPayUI ui) {
		super(ui);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onProcessPayment(Purchasable mPurchase, PayCallBack callBack) {
		// TODO Auto-generated method stub
		//ui.beginPayment(mPurchase, callBack);
	}

	@Override
	public boolean needUIValidator() {
		// TODO Auto-generated method stub
		return true;
	}

}
