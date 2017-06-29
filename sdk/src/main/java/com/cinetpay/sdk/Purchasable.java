package com.cinetpay.sdk;

public interface Purchasable {

	public String getPaymentMethod();
	
	public Customer getCustomer();

	public String getAmount();

	public String getCurrency();

	public String getReference();

	public String getDescription();

	public String getMetaData();

	public String getTitle();

	public int getType();

	public Purchasable setDescription(String description);

	public void setPaymentMethod(String payMode);

	public Purchasable setCustomer(Customer user);

	public String getCustom();

	public String getOrigin();

}
