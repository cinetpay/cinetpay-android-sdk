package com.cinetpay.sdk;

import com.cinetpay.sdk.tool.JSON;
import com.cinetpay.sdk.tool.Settings;
import com.cinetpay.sdk.MerchantService.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Permet d'effectuer des conversions entre des chaines de caractères en format
 * JSON vers des objets CinetPay directement utilisables.
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
public final class CinetPayParser {

	private CinetPayParser() {}

	/**
	 * Permet de Parser(convertire) une chaine de caractères JSON en un objet de
	 * type {@link ServiceAccount} le représentant.
	 *
	 * @param jsons
	 *            la chaine JSON
	 * @return L'objet {@link ServiceAccount} équivalent au JSON.
	 * @throws JSONException
	 */
	public static ServiceAccount parseToServiceAccount(String jsons) throws JSONException {
		JSONObject json = new JSONObject(jsons);
		json = json.getJSONObject("user").getJSONObject("service");
		ServiceAccount account = new ServiceAccount();
		account.setCredit(json.getDouble("credit"));
		account.setDebit(json.getDouble("debit"));
		account.setSolde(json.getDouble("solde"));
		return account;
	}

	public static List<PurchaseTransaction> parseToTransactionList(String jsons) throws JSONException {
		List<PurchaseTransaction> out = new ArrayList<PurchaseTransaction>();
		JSONObject json = new JSONObject(jsons);
		JSONObject jsonTransactions = json.optJSONObject("Transactions");
		JSONArray array = jsonTransactions.optJSONArray("transaction");
		List<JSONObject> transactionJSONList = JSON.JSONArrayToJsonList(array);
		for (JSONObject jsonO : transactionJSONList) {
            out.add(purchaseTransactionFromJSONObject(jsonO));
        }
		return out;
	}

	/**
	 * Permet de Parser(convertire) une chaine de caractères JSON en un objet de
	 * type {@link PurchaseTransaction} le représentant.
	 *
	 * @param jsons
	 *            la chaine JSON
	 * @return L'objet {@link PurchaseTransaction} équivalent au JSON.
	 * @throws JSONException
	 */
	public static PurchaseTransaction parseToPurchaseTransaction(String jsons) throws JSONException {
		JSONObject json = new JSONObject(jsons);
		json = json.getJSONObject("transaction");
		return purchaseTransactionFromJSONObject(json);
	}

	/**
	 * Permet de Parser(convertire) une chaine de caractères JSON en un objet de
	 * type {@link PaymentResponse} le représentant.
	 *
	 * @param jsons
	 *            la chaine JSON
	 * @return L'objet {@link PaymentResponse} équivalent au JSON.
	 * @throws JSONException
	 */
	public static PaymentResponse parseToPaymentResponse(String jsons) throws JSONException {
		JSONObject json = new JSONObject(jsons);
		json = json.getJSONObject("status");
		return new PaymentResponse(json.getString("code"), json.getString("message"));
	}

	/**
	 * Permet de Parser(convertire) une chaine de caractères JSON en un objet de
	 * type {@link MerchantService} le représentant.
	 *
	 * @param json
	 *            la chaine JSON
	 * @return L'objet {@link MerchantService} équivalent au JSON.
	 * @throws JSONException
	 */
	public static MerchantService parseForGetMerchantService(String json, MerchantService service)
            throws JSONException {
		service.info = parseToServiceInfo(json);
		Settings.prepar(json);
		return service;
	}

	private static PurchaseTransaction purchaseTransactionFromJSONObject(JSONObject json)
            throws JSONException {
		if (json.getString("cpm_trans_id") == null) {
            return null;
        }
		Customer customer = new Customer("+" + json.getString("cpm_phone_prefixe")
                + json.getString("cel_phone_num"), null);
		customer.name = json.optString("buyer_name");
		Purchase purchase = new Purchase(customer);
		purchase.setAmount(Integer.valueOf(json.getString("cpm_amount")));
		purchase.setCurrency(json.getString("cpm_currency"));
		purchase.setMetaData(json.getString("cpm_custom"));
		purchase.setPaymentMethod(json.optString("payment_method"));
		purchase.setReference(json.getString("cpm_trans_id"));
		PurchaseTransaction transaction = new PurchaseTransaction(purchase,
				new PaymentResponse(json.getString("cpm_result"),
						json.getString("cpm_error_message")));
		transaction.token = json.getString("signature");
		transaction.id = purchase.getReference();
		transaction.payId = json.getString("cpm_payid");
		transaction.serverTransactionDate = json.getString("created_at");
		transaction.transactionDate = json.getString("cpm_payment_date");
		MerchantService service = new MerchantService(null, json.getString("cpm_site_id"));
		transaction.merchant = service;
		transaction.purchase = purchase;
		return transaction;
	}

	/**
	 * Permet de Parser(convertire) une chaine de caractères JSON en un objet de
	 * type {@link ServiceInfo} le représentant.
	 *
	 * @param jsons
	 *            la chaine JSON
	 * @return L'objet {@link ServiceInfo} équivalent au JSON.
	 * @throws JSONException
	 */
	private static ServiceInfo parseToServiceInfo(String jsons) throws JSONException {

		JSONObject json = new JSONObject(jsons);
		ServiceInfo info = new ServiceInfo();

		JSONObject errorJson = json.optJSONObject("status");

		if (errorJson != null) {
			PaymentResponse response = parseToPaymentResponse(jsons);
			info.status_code_message[0] = response.code;
			info.status_code_message[1] = response.message;
			return info;
		} else {
			info.status_code_message[0] = "00";
			info.status_code_message[1] = "VERIFIED";
		}

		json = json.getJSONObject("service");

		info.cpm_site_id = json.getString("cpm_site_id");
		info.id_serv = json.getString("id_serv");
		info.id_clt_fk = json.getString("id_clt_fk");
		info.nom_serv = json.getString("nom_serv");
		info.description_serv = json.getString("description_serv");
		info.pays_serv = json.getString("pays_serv");
		info.ville_serv = json.getString("ville_serv");
		info.created_at = json.getString("created_at");
		info.updated_at = json.getString("updated_at");
		info.activated_at = json.getString("activated_at");
		info.solde_serv = json.getString("solde_serv");
		info.credit_serv = json.getString("credit_serv");
		info.debit_serv = json.getString("debit_serv");
		info.email_serv = json.getString("email_serv");

		return info;
	}

}
