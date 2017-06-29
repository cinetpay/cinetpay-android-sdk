package com.cinetpay.sdk.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.cinetpay.sdk.MerchantService;
import com.cinetpay.sdk.MerchantService.ServiceInfo;
import com.cinetpay.sdk.PaymentResponse;
import com.cinetpay.sdk.Purchasable;
import com.cinetpay.sdk.Purchase;
import com.cinetpay.sdk.PurchaseTransaction;
import com.cinetpay.sdk.ServiceAccount;
import com.cinetpay.sdk.tool.AdvancedAsyncTask;
import com.cinetpay.sdk.tool.CinetPayException;
import com.cinetpay.sdk.tool.CinetPayText;
import com.cinetpay.sdk.tool.Settings;
import com.cinetpay.sdk.tool.ToolKit;
import com.cinetpay.sdk.ui.CinetPayUI;

/**
 * Class centrale du module CinetPay elle permet d'interfacé la plateforme
 * CinetPay API avec votre application mobile. il sagit du grand coordinateur.
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
public final class CinetPay {
	CinetPayProcess mProcess;
	AdvancedAsyncTask<?, ?, ?> currentTask;
	public final static int MODE_CONNEXION_SMS = 1,
			MODE_CONNEXION_INTERNET = 0;

	public CinetPay(MerchantService merchantService) {
		mProcess = new CinetPayProcess(merchantService);
	}

	public static void preparSettings(Context context) {
		Settings.prepar(context);
	}

	// public CinetPayProcess getCurrentProcess() {
	// return mProcess;
	// }

	/**
	 * obtenir l'instance du marchant actuellement enregistré lors de la
	 * creation de votre CinetPay Instance.
	 */
	public MerchantService getCurrentMerchant() {
		return mProcess.getCurrentMerchant();
	}


	public AsyncPay doAsyncPay(Purchasable purchase, PayCallBack callBack,
							   CinetPayUI ui) {
		if (ui != null && ui.isSafePurchase(purchase)) {
			if (purchase.getCustomer() == null) {
				throw new CinetPayException(
						"CinetpPay customer not defined: the current Customer you are using to perform this paiement is not fine defined. please use a fine defined customer to perfom payment: ");
			}
			AsyncPay tmp = new AsyncPay(callBack);
			tmp.execute(purchase);
			currentTask = tmp;
			return tmp;
		} else {
			throw new CinetPayException(
					"CinetpPay security violation: the current transaction is not safe. please use CinetPayUI.begginPayment or CinetPayUI.showTPEDialog in order to start a safe payment transaction.");
		}
	}

	/**
	 * Effectuer une requête asynchrone afin d'obtenir le compte (credit, debit,
	 * solde) du marchant actuellement enregistré lors de la creation de votre
	 * instance de {@link CinetPay}
	 *
	 * @param callBack
	 *            instance de {@link AccountCheckCallBack} à appeler a la fin de
	 *            la requête asynchrone.
	 * @return retourne une instance du processus ({@link AsyncAccount}
	 *         déscendant de android {@link AsyncTask) actuellement en
	 *         execution.
	 */
	public AsyncAccount doAsyncAccountCheck(AccountCheckCallBack callBack) {

		AsyncAccount tmp = new AsyncAccount(callBack);
		tmp.execute();
		currentTask = tmp;
		return tmp;
	}

	/**
	 * Effectuer une requête asynchrone afin d'obtenir plus d'infos concernant
	 * le marchant actuellement enregistré lors de la creation de l'objet
	 * {@link CinetPay}. Ces informations sont matérialisé par la class
	 * {@link ServiceInfo} qui les renferme.
	 *
	 * @param callBack
	 *            instance de {@link TransactionCheckCallBack} à appeler a la
	 *            fin de la requête asynchrone.
	 * @return retourne une instance du processus ({@link AsyncCheck} déscendant
	 *         de android {@link AsyncTask) actuellement en execution.
	 */
	public AsyncServiceInfo doAsyncServiceInfoCheck(
			ServiceInfoCheckCallBack callBack) {
		AsyncServiceInfo tmp = new AsyncServiceInfo(callBack);
		tmp.execute();
		currentTask = tmp;
		return tmp;
	}

	/**
	 * Effectuer une requête asynchrone afin d'obtenir les informations sur une
	 * transaction effectuée avec in ID bien précis
	 *
	 * @param callBack
	 *            instance de {@link TransactionCheckCallBack} à appeler a la
	 *            fin de la requête asynchrone.
	 * @return retourne une instance du processus ({@link AsyncCheck} déscendant
	 *         de android {@link AsyncTask) actuellement en execution.
	 */
	public AsyncCheck doAsyncTransactionCheck(
			TransactionCheckCallBack callBack, String... purchase_ref) {

		AsyncCheck tmp = new AsyncCheck(callBack);
		tmp.execute(purchase_ref);
		currentTask = tmp;
		return tmp;
	}

	public AsyncHistoricCheck doAsyncTransactionHistoricCheck(
			HistoricCheckCallBack callBack, String begginDate, String endDate) {

		AsyncHistoricCheck tmp = new AsyncHistoricCheck(callBack, begginDate,
				endDate);
		tmp.execute();
		currentTask = tmp;
		return tmp;
	}

	/**
	 * Permet d'obtenir la tâche actuellement effectuée par votre instance
	 * {@link CinetPay}. Celle ci peut être de type {@link AsyncPay},
	 * {@link AsyncCheck}, {@link AsyncServiceInfo} ou une {@link AsyncAccount}
	 * qui sont toutes filles de la class android {@link AsyncTask}. le type de
	 * tâche dépendant de la dernière tâche effectuée par la {@link CinetPay}
	 * instance. (paiement,transactionCheck,ServiceInfo ou check solde).
	 *
	 * @return retourne la tâche actuellement effectuée par la CinetPay
	 *         instance,, si aucune tâche n'est en cours, elle retourne null.
	 */
	public AdvancedAsyncTask<?, ?, ?> getCurrentTask() {
		return currentTask;
	}

	/**
	 * vous n'avez pas a utiliser cette fonction. elle est utilisée
	 * intérieurement par CinetPay-SDK afin de définir quel message doit être
	 * affiché a l'utilisateur.
	 *
	 * @param id_msg
	 * @return le message CinetPay ainsi décodé et déterminé.
	 */
	public static String getCinetPayMessage(String id_msg) {
		String out = getErrorMap().get(id_msg);
		if (TextUtils.isEmpty(out))
			return getErrorMap().get("UNKNOWN_ERROR");
		return out;
	}

	private static HashMap<String, String> getErrorMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("SUCCES", CinetPayText.SUCCES_P);
		map.put("SUCCES_D", CinetPayText.SUCCES_P_WITH_NAME);
		map.put("PAYMENT_FAILED", CinetPayText.PAYMENT_FAILED);
		map.put("MERCHANT_NOT_FOUND", CinetPayText.MERCHANT_NOT_FOUND);
		map.put("AUTH_NOT_FOUND", CinetPayText.MERCHANT_NOT_FOUND);
		map.put("INSUFFICIENT_BALANCE", CinetPayText.INSUFFICIENT_BALANCE);
		map.put("OTP_CODE_ERROR", CinetPayText.OTP_CODE_ERROR);
		map.put("WAITING_CUSTOMER_TO_VALIDATE",
				CinetPayText.WAITING_CUSTOMER_TO_VALIDATE);
		map.put("ERROR_SITE_ID_NOTVALID", CinetPayText.ERROR_SITE_ID_NOTVALID);
		map.put("GATEWAY_TIMEOUT", CinetPayText.GATEWAY_TIMEOUT);
		map.put("UNKNOWN_ERROR", CinetPayText.UNKNOWN_ERROR);
		map.put("NETWORK_ERROR", CinetPayText.NETWORK_ERROR);
		map.put("NETWORK_ERROR_EXPLICIT", CinetPayText.NETWORK_ERROR_EXPLICIT);
		map.put("ERROR_MOMOPAY_UNAVAILABLE",
				CinetPayText.ERROR_MOMOPAY_UNAVAILABLE);
		map.put("ERROR_OMPAY_UNAVAILABLE", CinetPayText.ERROR_OMPAY_UNAVAILABLE);

		return map;
	}

	/**
	 * Verifie si oui ou non, votre application s'exécute dans un context
	 * CinetPay. pour le moment, le seul context CinetPay valide est la côte
	 * d'ivoire. vous pouvez utilisez cette fonction pour par exemple n'affichez
	 * votre module de paiement que ssi; l'utilisateur est dans une region ou
	 * CinetPay est valable et accepté.
	 *
	 * @param context
	 *            le context de l'application
	 * @return oui ou non le context est "CinetPayen".
	 */
	public static boolean isCinetPayContext(Context context) {
		return ToolKit.Control.isCinetPayContext(context);
	}

	public static HashMap<String, String> getPaymentMap(Context context) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY,
				CinetPayText.MOBILE_MONEY_LABEL);
		map.put(Purchase.PAYMENT_METHOD_MOOV_FLOOZ,
				CinetPayText.MOOV_FLOOZ_LABEL);
		map.put(Purchase.PAYMENT_METHOD_ORANGE_MONEY,
				CinetPayText.ORANGE_MONEY_LABEL);
		map.put(Purchase.PAYMENT_METHOD_UNKNOW,
				"\nINCONNU. Désolé; Votre numéro n'est ni un numéro Orange ni un numéro Mtn");

		return map;
	}

	final class AsyncPay extends
			AdvancedAsyncTask<Purchasable, PaymentResponse, Void> {
		PayCallBack mCallBack;
		PaymentResponse response;
		Purchasable tmp;

		public AsyncPay(PayCallBack callBack) {
			mCallBack = callBack;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mCallBack != null)
				mCallBack.onPurchaseComplete(response,
						Purchase.createFromPurchasable(tmp));
		}

		@Override
		protected void onProgressUpdate(PaymentResponse... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Purchasable... params) {
			// TODO Auto-generated method stub

			for (Purchasable purchase : params) {
				try {
					response = mProcess.processPayment(purchase);
					tmp = purchase;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					response = new PaymentResponse("1024", "NETWORK_ERROR");
					Log.e("Async_pay_bkg_pay",
							"error::" + response.getMessage() + " Label:" + e);
					break;
				}

			}
			return null;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	}

	final class AsyncCheck extends
			AdvancedAsyncTask<String, PurchaseTransaction, Void> {
		TransactionCheckCallBack mCallBack;
		PurchaseTransaction mTransaction;

		public AsyncCheck(TransactionCheckCallBack callBack) {
			mCallBack = callBack;
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			for (String trans_id : params) {
				try {
					mTransaction = mProcess.checkPaymentStatus(trans_id);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e("cinet_asycCheck_bkg", "EROOR::" + e);
					break;
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mCallBack != null)
				mCallBack.onCheckComplete(mTransaction, mTransaction != null);
		}
	}

	final class AsyncAccount extends AdvancedAsyncTask<Void, Void, Void> {
		AccountCheckCallBack mCallBack;
		ServiceAccount mAccount;

		public AsyncAccount(AccountCheckCallBack callBack) {
			mCallBack = callBack;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				mAccount = mProcess.getCurrentAccount();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("cinetP_AsyncAc_doBck", "ERROR::" + e
						+ "\nMerchantInfo::"
						+ mProcess.getCurrentMerchant().toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mCallBack != null)
				mCallBack.onCheckComplete(mAccount, mAccount != null);
		}
	}

	final class AsyncServiceInfo extends AdvancedAsyncTask<Void, Void, Void> {
		ServiceInfoCheckCallBack mCallBack;
		ServiceInfo mInfo;

		public AsyncServiceInfo(ServiceInfoCheckCallBack callBack) {
			mCallBack = callBack;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				mInfo = mProcess.getServiceInfo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("cinetP_AsyncAc_doBck", "ERROR::" + e
						+ "\nMerchantInfo::"
						+ mProcess.getCurrentMerchant().toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mCallBack != null)
				mCallBack.onCheckComplete(mInfo,
						mInfo != null && mInfo.isValidMerchant());
		}
	}

	final class AsyncHistoricCheck extends
			AdvancedAsyncTask<Void, PurchaseTransaction, Void> {
		HistoricCheckCallBack mCallBack;
		List<PurchaseTransaction> mTransactions = new ArrayList<PurchaseTransaction>();
		String begginDate, endDate;

		public AsyncHistoricCheck(HistoricCheckCallBack callBack,
								  String begginDate, String endDate) {
			this.begginDate = begginDate;
			this.endDate = endDate;
			mCallBack = callBack;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				mTransactions = mProcess.getTransactionHistory(begginDate,
						endDate);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("asycHistoricCheck_bkg", "EROOR::" + e);

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mCallBack != null)
				mCallBack.onCheckComplete(mTransactions, mTransactions != null);
		}
	}

	public interface PayCallBack {
		public abstract void onPurchaseComplete(PaymentResponse response,
                                                Purchase purchase);
	}

	public interface HistoricCheckCallBack {
		public abstract void onCheckComplete(
                List<PurchaseTransaction> transactions, boolean success);
	}

	/**
	 * Interface de rappel après demande de plus d'information sur une
	 * transaction CinetPay (vous pourez aisi obtenir: le nom, le solde, l'
	 * adresse, etc...). Voir
	 * {@link CinetPay#doAsyncTransactionCheck(TransactionCheckCallBack, String...)}
	 *
	 * @author Istat
	 *
	 */
	public interface TransactionCheckCallBack {
		public abstract void onCheckComplete(PurchaseTransaction transaction,
                                             boolean success);
	}

	/**
	 * Interface de rappel après demande d'information sur le solde, débit et
	 * crédit du compte marchant actuel. Voir
	 * {@link CinetPay#doAsyncAccountCheck(AccountCheckCallBack)}
	 *
	 * @author Istat
	 *
	 */
	public interface AccountCheckCallBack {
		public abstract void onCheckComplete(ServiceAccount account,
                                             boolean success);
	}

	/**
	 * Interface de rappel après demande d'information sur le service
	 * actuellement enregistré (Marchant). Voir
	 * {@link CinetPay#doAsyncServiceInfoCheck(ServiceInfoCheckCallBack)}
	 *
	 * @author Istat
	 *
	 */
	public interface ServiceInfoCheckCallBack {
		public abstract void onCheckComplete(ServiceInfo infos, boolean success);
	}

}
