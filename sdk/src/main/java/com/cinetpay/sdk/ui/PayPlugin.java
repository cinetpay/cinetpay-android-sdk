package com.cinetpay.sdk.ui;

import android.app.Dialog;
import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.process.CinetPay;
import com.cinetpay.sdk.process.CinetPay.PayCallBack;

public abstract class PayPlugin {
	protected CinetPayUI ui;
	protected CinetPay cinetpay;
	protected MerchantService mMerchant;;

	public PayPlugin(CinetPayUI ui) {
		this.ui = ui;
		cinetpay = ui.getCinetPayInstance();
		mMerchant = cinetpay.getCurrentMerchant();
	}

	protected abstract void onProcessPayment(Purchasable mPurchase,
			PayCallBack callBack);

	public abstract boolean needUIValidator();

	protected final void showCinetMessageDialog(String string) {
		// TODO Auto-generated method stub
		ui.showCinetMessageDialog(string);
	}

	protected final Dialog showCinetProgressDialog(String string, boolean b) {
		// TODO Auto-generated method stub
		return ui.showCinetProgressDialog(string, b);
	}

	protected final void onPaymentComplete(PaymentResponse response,
			Purchase purchase, PayCallBack callBack) {
		// TODO Auto-generated method stub
		ui.mPayUI.onPaymentComplete(response, purchase);
	}

	public final void beginPayment(final Purchasable purchase,
			final PayCallBack callBack) {
		ui.beginPayment(this, purchase, callBack);
	}

	public MerchantService getCurrentMerchant() {
		return mMerchant;
	}

	public CinetPay getCinetpayInstance() {
		return cinetpay;
	}
}
