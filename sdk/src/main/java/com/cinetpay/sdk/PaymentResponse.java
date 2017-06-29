package com.cinetpay.sdk;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
public final class PaymentResponse {

	public static String CODE_ACCEPTED = "00";
    public static String CODE_WAITING_CUSTOMER_TO_VALIDATE = "623";
	public String code = "604";
    public String message = "OTP_CODE_ERROR";

	private boolean ACCEPTED = false;
    private boolean SOLDED = false;
    private boolean WAITING = false;

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public PaymentResponse(String code1, String message1) {
		code = code1;
		message = message1;
		if (code != null) {
			SOLDED = code.equals(CODE_ACCEPTED);
			WAITING = code.equals(CODE_WAITING_CUSTOMER_TO_VALIDATE);
		}
		ACCEPTED = SOLDED || WAITING;
	}

	public boolean hasBeenAccepted() {
		return ACCEPTED;
	}

	public boolean hasBeenConfirmed() {
		return SOLDED;
	}

	@Deprecated
	public boolean hasBeenSolded() {
		return SOLDED;
	}

	public boolean isWaiting() {
		return WAITING;
	}

	@Override
	public String toString() {
		String out = "";
		out += "Code: " + code + "\nMessage:" + message;
		return out;
	}

	/*
	 * { "status":{ "code":"604", "message":"OTP_CODE_ERROR" } }
	 */
	public static PaymentResponse RESPONSE_WAITING = new PaymentResponse(
	        CODE_WAITING_CUSTOMER_TO_VALIDATE, "WAITING_CUSTOMER_TO_VALIDATE");
}
