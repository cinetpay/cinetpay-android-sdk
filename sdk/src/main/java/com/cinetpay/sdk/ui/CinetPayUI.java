package com.cinetpay.sdk.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.PurchaseTransaction;
import com.cinetpay.sdk.MerchantService.ServiceInfo;
import com.cinetpay.sdk.plugins.HttpPayPlugin;
import com.cinetpay.sdk.process.CinetPay;
import com.cinetpay.sdk.process.CinetPay.AccountCheckCallBack;
import com.cinetpay.sdk.process.CinetPay.PayCallBack;
import com.cinetpay.sdk.process.CinetPay.TransactionCheckCallBack;
import com.cinetpay.sdk.tool.AdvancedAsyncTask;
import com.cinetpay.sdk.tool.Settings;
import com.cinetpay.sdk.tool.ToolKit.Screen;

/**
 * Permet la gestion de toute les interfaces utilisateur et les vues CinetPay.
 * toute invocation d'un visuele CinetPay se fait directement à partir de cette
 * class qui est étroitement lié à la class {@link CinetPay}
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
/**
 * @author Toukea Tatsi (Istat)
 *
 */
public final class CinetPayUI {

	Activity mActivity;
	MerchantService mMerchant;
	CinetMessageDialog currentMessageDialog;
	CinetProgressDialog currentProgressDialog;
	CinetPay mCinetPay;
	PayUI mPayUI;
	Purchasable currentPurchase;
	private boolean safeMode = false;

	private CinetPayUI(Activity activity, MerchantService merchant) {
		this.mActivity = activity;
		this.mMerchant = merchant;
		this.mCinetPay = new CinetPay(merchant);
		Settings.prepar(mActivity);

		// CinetPayUISecurity.checkPayUIIntegrity(mActivity);
	}

	public void beginPayment(Purchasable purchase) {
		beginPayment(purchase, null);
	}

	public void beginPayment(Purchasable purchase, PayCallBack callBack) {
		beginPayment(null, purchase, callBack);
	}

	/**
	 * Permet de commencer une sequence sécurisée de paiement. avec CinetPay,
	 * toute sequence sécurisé de paement se fait via une suite d'interfaces
	 * durant lesquelles, l'utilisateur est appelé a renseigner et a valider un
	 * certain nombre de champs et d'actions. Cette fonction permet d'enclencher
	 * cette séquence automatiquement.
	 *
	 * @param purchase
	 *            l'objet {@link Purchase} représentant l'achat
	 * @param callBack
	 *            une instance de {@link PayCallBack} qui définie les actions a
	 *            appeler une fois l'achat acompli. voir
	 *            {@link PayCallBack#onPurchaseComplete(PaymentResponse,Purchase)}
	 */
	public void beginPayment(final PayPlugin plugin,
							 final Purchasable purchase, PayCallBack callBack) {
		if (plugin != null) {
			beginPluginedPayment(plugin, purchase, callBack);
		} else {
			beginInternetPayment(purchase, callBack);
		}
	}

	private void beginInternetPayment(final Purchasable purchase,
									  final PayCallBack callBack) {
		currentPurchase = purchase;
		setUpOrientation();
		final Dialog dialog = showCinetProgressDialog(
				"Initialisation CinetPay...", true);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				mCinetPay.getCurrentTask().cancel(true);
				restorOrientation();
			}
		});
		mCinetPay
				.doAsyncServiceInfoCheck(new CinetPay.ServiceInfoCheckCallBack() {
					@Override
					public void onCheckComplete(ServiceInfo infos,
												boolean success) {
						// TODO Auto-generated method stub
						dialog.setOnCancelListener(null);
						cancelCurrentProgressDialog();
						if (success) {
							Settings.save(mActivity);
							mPayUI = PayUI.getInstance(CinetPayUI.this);
							// Log.d("cinetpay_instanciate_pay_has_callback", ""
							// + callBack);
							mPayUI.beginPayment(null, purchase, callBack);
						} else {
							restorOrientation();
							showCinetMessageDialog(infos != null ? CinetPay
									.getCinetPayMessage(infos
											.getMerchantStatus()) : CinetPay
									.getCinetPayMessage("NETWORK_ERROR"));
						}
					}
				});

	}

	private void beginPluginedPayment(final PayPlugin plugin,
									  final Purchasable purchase, final PayCallBack callBack) {
		currentPurchase = purchase;
		if (plugin instanceof HttpPayPlugin) {
			beginInternetPayment(purchase, callBack);
			return;
		} else {
			setUpOrientation();
			if (plugin.needUIValidator()) {
				mPayUI = PayUI.getInstance(CinetPayUI.this);
				mPayUI.beginPayment(plugin, purchase, callBack);
			} else {
				showCinetProgressDialog("Patientez svp...");
				plugin.onProcessPayment(purchase, callBack);
			}
		}
	}

	@Deprecated
	/**
	 * Permet d'afficher un Dialog de type TPE Cinetpay; afin de permettre a
	 * l'utilisateur d'effectuer un Paiement. voir aussi
	 * {@link CinetPayUI#showTPEDialog(Purchase, PayCallBack,int)}.
	 *
	 * @param purchase
	 *            l'objet {@link Purchase} représentant l'achat
	 * @param callBack
	 *            une instance de {@link PayCallBack} qui défini les actions a
	 *            effectuer une fois l'achat acompli. voir
	 *            {@link PayCallBack#onPurchaseComplete(PaymentResponse,Purchase)}
	 */
	public TPEPayDialog showTPEDialog(Purchase purchase) {
		// currentPurchase = purchase;
		// TPEPaymentDialog dialog = new TPEPaymentDialog(mActivity, mMerchant,
		// null, purchase, this);
		// dialog.show();
		// currentMessageDialog = dialog.messageD;
		// currentProgressDialog = dialog.progresD;
		// return dialog;
		return showTPEDialog(purchase, null);
	}

	@Deprecated
	/**
	 * Permet d'afficher un Dialog de type TPE Cinetpay; afin de permettre a
	 * l'utilisateur d'effectuer un Paiement. voir aussi
	 * {@link CinetPayUI#begginPayment(Purchase, PayCallBack,int)} qui est une
	 * alternative recommandée pour les applications grand-public.
	 *
	 * @param purchase
	 *            l'objet {@link Purchase} représentant l'achat
	 * @param callBack
	 *            une instance de {@link PayCallBack} qui défini les actions a
	 *            effectuer une fois l'achat acompli. voir
	 *            {@link PayCallBack#onPurchaseComplete(PaymentResponse,Purchase)}
	 */
	public TPEPayDialog showTPEDialog(Purchase purchase, PayCallBack callback) {
		TPEPayDialog dialog = new TPEPayDialog(mActivity, mMerchant, callback,
				purchase, this);
		dialog.show();
		currentMessageDialog = dialog.messageD;
		currentProgressDialog = dialog.progresD;
		return dialog;
	}

	/**
	 * Permet d'afficher dans une interface CinetPay le solde du marchant
	 * actuellement enregistré lors de la création de la CinetPayUI instance.
	 *
	 * @param callback
	 *            instance de {@link AccountCheckCallBack} a appeler une fois la
	 *            requête terminée.
	 * @return retourne un tableau de dialogue[] dont le 1er représente le
	 *         dialogue de progression (il fait patienter l'utilisateur durant
	 *         l'execution de la requête) et le 2eme le dialogue d'affichage de
	 *         solde (celui-ci est affiché une fois que la requête est terminée
	 *         et que le ProgressDialog est fermé.)
	 */
	public Dialog[] showAccountDialog(final AccountCheckCallBack callback) {
		return showAccountDialog(mActivity, mMerchant, callback);
	}

	/**
	 * Permet d'afficher dans une interface CinetPay le solde du marchant actuellement enregistré lors de la création de la CinetPayUI instance.
	 * voir {@link #showAccountDialog(AccountCheckCallBack)
	 * @return retourne un tableau de dialogue[] dont le 1er représente le dialogue de progression (il fait patienter l'utilisateur durant l'execution de la requête) et le 2eme le dialogue d'affichage de solde (celui-ci est affiché une fois que la requête est terminée et que le ProgressDialog est fermé.)

	 */
	public Dialog[] showAccountDialog() {
		return showAccountDialog(mActivity, mMerchant, null);
	}

	public Dialog[] showTransactionDialog(
			final TransactionCheckCallBack callback, String trans_id) {
		return showTransactionDialog(mActivity, mMerchant, callback, trans_id);
	}

	public Dialog[] showTransactionDialog(String trans_id) {
		return showTransactionDialog(mActivity, mMerchant, null, trans_id);
	}

	public boolean cancelCurrentMessageDialog() {
		boolean out = currentMessageDialog != null
				&& currentMessageDialog.isShowing();
		if (out)
			currentMessageDialog.cancel();
		return out;
	}

	public boolean cancelCurrentProgressDialog() {
		boolean out = currentProgressDialog != null
				&& currentProgressDialog.isShowing();
		if (out)
			currentProgressDialog.cancel();
		return out;
	}

	public void cancelCurrentPayInitDialog() {
		if (currentProgressDialog.isShowing())
			mPayUI.mInitDialog.cancel();
	}

	public void cancelCurrentPayDialog() {
		if (mPayUI.mPayDialog.isShowing())
			mPayUI.mPayDialog.cancel();
	}

	public boolean isFinalisingPayUIShowing() {
		// if(mPayUI.mInitDialog.isShowing()) return true;
		if (mPayUI.mPayDialog.isShowing())
			return true;
		return false;
	}

	public boolean isSafePurchase(Purchasable purchase) {
		return safeMode
				|| (currentPurchase == purchase && isPayUIShowing() && mPayUI
				.hasUserAgreement());
	}

	public boolean isPayUIShowing() {
		return ((currentMessageDialog != null && currentMessageDialog
				.isShowing())
				|| (currentProgressDialog != null && currentProgressDialog
				.isShowing()) || mPayUI.mInitDialog.isShowing() || mPayUI.mPayDialog
				.isShowing());
	}

	public PayUI getCurrentPayUI() {
		if ((currentMessageDialog != null && currentMessageDialog.isShowing())
				|| (currentProgressDialog != null && currentProgressDialog
				.isShowing()) || mPayUI.mInitDialog.isShowing()
				|| mPayUI.mPayDialog.isShowing())
			return mPayUI;
		else
			return null;
	}

	public int getPayUIState() {
		if (mPayUI.mInitDialog.isShowing())
			return PayUI.STATE_USER_ENTRY_PHONE;
		if (mPayUI.mPayDialog.isShowing())
			return PayUI.STATE_USER_ENTRY_OTP;
		if (currentProgressDialog.isShowing())
			return PayUI.STATE_PROCESSING;
		if (currentMessageDialog.isShowing())
			return PayUI.STATE_FINISH;
		else
			return PayUI.STATE_NO_STARTED;
	}

	public static CinetPayUI getInstance(Activity activity,
										 MerchantService merchant) {
		return new CinetPayUI(activity, merchant);

	}

	static Dialog[] showAccountDialog(final Activity context,
									  final MerchantService merchant, final AccountCheckCallBack callback) {
		final CinetPay cinetPay = new CinetPay(merchant);
		final AccountCheckDialog accountD = new AccountCheckDialog(context,
				merchant);
		final CinetProgressDialog progresD = new CinetProgressDialog(context);
		progresD.show();
		final AdvancedAsyncTask<?, ?, ?> task = cinetPay
				.doAsyncServiceInfoCheck(new CinetPay.ServiceInfoCheckCallBack() {

					@Override
					public void onCheckComplete(ServiceInfo infos,
												boolean success) {
						// TODO Auto-generated method stub

						accountD.setAccount(success ? infos.getAccount() : null);
						if (callback != null) {
							callback.onCheckComplete(
									infos != null ? infos.getAccount() : null,
									infos != null ? success : false);
						}
						accountD.show();
						progresD.cancel();
					}
				});
		progresD.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				task.cancel(true);
			}
		});
		return new Dialog[] { progresD, accountD };
	}

	/**
	 *
	 * @return the current {@link CinetPay} instance.
	 */
	public CinetPay getCinetPayInstance() {
		return mCinetPay;
	}

	static Dialog[] showTransactionDialog(final Activity context,
										  final MerchantService merchant,
										  final TransactionCheckCallBack callback, String trans_id) {
		final CinetPay cinetPay = new CinetPay(merchant);
		final CinetMessageDialog msgD = new CinetMessageDialog(context);
		final CinetProgressDialog progresD = new CinetProgressDialog(context);
		progresD.show();
		final AdvancedAsyncTask<?, ?, ?> task = cinetPay
				.doAsyncTransactionCheck(new TransactionCheckCallBack() {

					@Override
					public void onCheckComplete(
							PurchaseTransaction transaction, boolean success) {
						// TODO Auto-generated method stub
						if (transaction != null)
							msgD.show(transaction.toString());
						else
							msgD.show(CinetPay
									.getCinetPayMessage("NETWORK_ERROR"));
						progresD.cancel();
						if (callback != null)
							callback.onCheckComplete(transaction, success);
					}
				}, trans_id);
		progresD.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				task.cancel(true);
			}
		});
		return new Dialog[] { progresD, msgD };
	}

	void beginSafeMode() {
		safeMode = true;
	}

	void cancelSafeMode() {
		safeMode = false;
	}

	public Activity getContext() {
		// TODO Auto-generated method stub
		return mActivity;
	}

	Dialog showCinetProgressDialog(String msg, boolean recreateDialog) {
		if (recreateDialog || currentProgressDialog == null)
			currentProgressDialog = new CinetProgressDialog(mActivity);
		currentProgressDialog.show(msg);
		return currentProgressDialog;
	}

	Dialog showCinetMessageDialog(String msg, boolean recreateDialog) {
		if (recreateDialog || currentMessageDialog == null
				|| currentMessageDialog instanceof CinetWaitingDialog)
			currentMessageDialog = new CinetMessageDialog(mActivity);
		currentMessageDialog.show(msg);
		return currentMessageDialog;
	}

	Dialog showWaitingDialog(String msg, boolean recreateDialog, Purchasable p) {
		if (recreateDialog || currentMessageDialog == null
				|| !(currentMessageDialog instanceof CinetWaitingDialog))
			currentMessageDialog = new CinetWaitingDialog(p);
		currentMessageDialog.show(msg);
		return currentMessageDialog;
	}

	Dialog showCinetProgressDialog(String msg) {
		return showCinetProgressDialog(msg, true);
	}

	Dialog showCinetMessageDialog(String msg) {
		return showCinetMessageDialog(msg, true);
	}

	Dialog showCinetWaitingDialog(String msg, Purchasable p) {
		return showWaitingDialog(msg, false, p);
	}

	static int activityHostOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

	int setUpOrientation() {
		int currentOrientation = getContext().getRequestedOrientation();
		int orientation = getContext().getWindowManager().getDefaultDisplay()
				.getRotation();
		if (orientation == Surface.ROTATION_90
				|| orientation == Surface.ROTATION_270) {
			Screen.setLandScape(getContext());
		} else {
			Screen.setPortrait(getContext());
		}
		activityHostOrientation = currentOrientation;
		return currentOrientation;
	}

	void restorOrientation() {
		if (activityHostOrientation != 0) {
			getContext().setRequestedOrientation(activityHostOrientation);
			activityHostOrientation = 0;
		}
	}
	// ------------------------------------

}
