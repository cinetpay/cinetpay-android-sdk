package com.cinetpay.sdk.ui;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.R;
import com.cinetpay.sdk.ServiceAccount;
import com.cinetpay.sdk.process.CinetPay;
import com.cinetpay.sdk.tool.ToolKit;
import android.app.Activity;
import android.widget.TextView;

final class AccountCheckDialog extends BaseDialog {

	ServiceAccount mAccount;
	TextView txt_amount, txt_merchant_name;
	static int MAX_MARCHANT_LENGTH = 25;

	public AccountCheckDialog(Activity context, MerchantService merchant, ServiceAccount account) {
		super(context, merchant);
		init();
		mAccount = account;
	}

	AccountCheckDialog(Activity context, MerchantService merchant) {
		super(context, merchant);
		init();
	}

	void setAccount(ServiceAccount account) {
		mAccount = account;
	}

	void init() {
		// CinetPayUISecurity.checkAccountUIIntegrity(this.getContext());
	}

	@Override
	public void show() {
		super.show();
		if (mAccount != null) {
			double solde = 0;
//			if (mAccount != null) {
				solde = mAccount.getSolde();
//			} else
				txt_amount.setText((""
						+ ToolKit.Word.adjustNumber((float) solde) + " FCFA")
						.replace(".", ","));
			txt_merchant_name.setHint(ToolKit.Word.ShortWord(
					mMerchant.getName(), MAX_MARCHANT_LENGTH));
		} else {
			this.cancel();
			new CinetMessageDialog(getContext()).show(CinetPay
					.getCinetPayMessage("NETWORK_ERROR"));
		}
	}

	@Override
	void linkToXml() {
		txt_amount = (TextView) findViewById(R.id.cinetpay_txt_amount);
		txt_merchant_name = (TextView) findViewById(R.id.cinetpay_txt_merchant_name);
	}

	@Override
	void onPrepar() {
		setContentView(R.layout.cinetpay_dialog_account);
	}

	@Override
	void onInit() {
	}

	@Override
	void addListener() {
	}

}
