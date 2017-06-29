package com.cinetpay.sdk;

import android.text.TextUtils;

import com.cinetpay.sdk.tool.ToolKit;

/**
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
public class Purchase implements Purchasable {

	public final static int MAX_LENGHT_TITLE = 16;

	public final static String PAYMENT_METHOD_ORANGE_MONEY = "OM";
    public final static String MODE_PAYMENT_PAYPAL = "PP";
    public final static String PAYMENT_METHOD_MTN_MOBILE_MONEY = "MOMO";
    public final static String PAYMENT_METHOD_MOOV_FLOOZ = "FLOOZ";
    public final static String PAYMENT_METHOD_UNKNOW = "UKN";

	public final static int TYPE_PAYMENT = 0;
    public final static int TYPE_DONATION = 1;
    public final static int TYPE_BUYING = 3;

	private String reference = "";
    private String amount;
    private String currency = "CFA";
    private String description = "";
    private String origin = "";
    private String paymentMethod = PAYMENT_METHOD_UNKNOW;
	private String title;
	private String metaData;
	private int type = TYPE_PAYMENT;
	private Customer user;

	public Purchase() {
	}

	public Purchase(String customerPhone, String otp_or_token) {
		this.user = new Customer(customerPhone, otp_or_token);
	}

	public Purchase(Customer user) {
		this.user = user;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public Customer getCustomer() {
		return user;
	}

	public String getAmount() {
		return amount;
	}

	public String getAmountJoinWithCurrency() {
		return amount + currency;
	}

	public String getCurrency() {
		return currency;
	}

	public String getReference() {
		return reference;
	}

	public String getDescription() {
		return description;
	}

	public void setPaymentMethod(String payMode) {
		this.paymentMethod = payMode;
	}

	@Deprecated
	public void setMode(String payMode) {
		this.paymentMethod = payMode;
	}

	public Purchase setCustomer(Customer user) {
		this.user = user;
		return this;
	}

	public Purchase setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public Purchase setAmount(int amount) {
		this.amount = "" + amount;
		return this;
	}

	public Purchase setMetaData(String metaData) {
		this.metaData = metaData;
		return this;
	}

	public String getMetaData() {
		return metaData;
	}

	public Purchase setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public Purchase setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean hasTitle() {
		return !TextUtils.isEmpty(title);
	}

	public String getTitle() {
		return title == null ? ToolKit.Word.ShortWord(description, MAX_LENGHT_TITLE) : title;
	}

	public Purchase setTitle(String title) {
		this.title = title;
		return this;
	}

	public Purchase setType(int type) {
		this.type = type;
		return this;
	}

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		String out = "";
		out += "Référence: " + reference + "\nMontant: " + amount + "\nUnité: "
				+ currency + "\nMode: " + paymentMethod + "\nMeta: " + metaData
				+ "\nDescription: " + description + "\n" + "\n"
				+ user.toString();
		return out;
	}

	@Override
	public String getCustom() {
		return metaData;
	}

	@Override
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public static Purchase createFromPurchasable(Purchasable p) {
		if (p == null) {
            return null;
        }
		Purchase purchase = new Purchase();
		purchase.setAmount(Integer.valueOf(p.getAmount()));
		purchase.setCurrency(p.getCurrency());
		purchase.setCustomer(p.getCustomer());
		purchase.setDescription(p.getDescription());
		purchase.setMetaData(p.getMetaData());
		purchase.setPaymentMethod(p.getPaymentMethod());
		purchase.setOrigin(p.getOrigin());
		purchase.setReference(p.getReference());
		purchase.setTitle(p.getTitle());
		purchase.setType(p.getType());
		return purchase;
	}

}
