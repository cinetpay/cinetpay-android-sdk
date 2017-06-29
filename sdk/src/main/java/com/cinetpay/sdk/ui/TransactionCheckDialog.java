package com.cinetpay.sdk.ui;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.R;
import com.cinetpay.sdk.ServiceAccount;
import android.app.Activity;
/**
 * 
 * @author Istat: Toukea tatsi Jephte
 *
 */
final class TransactionCheckDialog extends BaseDialog {
	ServiceAccount mAccount;

	 TransactionCheckDialog(Activity context, MerchantService merchant,ServiceAccount account) {
		super(context, merchant);
		// TODO Auto-generated constructor stub
		//CinetPayUISecurity.checkTransactionUIIntegrity(this.getContext());
		mAccount=account;
	}
	void setAccount(ServiceAccount account){
		mAccount=account;
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
	

	@Override
	void linkToXml() {
		// TODO Auto-generated method stub

	}

	@Override
	void onPrepar() {
		// TODO Auto-generated method stub
		setContentView(R.layout.cinetpay_dialog_account);
	}

	@Override
	void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	void addListener() {
		// TODO Auto-generated method stub

	};

}
