package com.cinetpay.sdk.tool;
/**
 * 
 * @author Istat: Toukea tatsi Jephte
 *
 */
public class CinetPayException extends UnsupportedOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CinetPayException(String label){
		super(label);
	}

}
