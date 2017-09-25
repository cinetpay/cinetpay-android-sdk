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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(ressource);
		setCanceledOnTouchOutside(false);
		findViewById(R.id.cinetpay_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        if (findViewById(R.id.cinetpay_retry) != null) {
            findViewById(R.id.cinetpay_retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });
        }
	}

	public void show(String text, boolean paymentAccepted) {
		if (!isShowing()) {
            super.show();
        }
		setMessage(text);
        setRetry(paymentAccepted);
	}

	public void setMessage(String message) {
		((TextView) findViewById(R.id.cinetpay_txt_message)).setText(message);
	}

	public void setRetry(boolean retry) {
		if (findViewById(R.id.cinetpay_retry) != null) {
			findViewById(R.id.cinetpay_retry).setVisibility(retry ? View.VISIBLE : View.GONE);
		}
    }

	@Override
	public void setCancelable(boolean flag) {
		super.setCancelable(flag);
		findViewById(R.id.cinetpay_cancel).setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
	}

}
