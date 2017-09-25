package com.cinetpay.sdk.ui;

import com.cinetpay.sdk.Customer;
import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.R;
import com.cinetpay.sdk.process.CinetPay;
import com.cinetpay.sdk.process.CinetPay.PayCallBack;
import com.cinetpay.sdk.tool.AdvancedAsyncTask;
import com.cinetpay.sdk.tool.CinetPayText;
import com.cinetpay.sdk.tool.SmsWatcher;
import com.cinetpay.sdk.tool.ToolKit;
import com.cinetpay.sdk.tool.SmsWatcher.SmsPart;
import android.Manifest.permission;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
@Deprecated
public final class TPEPayDialog extends BaseDialog implements
		View.OnClickListener, TextWatcher {
	Button btn_cinet_pay;
	// RadioButton rdb_om, rdb_momo;
	EditText edt_phone, edt_otp;
	Purchase mPurchase;
	TextView txt_amount, txt_explicit;
	static String TEXT_AMOUNT = "Montant: ", TEXT_AMOUNT_CURRENCY = " FCFA";
	protected CinetPay cinetPay;
	protected AdvancedAsyncTask<?, ?, ?> payTask;
	private SmsWatcher mSmsWatcher;
	private boolean otpWatcherEnable = true, configSaveEnable = false;
	CinetProgressDialog progresD;
	CinetMessageDialog messageD;
	private CinetPayUI mUI;
	String payMode = Purchase.PAYMENT_METHOD_UNKNOW;
	private PayCallBack mCallBack = new PayCallBack() {

		@Override
		public void onPurchaseComplete(PaymentResponse response,
									   Purchase purchase) {
			// TODO Auto-generated method stub

		}
	};

	public TPEPayDialog(Activity context, MerchantService merchant,
						Purchase purchases, CinetPayUI ui) {
		super(context, merchant);
		// TODO Auto-generated constructor stub
		mPurchase = purchases;
		cinetPay = new CinetPay(merchant);
		mUI = ui;
		initPaymentDialog();
	}

	public TPEPayDialog(Activity context, MerchantService merchant,
						PayCallBack payCallBack, Purchase purchases, CinetPayUI ui) {
		super(context, merchant);
		// TODO Auto-generated constructor stub
		mPurchase = purchases;
		cinetPay = new CinetPay(merchant);
		mCallBack = payCallBack;
		mUI = ui;
		initPaymentDialog();
	}

	private void initPaymentDialog() {
		// CinetPayUISecurity.checkTPEUIIntegrity(this.getContext());
		progresD = new CinetProgressDialog(getContext());
		messageD = new CinetMessageDialog(getContext());
		progresD.setMessage(CinetPayText.pay_in_process);
	}

	@Override
	void onPrepar() {
		setContentView(R.layout.cinetpay_dialog);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		if (mSmsWatcher != null)
			mSmsWatcher.unregisterSmsListener();
		super.cancel();
	}

	@Override
	void linkToXml() {
		btn_cinet_pay = (Button) findViewById(R.id.cinetpay_pay);
		edt_phone = (EditText) findViewById(R.id.cinetpay_edt_phone);
		edt_otp = (EditText) findViewById(R.id.cinetpay_edt_otp);
		txt_amount = (TextView) findViewById(R.id.cinetpay_txt_amound_to_pay);
		txt_explicit = (TextView) findViewById(R.id.cinetpay_txt_explicit);
	}

	@Override
	void onInit() {
		if (mPurchase != null && !TextUtils.isEmpty(mPurchase.getAmount()))
			txt_amount.setText(/* TEXT_AMOUNT + */mPurchase.getAmount()
					+ mPurchase.getCurrency());
		if (isPhoneSaveEnable())
			edt_phone.setText(ToolKit.Memory.LoadPreferences(getContext(),
					"config", "phone", ""));
	}

	@Override
	void addListener() {

		btn_cinet_pay.setOnClickListener(this);
		edt_otp.addTextChangedListener(this);
		edt_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
									  int arg3) {
				// TODO Auto-generated method stub
				boolean phoneOk = phoneFieldIsOk();
				btn_cinet_pay.setEnabled(phoneOk);
				int maxleght = 13;
				int color = 0xFFe0001b;
				if (phoneOk) {
					s = getPhone();
					maxleght = s.length();
					// 0xFFabaaa5;
					if (ToolKit.Control.isPhoneCinetPayCompatible(s.toString())) {
						mPurchase.setPaymentMethod(ToolKit.Control.isMtnPhone(s
								.toString()) ? Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY
								: Purchase.PAYMENT_METHOD_ORANGE_MONEY);

						color = mPurchase.getPaymentMethod().equals(
								Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY) ? 0xFFffc201
								: 0xFFf68121;
						txt_explicit
								.setText(mPurchase.getPaymentMethod().equals(
										Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY) ? "MTN Mobile Money"
										: "Orange Money");
					} else {
						mPurchase.setPaymentMethod(Purchase.PAYMENT_METHOD_UNKNOW);
						txt_explicit
								.setText("CinetPay numéro non pris en charge.");
						color = 0xFF3dac62;
					}
				} else {
					color = 0xFF3dac62;
					txt_explicit.setText("CinetPay");
				}
				txt_explicit.setTextColor(color);
				edt_phone.setTextColor(color);
				edt_phone
						.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								maxleght) });
				edt_otp.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						mPurchase.getPaymentMethod().equals(
								Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY) ? 5 : 4) });
				btn_cinet_pay.setEnabled((mPurchase.getPaymentMethod().equals(
						Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY) && edt_otp
						.getText().toString().length() == 5)
						|| (mPurchase.getPaymentMethod().equals(
						Purchase.PAYMENT_METHOD_ORANGE_MONEY) && edt_otp
						.getText().toString().length() == 4));
				if (mPurchase.getPaymentMethod().equals(
						Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY))
					edt_otp.setHint("Composer le *133*4*1#");
				if (mPurchase.getPaymentMethod().equals(
						Purchase.PAYMENT_METHOD_ORANGE_MONEY))
					edt_otp.setHint("Composer le #144*621*CODE_SECRET#");

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.cinetpay_txt_amound_to_pay) {
			toast("Marchant: " + cinetPay.getCurrentMerchant().getName()
					+ "\nMontant: " + mPurchase.getAmount() + " "
					+ mPurchase.getCurrency() + "\nReference: "
					+ mPurchase.getReference());
		}
		if (id == R.id.cinetpay_pay) {

			if (ToolKit.isNetworkConnected(getContext())) {
				processPayment();
				if (isPhoneSaveEnable())
					ToolKit.Memory.SavePreferences(getContext(), "config",
							"phone", getPhone(), 0);
			} else {
				ToolKit.vibrate(mContext, 90);
				new CinetMessageDialog(getContext()).show(CinetPay.getCinetPayMessage("NETWORK_ERROR_EXPLICIT"), false);
			}

		}

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
								  int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (phoneFieldIsOk()) {
			if (mPurchase.getPaymentMethod().equals(Purchase.PAYMENT_METHOD_ORANGE_MONEY)) {
				btn_cinet_pay
						.setEnabled(edt_otp.getText().toString().length() == 4);
			} else if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY)) {
				btn_cinet_pay
						.setEnabled(edt_otp.getText().toString().length() == 5);
			}
		}
	}

	/**
	 * Spécifier un callBack ({@link PayCallBack}) de rappel une fois le
	 * paiement effectué.
	 *
	 * @param mCallBack
	 */
	public void setOnPayCallBack(PayCallBack mCallBack) {
		this.mCallBack = mCallBack;
	}

	/**
	 * Si une tâche de paiement a déja été lancée, permet de stoper la tâche en
	 * cours
	 */
	public void abordCurrentPayTask() {
		if (payTask != null)
			payTask.cancel(true);
	}

	/**
	 * Permet d'obtenir la tâche de paiement en cours si il en existe une déja
	 * lancée.
	 *
	 * @return tâche en cours. elle sera null si il n'en existe pas.
	 */
	public AdvancedAsyncTask<?, ?, ?> getCurrentPayTask() {
		return payTask;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		if (otpWatcherEnable
				&& ToolKit.havePermission(getContext(), permission.RECEIVE_SMS))
			registerSmsWatcher();
		super.show();
	}

	private void toast(String po) {
		Toast.makeText(this.getContext(), po, Toast.LENGTH_LONG).show();
	}

	private void processPayment() {
		progresD.show();
		this.cancel();
		mPurchase
				.setPaymentMethod(payMode == Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY ? Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY
						: Purchase.PAYMENT_METHOD_ORANGE_MONEY);
		mPurchase.setCustomer(new Customer(ToolKit.Control
				.extractPhone(getPhone()), getOtp()));
		mUI.beginSafeMode();
		payTask = cinetPay.doAsyncPay(mPurchase, new PayCallBack() {
			@Override
			public void onPurchaseComplete(PaymentResponse response,
										   Purchase purchase) {
				messageD.show(CinetPay.getCinetPayMessage(response.getMessage()), false);
				progresD.cancel();
				mUI.cancelSafeMode();
				if (mCallBack != null)
					mCallBack.onPurchaseComplete(response, purchase);

			}
		}, mUI);

		progresD.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				payTask.cancel(true);
			}
		});
	}

	private boolean payFieldIsOk() {
		return (payMode == Purchase.PAYMENT_METHOD_ORANGE_MONEY
				&& getOtp().length() >= 4 || payMode == Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY
				&& getOtp().length() >= 5);
	}

	private void setPayModeEnable(boolean value) {
		if (value) {
			btn_cinet_pay.setText("Payer");
			btn_cinet_pay.setEnabled(true);
		} else {
			if (ToolKit.Control.isPhoneCinetPayCompatible(getPhone())) {
				switchToOM();
			} else if (ToolKit.Control.isMtnPhone(getPhone())) {
				switchToMOMO();
			} else {
				switchToUnknow();
			}

		}
	}

	private void switchToOM() {
		btn_cinet_pay.setText("Entrer Code OTP de l'achéteur");
		txt_explicit.setText("Orange Money");
		setDialogTextColor(0xff3dac62);
	}

	private void switchToMOMO() {
		txt_explicit.setText("MTN Mobile Money");
		btn_cinet_pay.setText("Entrer Token de l'achéteur");
		setDialogTextColor(0xFFffc201);
	}

	private void switchToUnknow() {
		btn_cinet_pay.setText("Numéro non pris en charge par CinetPay");
		txt_explicit.setText("Numéro non pris en charge par CinetPay");
		setDialogTextColor(0xff3dac62);
	}

	private void setDialogTextColor(int color) {
		edt_otp.setTextColor(0xff3dac62);
		edt_phone.setTextColor(color);
		txt_explicit.setTextColor(color);
	}

	public String getPhone() {
		return edt_phone.getText().toString().replace(" ", "").replace("-", "")
				.replaceFirst("^(\\+\\d{3})", "")
				.replaceFirst("^(00\\d{3})", "");
	}

	private String getOtp() {
		return edt_otp.getText().toString();
	}

	private void registerSmsWatcher() {
		mSmsWatcher = new SmsWatcher(this.getContext());
		mSmsWatcher.setSmsListener(new SmsWatcher.SmsListener() {

			@Override
			public void onReceiveSms(SmsPart sms, BroadcastReceiver receiver) {
				// TODO Auto-generated method stub
				String otp_code = payMode == Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY ? ToolKit
						.getOtpCodeMOMO(sms.body) : ToolKit
						.getOtpCodeOM(sms.body);
				if (!TextUtils.isEmpty(otp_code))
					edt_otp.setText(otp_code);
				// edt_phone.requestFocus();
				ToolKit.closeKeyboard(mContext);
			}
		}, 99999);
	}

	/**
	 * permet d'activer ou pas le captureur automatique de code OTP. celui ci
	 * permet de remplire automatiquement la zone Code OTP du dialogue de
	 * paiement une fois qu'un SMS contenant un code OTP est reçu.
	 *
	 * @param otpWatcherEnable
	 *            l'etat du watcher.
	 */
	public void setOtpWatcherEnable(boolean otpWatcherEnable) {
		this.otpWatcherEnable = otpWatcherEnable;
	}

	/**
	 * Obtenir l'état de l'ecouteur de code OTP de l'actuel dialog de paiement
	 * {@link TPEPayDialog}
	 *
	 * @return etat du OTP watcher
	 */
	public boolean isOtpWatcherEnable() {
		return otpWatcherEnable;
	}

	public void setSaveConfigEnable(boolean configSaveEnable) {
		this.configSaveEnable = configSaveEnable;
	}

	public boolean isPhoneSaveEnable() {
		return configSaveEnable;
	}

	private boolean phoneFieldIsOk() {
		return isValidPhone(getPhone());
		// getPhone().matches("^[^(00)]\\d{7}")
		// || getPhone().matches("^[^\\+]\\d{7}")
		// || getPhone().matches("^(\\+\\d{11})")
		// || getPhone().matches("^(00\\d{11})");
	}

	private boolean isValidPhone(String phone) {
		return phone.matches("^[^(00)]\\d{7}")
				|| phone.matches("^[^\\+]\\d{7}")
				|| phone.matches("^(\\+\\d{11})")
				|| phone.matches("^(00\\d{11})");
	}

}
