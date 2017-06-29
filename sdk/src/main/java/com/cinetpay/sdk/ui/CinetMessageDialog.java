package com.cinetpay.sdk.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cinetpay.sdk.R;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
class CinetMessageDialog extends Dialog {

	public CinetMessageDialog(Context context) {
		super(context);
		// CinetPayUISecurity.checkMessageUIIntegrity(this.getContext());
		init(R.layout.cinetpay_dialog_info);
	}

	protected CinetMessageDialog(Context context, int ressource) {
		super(context);
		// TODO Auto-generated constructor stub
		// CinetPayUISecurity.checkMessageUIIntegrity(this.getContext());
		init(ressource);
	}

	private void init(int ressource) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ressource);
		setCanceledOnTouchOutside(false);
		findViewById(R.id.cinetpay_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						cancel();
					}
				});
	}

	public void show(String text) {
		// TODO Auto-generated method stub
		if (!isShowing())
			super.show();
		setMessage(text);
	}

	public void setMessage(String message) {
		((TextView) findViewById(R.id.cinetpay_txt_message)).setText(message);
	}

	@Override
	public void setCancelable(boolean flag) {
		// TODO Auto-generated method stub
		super.setCancelable(flag);
		findViewById(R.id.cinetpay_cancel).setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
	}

}
