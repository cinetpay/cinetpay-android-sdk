package com.cinetpay.sdk.ui;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cinetpay.sdk.Customer;
import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.R;
import com.cinetpay.sdk.process.CinetPay;
import com.cinetpay.sdk.process.CinetPay.PayCallBack;
import com.cinetpay.sdk.tool.AdvancedAsyncTask;
import com.cinetpay.sdk.tool.CinetPayText;
import com.cinetpay.sdk.tool.HelloGoodies;
import com.cinetpay.sdk.tool.Settings;
import com.cinetpay.sdk.tool.SmsWatcher;
import com.cinetpay.sdk.tool.ToolKit;
import com.cinetpay.sdk.tool.SmsWatcher.SmsPart;


public final class PayUI {
	public final static int STATE_NO_STARTED = -2, STATE_BEGGIN = -1,
			STATE_USER_ENTRY_PHONE = 0, STATE_USER_ENTRY_OTP = 1,
			STATE_PROCESSING = 2, STATE_FINISH = 3;
	public final static String SHARED_HISTORIC_FILE = "cinetpay_historic",
			SHARED_PHONE_HISTORIC = "phone_hist";
	final static int NUMBER_GOODIES_CHECK_COUNT = 10;
	static MerchantService MERCHANT;
	static Activity CONTEXT;
	Purchasable mPurchase;
	InitDialog mInitDialog;
	PayDialog mPayDialog;
	boolean userAgreement = false;
	static CinetPayUI UI;
	private CinetPay cinetPay;
	private AdvancedAsyncTask<?, ?, ?> payTask;
	private boolean otpWatcherEnable = true;
	PayPlugin mPlugin;
	static PayUI instance;

	private PayCallBack mCallBack;

	static PayUI getInstance(CinetPayUI ui) {
		return new PayUI(ui);
	}

	static Dialog displayDialogExclamation(String... args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);
		// ((Activity)context).getWindowManager().
		// builder.setIcon(R.drawable.icon);
		builder// .setTitle(args[0])
				.setIcon(R.drawable.cinetpay_icon_mini)
				.setMessage(args[1])
				.setCancelable(false)
				.setPositiveButton(args[2],
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									dialog.cancel();
								} catch (Exception e) {
								}
							}
						});
		AlertDialog alert = builder.create();
		// if(!((Activity)(context)).isDestroyed())
		try {
			alert.show();
		} catch (Exception e) {
		}
		return alert;
	}

	private PayUI beginPayment(Purchasable purchase, PayPlugin plugin) {
		userAgreement = false;
		mPlugin = plugin;
		mPurchase = purchase;
		if (TextUtils.isEmpty(mPurchase.getDescription())) {
			mPurchase.setDescription(getPrefixDefaultTitle()
					+ cinetPay.getCurrentMerchant().getName());
		}
		switchPayMode();
		return this;
	}

	PayUI beginPayment(PayPlugin plugin, Purchasable purchase,
					   PayCallBack callBack) {
		mCallBack = callBack;
		return beginPayment(purchase, plugin);

	}

	Dialog showDialogExclamation(String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(CONTEXT);
		String text_s = "";
		if (mPurchase.getPaymentMethod().equals(
				Purchase.PAYMENT_METHOD_ORANGE_MONEY))
			text_s = "<b>CONSIGNES POUR PAYER VIA ORANGE MONEY :</b><br><br><p style=\"font-size:17px\"> <b>1°)</b> Cliquer sur le bouton <b>OK</b>, entrez votre <b>CODE SECRET</b> Orange Money en lieu et place des chiffres <b>0000</b> dans la chaine suivante <b>"
					+ Settings.getOrangeUssdSyntax()
					+ "</b><br><br> <b>2°)</b> Une fois votre <b>CODE SECRET</b> Orange Money entré veuillez lancer l'appel pour obtenir le <b>code OTP</b> (Quatre (4) chiffres).<br><br> <b>3°)</b> Une fois le <b>code OTP</b> obtenu, veuillez le relever ou le retenir puis le saisir dans la zone de texte du <b>code OTP</b> et cliquez sur le bouton <b>Continuer</b> </p>";
		if (mPurchase.getPaymentMethod().equals(
				Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY)) {
			text_s = "<b>CONSIGNES POUR PAYER VIA MTN MOBILE MONEY :</b><br><br><p style=\"font-size:17px\"><b>1°)</b> En cliquant sur le bouton <b>OK</b>, vous allez obtenir un <b>Token</b> qui est un code de cinq (5) chiffres. <br><br> <b>2°)</b> Une fois le <b>Token</b> obtenu, veuillez relever ou le retenir puis le saisir dans la zone de texte du <b>Token</b> et cliquez sur le bouton <b>Continuer</b> </p>";
		}
		TextView textV = new TextView(CONTEXT);
		textV.setText(Html.fromHtml(text_s));
		textV.setPadding(10, 10, 10, 10);
		textV.setTextSize(17);
		textV.setBackgroundColor(0xFFf0f0f0);
		textV.setScrollContainer(true);
		ScrollView scrol = new ScrollView(CONTEXT);
		scrol.addView(textV);
		builder.setView(scrol);
		builder
				// .setTitle(title).setIcon(R.drawable.cinetpay_icon_mini)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (mPurchase.getPaymentMethod().equals(
								Purchase.PAYMENT_METHOD_ORANGE_MONEY)) {
							ToolKit.phoneDial(Settings.getOrangeUssdSyntax(),
									CONTEXT);
							toast("Remplacer 0000 par votre CODE SECRET et lancer l'Appel avec votre Puce ORANGE pour continuer. Merci.");
						}
						if (mPurchase.getPaymentMethod().equals(
								Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY)) {
							String call = Settings.getMtnUssdSyntax();
							if (isCurrentPhoneAsMine()) {
								ToolKit.phoneCall(call, CONTEXT);
							} else {
								toast("Lancer l'Appel avec votre Puce MTN pour continuer. Merci.");
								ToolKit.phoneDial(call, CONTEXT);
							}
						} else
							dialog.cancel();
					}
				})
				.setNegativeButton("Annuler",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		AlertDialog alert = builder.create();
		// if(!((Activity)(context)).isDestroyed())
		try {
			alert.show();
		} catch (Exception e) {
		}
		return alert;
	}

	boolean hasUserAgreement() {
		return userAgreement;
	}

	class InitDialog extends BasePayUIDialog {
		EditText edt_phone;
		Button btn_continue;
		TextView txt_amount;

		@Override
		void linkToXml() {
			// TODO Auto-generated method stub
			setContentView(R.layout.cinetpay_dialog_phone_input);
			edt_phone = (EditText) findViewById(R.id.cinetpay_edt_phone);
			btn_continue = (Button) findViewById(R.id.cinetpay_btn_continue);
			txt_amount = (TextView) findViewById(R.id.cinetpay_txt_amount);
			super.linkToXml();
		}

		@Override
		void onInit() {
			// TODO Auto-generated method stub
			super.onInit();
		}

		@Override
		public void show() {
			// TODO Auto-generated method stub
			super.show();
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if (mPayDialog != null && !mPayDialog.isShowing()) {
				onPaymentFinish();
			}
		}

		@Override
		void refreshInflation() {
			// TODO Auto-generated method stub
			super.refreshInflation();
			txt_amount.setText(mPurchase.getAmount() + mPurchase.getCurrency());

		}

		@Override
		void addListener() {
			// TODO Auto-generated method stub
			super.addListener();
			View.OnClickListener oncl = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CinetPayUISecurity.checkPayUIInitIntegrity(mInitDialog);
					if (v.getId() == R.id.cinetpay_btn_continue) {
						onContinuePressed();
					}
					if (v.getId() == R.id.cinetpay_edt_phone) {
						if (isDetailVisible())
							showDetail(false);
					}

				}
			};
			edt_phone.setOnClickListener(oncl);
			btn_continue.setOnClickListener(oncl);
			edt_phone.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
										  int before, int count) {
					// TODO Auto-generated method stub
					boolean phoneOk = phoneFieldIsOk();
					btn_continue.setEnabled(phoneOk);
					int maxleght = 13;
					if (phoneOk) {
						s = getPhone();
						maxleght = s.length();
						int color = 0xFFe0001b;// 0xFFabaaa5;
						if (ToolKit.Control.isPhoneCinetPayCompatible(s
								.toString())) {
							if (ToolKit.Control.isMtnPhone(s.toString())) {
								mPurchase
										.setPaymentMethod(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY);
							} else if (ToolKit.Control.isOrangePhone(s
									.toString())) {
								mPurchase
										.setPaymentMethod(Purchase.PAYMENT_METHOD_ORANGE_MONEY);
							} else if (ToolKit.Control.isMoovPhone(s.toString())) {
								mPurchase
										.setPaymentMethod(Purchase.PAYMENT_METHOD_MOOV_FLOOZ);
							}
							color = 0xFF3dac62;
						} else {
							mPurchase
									.setPaymentMethod(Purchase.PAYMENT_METHOD_UNKNOW);
						}
						txt_pay_way
								.setVisibility(mPurchase.getPaymentMethod()
										.equals(Purchase.PAYMENT_METHOD_UNKNOW) ? View.VISIBLE
										: View.GONE);

						txt_amount.setTextColor(color);
						edt_phone.setTextColor(color);
						refreshInflation();
					} else {
						int color = 0xFF3dac62;
						txt_amount.setTextColor(color);
						edt_phone.setTextColor(color);
					}
					edt_phone
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
									maxleght) });
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
											  int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if (isDetailVisible())
						showDetail(false);
				}
			});
		}

		private void onContinuePressed() {
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_UNKNOW)) {
				ToolKit.vibrate(CONTEXT, 90);
				displayDialogExclamation(CinetPayText.LABEL_OUPS,
						CinetPayText.PHONE_NOT_SUPPORTED, CinetPayText.LABEL_OK);
			} else {
				mPurchase.setCustomer(new Customer(getPhone(), ""));
				mPayDialog.show();
			}
			ToolKit.closeKeyboard(CONTEXT);
		}

		public String getPhone() {
			return edt_phone.getText().toString().replace(" ", "")
					.replace("-", "").replaceFirst("^(\\+\\d{3})", "")
					.replaceFirst("^(00\\d{3})", "");
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

	class PayDialog extends BasePayUIDialog {
		EditText edt_otp;
		SmsWatcher mSmsWatcher;
		Button btn_continue;
		TextView txt_phone, txt_pay_way_view, txt_otp;

		public PayDialog() {
			// TODO Auto-generated constructor stub
		}

		@Override
		void linkToXml() {
			// TODO Auto-generated method stub
			setContentView(R.layout.cinetpay_dialog_pay);
			edt_otp = (EditText) findViewById(R.id.cinetpay_edt_otp);
			btn_continue = (Button) findViewById(R.id.cinetpay_pay);
			txt_phone = (TextView) findViewById(R.id.cinetpay_txt_phone);
			txt_otp = (TextView) findViewById(R.id.cinetpay_txt_otp);
			txt_pay_way_view = (TextView) findViewById(R.id.cinetpay_txt_pay_way_view);
			super.linkToXml();
		}

		@Override
		void onInit() {
			// TODO Auto-generated method stub
			super.onInit();
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MOOV_FLOOZ)) {
				edt_otp.setText("0000");
			}
		}

		@Override
		public void show() {
			// TODO Auto-generated method stub
			super.show();
			if (otpWatcherEnable
					&& ToolKit.havePermission(getContext(),
					permission.RECEIVE_SMS)) {
				if (edt_otp.isEnabled()) {
					registerSmsWatcher();
				}
			}

		}

		void unregisterSmsWatcher() {
			if (mSmsWatcher != null)
				mSmsWatcher.unregisterSmsListener();
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if (mInitDialog != null && !mInitDialog.isShowing()) {
				onPaymentFinish();
			}
		}

		@Override
		void refreshInflation() {
			// TODO Auto-generated method stub
			super.refreshInflation();// e0001b
			txt_pay_way_view.setText(CinetPay.getPaymentMap(CONTEXT).get(
					mPurchase.getPaymentMethod()));
			int color = 0xFFf68121;
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY)) {
				mPurchase
						.setPaymentMethod(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY);
				txt_otp.setVisibility(View.VISIBLE);
				edt_otp.setVisibility(View.VISIBLE);
				color = 0xFFffc201;
			} else if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_ORANGE_MONEY)) {
				mPurchase
						.setPaymentMethod(Purchase.PAYMENT_METHOD_ORANGE_MONEY);
				txt_otp.setVisibility(View.VISIBLE);
				edt_otp.setVisibility(View.VISIBLE);
				color = 0xFFf68121;
			} else if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MOOV_FLOOZ)) {
				color = 0xFF3dac62;
				txt_otp.setVisibility(View.GONE);
				edt_otp.setVisibility(View.GONE);
				mPurchase.setPaymentMethod(Purchase.PAYMENT_METHOD_MOOV_FLOOZ);
			}

			txt_pay_way_view.setTextColor(color);
			txt_phone.setTextColor(color);
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY)
					&& getOtp().length() < 5
					|| mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_ORANGE_MONEY)
					&& getOtp().length() < 4)
				btn_continue
						.setText(mPurchase.getPaymentMethod().equals(
								Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY) ? "Obtenir un Token"
								: "Obtenir code OTP");
			txt_phone.setText(mPurchase.getCustomer().getPhone());
			txt_pay_way_view.setText(CinetPay.getPaymentMap(CONTEXT).get(
					mPurchase.getPaymentMethod()));
			edt_otp.setHint(mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_ORANGE_MONEY) ? "Code OTP..."
					: "Token...");
			txt_otp.setText(mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_ORANGE_MONEY) ? "Code OTP:"
					: "Token:");

		}

		@Override
		void addListener() {
			// TODO Auto-generated method stub
			super.addListener();
			View.OnClickListener oncl = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CinetPayUISecurity.checkPayUIPayIntegrity(mPayDialog);
					if (v.getId() == R.id.cinetpay_pay) {
						mPurchase.getCustomer().setOtpCode(getOtp());
						if (otpFieldIsOk()) {
							userAgreement = true;
							processPayment();
							userAgreement = false;
						} else {
							if (mPurchase.getPaymentMethod().equals(
									Purchase.PAYMENT_METHOD_ORANGE_MONEY))

								showDialogExclamation("CODE OTP");
							if (mPurchase.getPaymentMethod().equals(
									Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY))
								showDialogExclamation("TOKEN");

						}
					}
					if (v.getId() == R.id.cinetpay_txt_pay_way_view) {
						switchPayMode();

					}
					if (v.getId() == R.id.cinetpay_edt_otp) {
						if (isDetailVisible())
							showDetail(false);

					}
				}

			};
			btn_continue.setOnClickListener(oncl);
			edt_otp.setOnClickListener(oncl);
			edt_otp.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
										  int before, int count) {
					// TODO Auto-generated method stub
					boolean otpOk = otpFieldIsOk();
					int maxleght = 5;
					if (otpOk) {
						maxleght = s.length();
						btn_continue.setText(getPrefixPayButton()
								+ mPurchase.getAmount()
								+ mPurchase.getCurrency());

					} else {
						btn_continue
								.setText(mPurchase.getPaymentMethod().equals(
										Purchase.PAYMENT_METHOD_ORANGE_MONEY) ? "Obtenir Code OTP"
										: "Obtenir un Token");
					}
					edt_otp.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							maxleght) });
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
											  int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if (isDetailVisible())
						showDetail(false);
				}
			});

		}

		private String getOtp() {
			return edt_otp.getText().toString();
		}

		private boolean otpFieldIsOk() {
			return isValidOTP(getOtp());
		}

		private boolean isValidOTP(String otp) {
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_ORANGE_MONEY)) {
				return otp.matches("\\d{4}");
			}
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY)) {
				return otp.matches("\\d{5}");
			}
			if (mPurchase.getPaymentMethod().equals(
					Purchase.PAYMENT_METHOD_MOOV_FLOOZ)) {
				return true;
			}
			return false;
		}

		private void registerSmsWatcher() {
			mSmsWatcher = new SmsWatcher(this.getContext());
			mSmsWatcher.setSmsListener(new SmsWatcher.SmsListener() {

				@Override
				public void onReceiveSms(SmsPart sms, BroadcastReceiver receiver) {
					// TODO Auto-generated method stub
					String otp_code = mPurchase.getPaymentMethod().equals(
							Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY) ? ToolKit
							.getOtpCodeMOMO(sms.body) : ToolKit
							.getOtpCodeOM(sms.body);
					if (!TextUtils.isEmpty(otp_code))
						edt_otp.setText(otp_code);
					ToolKit.closeKeyboard(CONTEXT);
					rebootCurrentActivity();
				}
			}, 99999);
		}

		private void rebootCurrentActivity() {
			try {
				Context context = CONTEXT.createPackageContext(
						CONTEXT.getPackageName(),
						Context.CONTEXT_IGNORE_SECURITY);
				Intent intent = new Intent(context, CONTEXT.getClass());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(CONTEXT, "ERROR::" + e, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void cancel() {
			// TODO Auto-generated method stub

			if (mPayDialog.edt_otp.isEnabled()) {
				if (mSmsWatcher != null)
					mSmsWatcher.unregisterSmsListener();
			} else {
				if (mInitDialog != null && mInitDialog.isShowing())
					mInitDialog.cancel();
			}
			super.cancel();
		}

	}

	abstract class BasePayUIDialog extends BaseDialog implements
			OnCancelListener {
		TextView txt_merchant, txt_pay_way, txt_description, txt_title;
		View inflateLayout;
		ImageView inflater, img_merchant_inflater;
		private final String HEADER_SERVICE = "Service: ";

		public BasePayUIDialog() {
			super(PayUI.CONTEXT, PayUI.MERCHANT);
			// TODO Auto-generated constructor stub
		}

		@Override
		void onPrepar() {
			// TODO Auto-generated method stub
			// HelloGoodies.applyGoodies(findViewById(R.id.image_cinetpay), 10,
			// R.string.cinetpay_goodies);
		}

		@Override
		void linkToXml() {
			// TODO Auto-generated method stub
			inflater = (ImageView) findViewById(R.id.cinetpay_inflate_deflate);
			img_merchant_inflater = (ImageView) findViewById(R.id.cinetpay_img_merchant_inflater);
			inflateLayout = findViewById(R.id.cinetpay_lyt_inflation);
			txt_merchant = (TextView) findViewById(R.id.cinetpay_txt_merchant_name);
			txt_pay_way = (TextView) findViewById(R.id.cinetpay_txt_pay_way);
			txt_description = (TextView) findViewById(R.id.cinetpay_txt_pay_description);
			txt_title = (TextView) findViewById(R.id.cinetpay_txt_title);
		}

		@Override
		void onInit() {
			// TODO Auto-generated method stub
			HelloGoodies.applyGoodies(findViewById(R.id.cinetpay_cinetpay_image),
					NUMBER_GOODIES_CHECK_COUNT, CinetPayText.cinetpay_goodies);
			if (mMerchant.isValidMerchant()) {
				refreshInflation();
			} else {
				img_merchant_inflater.setVisibility(View.INVISIBLE);
				txt_merchant.setVisibility(View.INVISIBLE);
				if (TextUtils.isEmpty(mPurchase.getReference())) {
					inflater.setVisibility(View.INVISIBLE);
				}
			}
		}

		void refreshInflation() {
			MerchantService service = cinetPay.getCurrentMerchant();
			img_merchant_inflater
					.setImageResource(R.drawable.cinetpay_less);
			txt_merchant.setText(HEADER_SERVICE
					+ ToolKit.Word.begginByUperCase(service.getName())
					+ "\nEmail: " + service.getInfo().getEmail()
					+ "\nLocalisation: " + service.getInfo().getCountry()
					+ ", " + service.getInfo().getRegion() + "\nDescription: "
					+ service.getInfo().getDescription());
			txt_pay_way.setText("Moyen de paiement: "
					+ CinetPay.getPaymentMap(CONTEXT).get(
					mPurchase.getPaymentMethod()));
			txt_description
					.setText((!TextUtils.isEmpty(mPurchase.getTitle()) ? mPurchase
							.getTitle() + "\n"
							: "Description: ")
							+ ToolKit.Word.toSentense(
							mPurchase.getDescription(), ".")
							+ "\n\nRéférence: " + mPurchase.getReference());
			txt_description
					.setVisibility(mPurchase.getDescription().length() > 0 ? View.VISIBLE
							: View.GONE);
			String title = ToolKit.Word.ShortWord(
					ToolKit.Word.begginByUperCase(mPurchase.getTitle().trim()),
					Purchase.MAX_LENGHT_TITLE);
			title = TextUtils.isEmpty(title) ? ToolKit.Word.ShortWord(
					getPrefixDefaultTitle()
							+ cinetPay.getCurrentMerchant().getName(),
					Purchase.MAX_LENGHT_TITLE) : title;
			txt_title.setText(title);
			switchServiceDetails();
		}

		@Override
		public void show() {
			// TODO Auto-generated method stub
			super.show();
			refreshInflation();
		}

		@Override
		void addListener() {
			// TODO Auto-generated method stub
			setOnCancelListener(this);
			View.OnClickListener onclick = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (v.getId() == R.id.cinetpay_inflate_deflate) {
						showDetail(!isDetailVisible());
					}
					if (v.getId() == R.id.cinetpay_txt_pay_way) {
						switchPayMode();
					}
					if (v.getId() == R.id.cinetpay_img_merchant_inflater) {
						ToolKit.vibrate(CONTEXT, 90);
						switchServiceDetails();
					}

				}
			};
			inflater.setOnClickListener(onclick);
			img_merchant_inflater.setOnClickListener(onclick);
			// txt_pay_way.setOnClickListener(onclick);
		}

		protected void switchServiceDetails() {
			showServiceDetails(!isServiceDetailVisible());
		}

		protected void showServiceDetails(boolean visible) {

			if (!visible) {
				deflateMarchantDetails();
			} else {
				inflateMarchantDetails();
			}
		}

		private void inflateMarchantDetails() {
			MerchantService service = cinetPay.getCurrentMerchant();
			img_merchant_inflater
					.setImageResource(R.drawable.cinetpay_less);
			txt_merchant.setText(HEADER_SERVICE
					+ ToolKit.Word.begginByUperCase(service.getName())
					+ "\nEmail: " + service.getInfo().getEmail()
					+ "\nLocalisation: " + service.getInfo().getCountry()
					+ ", " + service.getInfo().getRegion() + "\nDescription: "
					+ service.getInfo().getDescription());
			txt_merchant.setBackgroundColor(0xFFe0e0e0);
			/*
			 * cpm_site_id, id_serv, id_clt_fk, nom_serv = "MARCHANT CINETPAY",
			 * description_serv, pays_serv, ville_serv, email_serv, created_at,
			 * updated_at, activated_at, solde_serv, credit_serv, debit_serv
			 */
		}

		private void deflateMarchantDetails() {
			img_merchant_inflater
					.setImageResource(R.drawable.cinetpay_more);
			txt_merchant.setText(HEADER_SERVICE
					+ ToolKit.Word.begginByUperCase(cinetPay
					.getCurrentMerchant().getName()));
			txt_merchant.setBackgroundColor(0x00000000);
		}

		protected void showDetail(boolean value) {
			ToolKit.closeKeyboard(CONTEXT);
			ToolKit.vibrate(CONTEXT, 90);
			inflateLayout.setVisibility(value ? View.VISIBLE : View.GONE);
			inflater.setImageResource(value ? R.drawable.cinetpay_deflate
					: R.drawable.cinetpay_inflate);
			refreshInflation();
		}

		protected boolean isDetailVisible() {
			return inflateLayout.getVisibility() == View.VISIBLE;
		}

		protected boolean isServiceDetailVisible() {
			return !txt_merchant.getText().equals(
					HEADER_SERVICE
							+ ToolKit.Word.begginByUperCase(cinetPay
							.getCurrentMerchant().getName()));
		}

	}

	/**
	 * Stoper la séquence de paiement en cour si il en existe une.(NB: une
	 * séquence de paiement est ensemble de dialogue, d'inteface et d'actions
	 * utilisateur)
	 */
	public void abordCurrentPayment() {
		if (mInitDialog != null)
			mInitDialog.cancel();
		if (mPayDialog != null)
			mPayDialog.cancel();
		UI.cancelCurrentMessageDialog();
		UI.cancelCurrentProgressDialog();
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

	private PayUI(CinetPayUI ui) {
		UI = ui;
		CONTEXT = ui.mActivity;
		MERCHANT = ui.mMerchant;
		cinetPay = ui.mCinetPay;
		mInitDialog = new InitDialog();
		mPayDialog = new PayDialog();
		instance = this;
	}

	private void processPayment() {
		mInitDialog.setOnCancelListener(null);
		mPayDialog.setOnCancelListener(null);
		mInitDialog.cancel();
		mPayDialog.cancel();
		UI.showCinetProgressDialog(CinetPayText.pay_in_process).setCancelable(
				false);
		mPayDialog.unregisterSmsWatcher();
		if (mPayDialog.mSmsWatcher != null) {
			mPayDialog.mSmsWatcher.unregisterSmsListener();
		}
		if (mPlugin != null) {
			mPlugin.onProcessPayment(mPurchase, mCallBack);
		} else {
			processInternetPayment();
		}

	}

	private void processInternetPayment() {
		payTask = cinetPay.doAsyncPay(mPurchase, new PayCallBack() {
			@Override
			public void onPurchaseComplete(PaymentResponse response,
										   Purchase purchase) {
				// TODO Auto-generated method stub
				onPaymentComplete(response, purchase);
			}

		}, UI);
		UI.currentProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (payTask != null)
					payTask.cancel(true);
				if (mPayDialog.isShowing())
					mPayDialog.registerSmsWatcher();
			}
		});
	}

	private void onPaymentFinish() {
		// TODO Auto-generated method stub
		instance = null;
		UI.restorOrientation();
	}

	void onPaymentComplete(PaymentResponse response, Purchase purchase) {
		// Log.d("cinetpay_complete_pay_has_callback", "" + mCallBack);
		Dialog dialog = createResponseDialog(response, purchase);
		if (dialog == null) {
			onPaymentFinish();
		} else {
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					onPaymentFinish();
				}

			});
		}
		// save user phone number
		if (purchase != null && purchase.getCustomer() != null
				&&! TextUtils.isEmpty(purchase.getCustomer().getPhone())) {
			Settings.appendCustomerPhone(UI.getContext(), purchase.getCustomer()
					.getPhone());
		}
		UI.cancelCurrentProgressDialog();
		mPayDialog.cancel();
		handleCinetPayHistoric(response, purchase);

	}

	private Dialog createResponseDialog(PaymentResponse response,
										Purchase purchase) {
		// TODO Auto-generated method stub
		// Log.d("create_response_dialog", "" + response);
		Dialog dialog = null;
		String message = createResponseMessage(response, purchase);
		if (response.isWaiting()) {
			dialog = UI.showCinetWaitingDialog(message, purchase);
		} else {
			if (response == CinetWaitingDialog.PAYMENT_RESPONSE_NOTIFY_WAITING) {
				response = PaymentResponse.RESPONSE_WAITING;
			}
			if (mCallBack != null) {
				// Log.d("callback is not null", "" + response);
				mCallBack.onPurchaseComplete(response, purchase);
			} else {
				// Log.d("callback is NULL", "" + response);
			}
			if (!response.isWaiting()) {
				dialog = UI.showCinetMessageDialog(message);
			}
		}
		return dialog;
	}

	private String createResponseMessage(PaymentResponse response,
										 Purchase purchase) {
		// TODO Auto-generated method stub
		boolean hasCustomerName = response.hasBeenConfirmed()
				&& purchase.getCustomer() != null
				&& !TextUtils.isEmpty(purchase.getCustomer().getName());
		String message = CinetPay
				.getCinetPayMessage(
						hasCustomerName ? "SUCCES_D" : response.getMessage())
				.replace("$amount",
						mPurchase.getAmount() + mPurchase.getCurrency())
				.replace("$service", MERCHANT.getName());
		message = (hasCustomerName ? message.replace("$name", purchase
				.getCustomer().getName()) : message);
		message = (!response.hasBeenAccepted() ? "Code: " + response.getCode()
				+ "\n" : "")
				+ message;
		return message;
	}

	private void handleCinetPayHistoric(PaymentResponse response,
										Purchasable purchase) {
		SharedPreferences sharedP = CONTEXT.getSharedPreferences(
				SHARED_HISTORIC_FILE, 0);
		String lastPhone = sharedP.getString(SHARED_PHONE_HISTORIC, null);
		Editor editor = sharedP.edit();
		if (lastPhone != null)
			lastPhone += "," + purchase.getCustomer().getPhone();
		else {
			if (purchase != null && purchase.getCustomer() != null)
				lastPhone = purchase.getCustomer().getPhone();
		}
		editor.commit();

	}

	private void setPurchaseCustomer(Customer customer, boolean autoValidator) {
		setPhoneNumber(customer.getPhone(), autoValidator);
		if (mPayDialog.isShowing())
			setOTPCode(customer.getOtpCode());
	}

	private void setOTPCode(String otp) {
		if (mPayDialog.isValidOTP(otp)) {
			mPayDialog.edt_otp.setText(otp);
			mPayDialog.edt_otp.setEnabled(false);
		}
	}

	private PayUI setPhoneNumber(String phone, boolean autoValidator) {
		if (ToolKit.Control.isPhoneCinetPayCompatible(phone)) {
			mInitDialog.edt_phone.setText(phone);
			if (autoValidator) {
				mInitDialog.onContinuePressed();
			}
		}
		return this;
	}

	private void switchPayMode() {
		if (mPurchase.getCustomer() != null
				&& mPurchase.getCustomer().getPhone() != null)
			modeDirectPay();
		else
			modeManualPay();
	}

	private void modeDirectPay() {
		mInitDialog.show();
		setPurchaseCustomer(mPurchase.getCustomer(), true);
	}

	private void modeManualPay() {
		mInitDialog.show();
	}

	private boolean isCurrentPhoneAsMine() {
		try {
			TelephonyManager m = (TelephonyManager) CONTEXT
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (mPurchase == null)
				return false;
			if (mPurchase.getCustomer() == null)
				return false;
			return mPurchase.getCustomer().getPhone()
					.equals(m.getLine1Number());
		} catch (Exception e) {
			return false;
		}
	}

	private void toast(String text) {
		Toast.makeText(CONTEXT, text, Toast.LENGTH_LONG).show();
	}

	private String getPrefixPayButton() {
		String out = "Payer ";
		if (mPurchase.getType() == Purchase.TYPE_DONATION)
			out = "Donner ";
		else if (mPurchase.getType() == Purchase.TYPE_BUYING)
			out = "Acheter ";
		return out;
	}

	private String getPrefixDefaultTitle() {
		String out = "Payer à ";
		if (mPurchase.getType() == Purchase.TYPE_DONATION)
			out = "Faire un don à ";
		else if (mPurchase.getType() == Purchase.TYPE_BUYING)
			out = "Achat chez ";
		return out;
	}

	// public static PayUI getInstance() {
	// return instance;
	// }
}
