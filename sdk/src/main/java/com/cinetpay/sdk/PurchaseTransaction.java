package com.cinetpay.sdk;
/**
 * 
 * @author Istat: Toukea tatsi Jephte
 *
 */
public final class PurchaseTransaction {

	public Purchase purchase;
	private PaymentResponse response;
	public MerchantService merchant;
	public String token;
	public String id;
	public String payId;
	public String transactionDate;
	public String serverTransactionDate;

	public String getServerTransactionDate() {
		return serverTransactionDate;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public PurchaseTransaction(Purchase purchase, PaymentResponse response) {
		this.purchase = purchase;
		this.response = response;
	}

	public Purchase getPurchase() {
		return purchase;
	}

	public void setResponse(PaymentResponse response) {
		this.response = response;
	}

	public MerchantService getMerchantService() {
		return merchant;
	}

	public String getId() {
		return id;
	}

	public String getPayId() {
		return payId;
	}

	public String getToken() {
		return token;
	}

	public PaymentResponse getPaymentStatus() {
		return response;
	}

	@Override
	public String toString() {
		String out = "";
		out += purchase.toString() + "\n" + "\n"+ response.toString() + "\n" + "\nToken: " + token
                + "\nTrans ID: " + id + "\nPay Id: " + payId;
		return out;
	}

}
