package com.cinetpay.sdk.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.cinetpay.sdk.R;
import com.cinetpay.sdk.tool.CinetPayException;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
class CinetPayUISecurity {
	public static String INTEGRITY_VIOLATION = "CinetPay Integrity violation due to a CinetPay SDK modification. you dn't have to apply any modification on the current SDK provided by CinetCore. any modification inside the original SDK version maybe throw a CinetPay Security Violation Exception. Please do not modify this SDK. just use it.";
	static int BACKGROUND_COLOR = 0xFFf0f0f0;

	public static void checkPayUIIntegrity(Context context) {
		checkAccountUIIntegrity(context);
		checkMessageUIIntegrity(context);
		checkProgressUIIntegrity(context);
		checkPayUIInitIntegrity(context);
		checkPayUIPayIntegrity(context);
		checkTPEUIIntegrity(context);
		checkTransactionUIIntegrity(context);
		checkUIRessource(context, R.layout.cinetpay_include_inflation_pay_details,
				-778452109);

	}

	private static void checkBasePayUIIntegrity(Dialog dialog){
		if(!(dialog.findViewById(R.id.cinetpay_inflate_deflate) instanceof ImageView))
			throw new CinetPayException(INTEGRITY_VIOLATION+"::inflater");
		if(!(dialog.findViewById(R.id.cinetpay_cancel) instanceof ImageView))
			throw new CinetPayException(INTEGRITY_VIOLATION+"::cancel");
//		if (viewColor(dialog.getWindow().getDecorView()) != BACKGROUND_COLOR)
//			throw new CinetPayException(INTEGRITY_VIOLATION+"::Color");
//		Toast.makeText(dialog.getContext(), ""+viewColor(dialog.getWindow().getDecorView())+"::"+BACKGROUND_COLOR, 2).show();
	}
	public static void checkPayUIInitIntegrity(Dialog dialog) {
		// checkUIRessource(context,R.layout.dialog_cinetpay_phone_input,-1626803609);
		if(!(dialog.findViewById(R.id.cinetpay_edt_phone) instanceof EditText))
			throw new CinetPayException(INTEGRITY_VIOLATION);
		if(!(dialog.findViewById(R.id.cinetpay_btn_continue) instanceof Button))
			throw new CinetPayException(INTEGRITY_VIOLATION+"::Continue_Button");
		checkBasePayUIIntegrity(dialog);
		
	}
	public static void checkPayUIPayIntegrity(Dialog dialog) {
		// checkUIRessource(context,R.layout.dialog_cinetpay_phone_input,-1626803609);
		if(!(dialog.findViewById(R.id.cinetpay_edt_otp) instanceof EditText))
			throw new CinetPayException(INTEGRITY_VIOLATION);
		if(!(dialog.findViewById(R.id.cinetpay_pay) instanceof Button))
			throw new CinetPayException(INTEGRITY_VIOLATION+"::Pay_Button");
		checkBasePayUIIntegrity(dialog);
		
	}

	public static void checkPayUIInitIntegrity(Context context) {
		// checkUIRessource(context,R.layout.dialog_cinetpay_phone_input,-1626803609);
	}

	public static void checkPayUIPayIntegrity(Context context) {
		checkUIRessource(context, R.layout.cinetpay_dialog_pay, -1618598307);
	}

	public static void checkMessageUIIntegrity(Context context) {
		checkUIRessource(context, R.layout.cinetpay_dialog_info, -1471878246);
	}

	public static void checkProgressUIIntegrity(Context context) {
		checkUIRessource(context, R.layout.cinetpay_dialog_proceed, 123153643);
	}

	public static void checkTPEUIIntegrity(Context context) {
		checkUIRessource(context, R.layout.cinetpay_dialog, 1585164000);
	}

	public static void checkTransactionUIIntegrity(Context context) {
		checkMessageUIIntegrity(context);
	}

	public static void checkAccountUIIntegrity(Context context) {
		checkUIRessource(context, 0, 0);
	}

	public static void checkUIRessource(Context context, int ressource,
			int goodValue) {
		// if
		// (Stream.streamToString(context.getResources().openRawResource(R.layout.dialog_cinetpay_account)).hashCode()!=goodValue)
		// throw new CinetPayException(INTEGRITY_VIOLATION);
	}

	private static int viewColor(View v) {
		if(v==null) return BACKGROUND_COLOR;
		ColorDrawable colorDrawable = ((ColorDrawable) v.getBackground());
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_4444);
		Canvas canvas = new Canvas(bitmap);
		colorDrawable.draw(canvas);
		int pix = bitmap.getPixel(0, 0);
		bitmap.recycle();
		return pix;
	}
}
