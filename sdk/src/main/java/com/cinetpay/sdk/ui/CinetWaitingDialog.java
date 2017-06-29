package com.cinetpay.sdk.ui;

import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.PurchaseTransaction;
import com.cinetpay.sdk.R;
import com.cinetpay.sdk.process.CinetPay.TransactionCheckCallBack;
import com.cinetpay.sdk.tool.CinetPayText;
import com.cinetpay.sdk.tool.ToolKit.Word;

/**
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
class CinetWaitingDialog extends CinetMessageDialog implements
		TransactionCheckCallBack, View.OnClickListener {
	final static int PAY_TIME_OUT = 1000 * 60;
	PayUI ui = PayUI.instance;
	Purchasable currentPurchase;
	Button btn_verify;
	Handler mHandler;

	final static int TIME_AUTO_CLOSE_TIME_OUT = 5000;
	static PaymentResponse PAYMENT_RESPONSE_NOTIFY_WAITING = new PaymentResponse(
			"6000", "WAITING_CUSTOMER_TO_VALIDATE");

	public CinetWaitingDialog(Purchasable purchase) {
		super(PayUI.UI.getContext(), R.layout.cinetpay_dialog_proceed);
		// TODO Auto-generated constructor stub
		this.currentPurchase = purchase;
		init();

	}

	private void init() {
		mHandler = new Handler();
		btn_verify = (Button) findViewById(R.id.cinetpay_btn_verify);
		btn_verify.setOnClickListener(this);
		btn_verify.setVisibility(View.VISIBLE);
		findViewById(R.id.cinetpay_pgs_loader).setVisibility(View.GONE);
		btn_verify.setText("Vérifier $type".replace("$type",
				getPurchaseTraitmentName(currentPurchase)));
		setCancelable(true);
	}

	private void verifyPayment(Purchasable p) {
		setCancelable(false);
		setMessage("Vérification de votre "
				+ getPurchaseTraitmentName(currentPurchase) + " en cours");
		findViewById(R.id.cinetpay_pgs_loader).setVisibility(View.VISIBLE);
		btn_verify.setVisibility(View.GONE);
		PayUI.UI.getCinetPayInstance().doAsyncTransactionCheck(this,
				p.getReference());
	}

	private void onPaymentWaiting(Purchasable currentPurchase2) {
		// TODO Auto-generated method stub
		setCancelable(false);
		btn_verify.setVisibility(View.VISIBLE);
		setMessage("Votre " + getPurchaseTraitmentName(currentPurchase)
				+ " n'est toujours pas validé...");
		findViewById(R.id.cinetpay_pgs_loader).setVisibility(View.GONE);
		mHandler.postDelayed(autoCloser, TIME_AUTO_CLOSE_TIME_OUT);
		super.setOnCancelListener(listernerCallBackIfWaiting);
	}

	@Override
	public void setMessage(String message) {
		// TODO Auto-generated method stub
		message = message.replace("$type",
				getPurchaseTraitmentName(currentPurchase));
		message = Word.toSentense(message, ".");
		super.setMessage(message);
	}

	@Override
	public void onCheckComplete(PurchaseTransaction t, boolean success) {
		// TODO Auto-generated method stub
		setCancelable(true);
		findViewById(R.id.cinetpay_pgs_loader).setVisibility(View.GONE);
		if (success) {
			PaymentResponse response = t.getPaymentStatus();
			if (!response.isWaiting()) {
				ui.onPaymentComplete(response,
						Purchase.createFromPurchasable(currentPurchase));
				super.setOnCancelListener(listernerCallBackIfWaiting);
				cancel();
			} else {
				onPaymentWaiting(currentPurchase);
			}
		} else {
			setMessage(CinetPayText.NETWORK_ERROR_EXPLICIT);
			btn_verify.setVisibility(View.VISIBLE);
			super.setOnCancelListener(listernerCallBackIfWaiting);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_verify) {
			removeAutoClose();
			verifyPayment(currentPurchase);
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		btn_verify.setVisibility(View.VISIBLE);
		super.cancel();
	}

	public static String getPurchaseTraitmentName(Purchasable currentPurchase) {
		String out = "paiement";
		if (currentPurchase.getType() == Purchase.TYPE_DONATION)
			out = "don";
		else if (currentPurchase.getType() == Purchase.TYPE_BUYING)
			out = "Achat";
		return out;
	}

	private OnCancelListener listernerCallBackIfWaiting = new OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			// Log.d("waitingD_calback_wait", "executed::" + mOnCancelListener);
			ui.onPaymentComplete(PAYMENT_RESPONSE_NOTIFY_WAITING,
					Purchase.createFromPurchasable(currentPurchase));
			if (mOnCancelListener != null) {
				mOnCancelListener.onCancel(dialog);
				mOnCancelListener = null;
			}
		}
	};

	private void removeAutoClose() {
		// TODO Auto-generated method stub
		try {
			mHandler.removeCallbacks(autoCloser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Runnable autoCloser = new Runnable() {
		public void run() {
			setCancelable(true);
			btn_verify.setVisibility(View.VISIBLE);
			ui.onPaymentComplete(PaymentResponse.RESPONSE_WAITING,
					Purchase.createFromPurchasable(currentPurchase));
		};
	};
	private OnCancelListener mOnCancelListener;

	public void setOnCancelListener(
			OnCancelListener listener) {
		this.mOnCancelListener = listener;
	}
}
