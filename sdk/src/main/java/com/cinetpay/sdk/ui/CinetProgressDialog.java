package com.cinetpay.sdk.ui;

import com.cinetpay.sdk.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
final class CinetProgressDialog extends Dialog {
	public static String DEFAULT_MSG_TEXT = "Traitement en cours...";

	public CinetProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		// CinetPayUISecurity.checkProgressUIIntegrity(this.getContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cinetpay_dialog_proceed);
		setCanceledOnTouchOutside(false);
		findViewById(R.id.cinetpay_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						cancel();
					}
				});
		setMessage(DEFAULT_MSG_TEXT);
	}

	@Override
	public void setCancelable(boolean flag) {
		// TODO Auto-generated method stub
		super.setCancelable(flag);
		findViewById(R.id.cinetpay_cancel).setVisibility(
				flag ? View.VISIBLE : View.INVISIBLE);
	}

	public CinetProgressDialog setMessage(String text) {
		if(text!=null)
		getTextView().setText(text);
		return this;
	}

	public void setMessage(int text) {
		getTextView().setText(text);
	}

	private TextView getTextView() {
		return (TextView) findViewById(R.id.cinetpay_txt_message);
	}

	public void show(String text) {
		// TODO Auto-generated method stub
		if (!isShowing())
			super.show();
		setMessage(text);
	}

}
