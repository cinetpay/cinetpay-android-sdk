package com.cinetpay.sdk.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.R;
/**
 * 
 * @author Istat: Toukea tatsi Jephte
 *
 */
abstract class BaseDialog extends Dialog {

	protected Activity mContext;
	MerchantService mMerchant;

	public BaseDialog(Activity context, MerchantService merchant) {
		super(context);
		mContext = context;
		mMerchant = merchant;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		onPrepar();
		linkToXml();
		addListener();
		onInit();
		findViewById(R.id.cinetpay_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						cancel();
					}
				});
	}

	abstract void onPrepar();

	abstract void linkToXml();

	abstract void onInit();

	abstract void addListener();

	protected MerchantService getCurrentMerchant() {
		return mMerchant;
	}

}
