package com.cinetpay.sdk;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
public final class Customer {

	public String phoneNumber;
    public String otpCode;
    public String name;

	public Customer(String phone, String otp_or_token) {
		this.phoneNumber = phone;
		this.otpCode = otp_or_token;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public String getPhone() {
		return phoneNumber;
	}

	public void setPhone(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		String out = "";
		out += "Phone: " + phoneNumber + "\nCode OTP: " + otpCode;
		return out;
	}

}
