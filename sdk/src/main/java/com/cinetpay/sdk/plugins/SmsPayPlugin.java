package com.cinetpay.sdk.plugins;

import istat.android.telephony.sms.Sms;
import istat.android.telephony.sms.SmsQL;
import istat.android.telephony.sms.tools.SmsSender;
import istat.android.telephony.sms.tools.SmsSender.SendCallBack;
import android.Manifest.permission;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.text.TextUtils;

import com.cinetpay.sdk.Customer;
import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.process.CinetPay.PayCallBack;
import com.cinetpay.sdk.tool.Settings;
import com.cinetpay.sdk.tool.SmsWatcher;
import com.cinetpay.sdk.tool.SmsWatcher.SmsListener;
import com.cinetpay.sdk.tool.SmsWatcher.SmsPart;
import com.cinetpay.sdk.tool.ToolKit;
import com.cinetpay.sdk.tool.ToolKit.Word;
import com.cinetpay.sdk.ui.CinetPayUI;
import com.cinetpay.sdk.ui.PayPlugin;

public final class SmsPayPlugin extends PayPlugin implements OnCancelListener {

	final static String PHONE_PREFIX_CI1 = "+225", PHONE_PREFIX_CI2 = "00225";
	public final static String PLUGIN_NAME = "smsPlugin";
	final static String CINETPAY_SMS_RESPONSE_STARTING = "CPR ";
	public final static int SMS_PAYMENT_TIME_OUT = /* 30000; */50000,
			SMS_INITIALIZATION_TIME_OUT = 8000;
	final static String PROTOCAL_SEPARATOR = "!";
	SmsWatcher mSmsWatcher;
	SmsSender mSmsSender;
	Handler timeOutHandler;
	private Runnable paymentTimeOutRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			closeSmsPaySession();
			ui.cancelCurrentProgressDialog();
			showCinetMessageDialog("Oups!\nVotre paiement est anormalement long, veuillez réessayer plus tard.");
		}

	};
	private Runnable initializationTimeOutRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ui.cancelCurrentProgressDialog();
			String applicationName = ToolKit.getAppLable(ui.getContext());
			showCinetMessageDialog("Désolé, impossible d'initialiser un paiement. avez vous accepté que l'application: "
					+ applicationName
					+ " puisse envoyer des SMS? veuillez vérifier et de réessayer. CinetPay vous remerci.");
		}

	};

	public SmsPayPlugin(CinetPayUI ui) {
		super(ui);
		mSmsWatcher = new SmsWatcher(ui.getContext());
		mSmsSender = new SmsSender(ui.getContext());
		timeOutHandler = new Handler();
	}

	@Override
	protected void onProcessPayment(final Purchasable purchase,
									final PayCallBack callBack) {
		if (!ToolKit.havePermission(ui.getContext(), permission.SEND_SMS)) {
			showCinetMessageDialog("Message pour développeur\nDésolé, la permission: "
					+ permission.SEND_SMS
					+ " n'a pas été déclarée dans votre manifest");
			ui.cancelCurrentProgressDialog();
			return;
		}
		Dialog dialog = showCinetProgressDialog("Initialisation CinetPay...",
				false);
		dialog.setOnCancelListener(this);
		dialog.setCancelable(false);
		beginSmsPaySession(purchase, callBack);
	}

	void beginSmsPaySession(final Purchasable purchase,
							final PayCallBack callBack) {
		startInitializationTimeOutWatcher();
		mSmsSender.sendSms(createPurchaseSms(purchase),
				getSmsSendCallBack(purchase, callBack));
	}

	void closeSmsPaySession() {
		if (mSmsWatcher != null) {
			mSmsWatcher.unregisterSmsListener();
		}
		timeOutHandler.removeCallbacks(paymentTimeOutRunnable);
	}

	private void startPaymentTimeOutWatcher() {
		if (timeOutHandler != null) {
			timeOutHandler.removeCallbacks(paymentTimeOutRunnable);
		} else {
			timeOutHandler = new Handler();
		}
		timeOutHandler
				.postDelayed(paymentTimeOutRunnable, SMS_PAYMENT_TIME_OUT);
	}

	private void startInitializationTimeOutWatcher() {
		if (timeOutHandler != null) {
			timeOutHandler.removeCallbacks(initializationTimeOutRunnable);
		} else {
			timeOutHandler = new Handler();
		}
		timeOutHandler.postDelayed(initializationTimeOutRunnable,
				SMS_INITIALIZATION_TIME_OUT);
	}

	private SendCallBack getSmsSendCallBack(final Purchasable purchase,
											final PayCallBack callBack) {
		// TODO Auto-generated method stub
		return new SendCallBack() {

			@Override
			public void onRadioOffFail(Sms sms) {
				// TODO Auto-generated method stub
				onEchecInitialization(sms);
				showCinetMessageDialog("Oups! erreur réseau");
			}

			@Override
			public void onBadFormedSmsFail(Sms sms) {
				onEchecInitialization(sms);
				showCinetMessageDialog("Oups! une erreur inattendue est survenue, Réessayer plus tard.");
			}

			@Override
			public void onSuccessSending(Sms sms) {
				showCinetProgressDialog("Paiement en cours...", false);
				mSmsWatcher.setSmsListener(
						createSmsListener(purchase, callBack), 2147483647);
				onSuccessInitialization(sms);
			}

			@Override
			public void onGenericFail(Sms sms) {
				// TODO Auto-generated method stub
				onEchecInitialization(sms);
				showCinetMessageDialog("Oups! une erreur inattendue est survenue, vérifier votre crédit SMS.");
			}
		};
	}

	private void onSuccessInitialization(Sms sms) {
		// TODO Auto-generated method stub
		startSmsPaySession();
	}

	private void startSmsPaySession() {
		// TODO Auto-generated method stub
		timeOutHandler.removeCallbacks(initializationTimeOutRunnable);
		startPaymentTimeOutWatcher();
		deleteSms();
	}

	private void onEchecInitialization(Sms sms) {
		ui.cancelCurrentPayDialog();
		ui.cancelCurrentProgressDialog();
		deleteSms();
	}

	private void deleteSms() {
		if (!(ToolKit.havePermission(ui.getContext(), permission.READ_SMS)))
			return;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SmsQL smsQ = new SmsQL(ui.getContext());
				smsQ.deleteSms()
						.whereAddressEqual(Settings.getMoovPhone())
						.orAdressEqual(Settings.getOrangePhone())
						.orAdressEqual(Settings.getMtnPhone())

						.orAdressEqual(
								PHONE_PREFIX_CI1 + Settings.getMoovPhone())
						.orAdressEqual(
								PHONE_PREFIX_CI1 + Settings.getOrangePhone())
						.orAdressEqual(
								PHONE_PREFIX_CI1 + Settings.getMtnPhone())

						.orAdressEqual(
								PHONE_PREFIX_CI2 + Settings.getMoovPhone())
						.orAdressEqual(
								PHONE_PREFIX_CI2 + Settings.getOrangePhone())
						.orAdressEqual(
								PHONE_PREFIX_CI2 + Settings.getMtnPhone())
                        .execute();
						//.commit();
			}
		}, 1000);

	}

	private SmsListener createSmsListener(final Purchasable purchase,
										  final PayCallBack callBack) {
		return new SmsListener() {

			@Override
			public void onReceiveSms(SmsPart sms, BroadcastReceiver receiver) {
				// TODO Auto-generated method stub
				String body = sms.body;
				if (!TextUtils.isEmpty(body)) {
					if (body.startsWith(CINETPAY_SMS_RESPONSE_STARTING)) {
						PaymentResponse response = createPaymentResponseFromSmsBody(body);
						if (response != null) {
							notifyPaymentComplete(response,
									Purchase.createFromPurchasable(purchase),
									callBack);
							if (receiver != null) {
								deleteSms();
								receiver.abortBroadcast();
							}
						}
					}
				}

			}
		};
	}

	private void notifyPaymentComplete(PaymentResponse response,
									   Purchasable purchase, final PayCallBack callBack) {
		onPaymentComplete(response, Purchase.createFromPurchasable(purchase),
				callBack);
	}

	private Sms createPurchaseSms(Purchasable purchase) {
		String phone = Settings.CINETPAY_PHONE_NUMBERS.get(purchase
				.getPaymentMethod());
		if (TextUtils.isEmpty(phone)) {
			phone = Settings.CINETPAY_PHONE_NUMBER_DEFAULT;
		}
		// phone = "5556";
		return new Sms(phone, createPurchaseSmsBody(purchase));
	}

	private String createPurchaseSmsBody(Purchasable purchase) {
		MerchantService merchant = cinetpay.getCurrentMerchant();
		Customer customer = purchase.getCustomer();
		String out = merchant.getSiteId() + PROTOCAL_SEPARATOR
				+ purchase.getAmount() + PROTOCAL_SEPARATOR
				+ customer.getPhone() + PROTOCAL_SEPARATOR
				+ customer.getOtpCode() + PROTOCAL_SEPARATOR
				+ purchase.getMetaData();
		return "CP" + (merchant.isProductionPlatform() ? "P" : "T") + " " + out
				+ " ";
	}

	private static PaymentResponse createPaymentResponseFromSmsBody(
			String smsBody) {
		String code = "1024", message = "UNKNOWN_ERROR";
		String[] splits = smsBody.split("\\s");
		if (splits.length == 2) {
			String pertinatData = splits[1];
			String[] datas = pertinatData.split(PROTOCAL_SEPARATOR);
			if (datas.length == 2) {
				code = datas[0];
				message = datas[1];
				if (!Word.isInteger(code)) {
					return null;
				}
			} else
				return null;
		} else {
			return null;
		}
		return new PaymentResponse(code, message);
	}

	@Override
	public boolean needUIValidator() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		closeSmsPaySession();
	}

}
