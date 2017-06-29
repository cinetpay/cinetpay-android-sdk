package com.cinetpay.sdk.tool;

import istat.android.network.utils.ToolKits.Stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.cinetpay.sdk.Purchase;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public final class Settings {
	final static String FIELD_OPERATOR_SYNTAX = "syntaxe_operator";
	final static String FIELD_OPERATOR_PHONES = "phone_operator";
	final static String FIELD_OPERATOR_PREFIX = "prefix_operator";
	static String SYNTAX_USSD_ORANGE;
	static String SYNTAX_USSD_MTN;
	static String PREFIX_PATERN_ORANGE;
	static String PREFIX_PATERN_MTN;
	static String PREFIX_PATERN_MOOV;

	final static String SYNTAX_USSD_ORANGE_DEFAULT = "#144*621*0000#";
	final static String SYNTAX_USSD_MTN_DEFAULT = "*133*11#";
	final static String PREFIX_PATERN_ORANGE_DEFAULT = "^[0,4,5,7]{1}[7,8,9]{1}\\d{6}$";
	final static String PREFIX_PATERN_MTN_DEFAULT = "^[0,4,5]{1}[4,5,6]{1}\\d{6}$";
	final static String PREFIX_PATERN_MOOV_DEFAULT = "^[0,4]{1}[0,1,2,3]{1}\\d{6}$";
	public final static HashMap<String, String> CINETPAY_PHONE_NUMBERS = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Purchase.PAYMENT_METHOD_ORANGE_MONEY, "48252211");
			put(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY, "75199190");
			put(Purchase.PAYMENT_METHOD_MOOV_FLOOZ, "02570002");
		}
	};
	public static String CINETPAY_PHONE_NUMBER_DEFAULT = CINETPAY_PHONE_NUMBERS
			.get(Purchase.PAYMENT_METHOD_ORANGE_MONEY);

	public static String getOrangeUssdSyntax() {
		return SYNTAX_USSD_ORANGE != null ? SYNTAX_USSD_ORANGE
				: SYNTAX_USSD_ORANGE_DEFAULT;
	}

	public static String getMtnUssdSyntax() {
		return SYNTAX_USSD_MTN != null ? SYNTAX_USSD_MTN
				: SYNTAX_USSD_MTN_DEFAULT;
	}

	public static String getOrangePhonePrefix() {
		return PREFIX_PATERN_ORANGE != null ? PREFIX_PATERN_ORANGE
				: PREFIX_PATERN_ORANGE_DEFAULT;
	}

	public static String getMoovPhonePrefix() {
		return PREFIX_PATERN_MOOV != null ? PREFIX_PATERN_MOOV
				: PREFIX_PATERN_MOOV_DEFAULT;
	}

	public static String getMtnPhonePrefix() {
		return PREFIX_PATERN_MTN != null ? PREFIX_PATERN_MTN
				: PREFIX_PATERN_MTN_DEFAULT;
	}

	public static String getOrangePhone() {
		return CINETPAY_PHONE_NUMBERS.get(Purchase.PAYMENT_METHOD_ORANGE_MONEY);
	}

	public static String getMtnPhone() {
		return CINETPAY_PHONE_NUMBERS
				.get(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY);
	}

	public static String getMoovPhone() {
		return CINETPAY_PHONE_NUMBERS.get(Purchase.PAYMENT_METHOD_MOOV_FLOOZ);
	}

	// ----------------------------------
	public static String setOrangePhone(String value) {
		value = value.replaceAll("\\r?\\n", "").replaceAll("\\s", "");
		return CINETPAY_PHONE_NUMBERS.put(Purchase.PAYMENT_METHOD_ORANGE_MONEY,
				value);
	}

	public static String setMtnPhone(String value) {
		value = value.replaceAll("\\r?\\n", "").replaceAll("\\s", "");
		return CINETPAY_PHONE_NUMBERS.put(
				Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY, value);
	}

	public static String setMoovPhone(String value) {
		value = value.replaceAll("\\r?\\n", "").replaceAll("\\s", "");
		return CINETPAY_PHONE_NUMBERS.put(Purchase.PAYMENT_METHOD_MOOV_FLOOZ,
				value);
	}

	// -------------------------------
	static String setOrangePhonePrefix(String value) {
		value = value.replaceAll("\\r?\\n", "").replaceAll("\\s", "");
		return PREFIX_PATERN_ORANGE = value;
	}

	static String setMtnPhonePrefix(String value) {
		value = value.replaceAll("\\r?\\n", "").replaceAll("\\s", "");
		return PREFIX_PATERN_MTN = value;
	}

	static String setMoovPhonePrefix(String value) {
		value = value.replaceAll("\\r?\\n", "").replaceAll("\\s", "");
		return PREFIX_PATERN_MOOV = value;
	}

	public static void prepar(Context context) {
		boolean settingsLoaded = new Settings(context).load();
		if (!settingsLoaded) {
			prepar("");
		}
		Log.e("DEBUG", "prepar_end::Loadd::" + settingsLoaded + "-->"
				+ PREFIX_PATERN_ORANGE + ":::" + PREFIX_PATERN_ORANGE_DEFAULT);
		Log.e("DEBUG_PHONE", "prepar_end::Loadd::" + settingsLoaded + "-->"
				+ getOrangePhone() + ":::" + getMtnPhone() + "::"
				+ getMoovPhone());
	}

	public static void prepar(String config_json) {
		if (!TextUtils.isEmpty(config_json)) {
			try {
				new Settings().parseString(config_json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.e("DEBUG", "prepar_during::" + PREFIX_PATERN_ORANGE + ":::"
				+ PREFIX_PATERN_ORANGE_DEFAULT);
		if (TextUtils.isEmpty(SYNTAX_USSD_ORANGE))
			SYNTAX_USSD_ORANGE = SYNTAX_USSD_ORANGE_DEFAULT;
		if (TextUtils.isEmpty(SYNTAX_USSD_MTN))
			SYNTAX_USSD_MTN = SYNTAX_USSD_MTN_DEFAULT;
		if (TextUtils.isEmpty(PREFIX_PATERN_ORANGE))
			PREFIX_PATERN_ORANGE = PREFIX_PATERN_ORANGE_DEFAULT;
		if (TextUtils.isEmpty(PREFIX_PATERN_MTN))
			PREFIX_PATERN_MTN = PREFIX_PATERN_MTN_DEFAULT;
		if (TextUtils.isEmpty(PREFIX_PATERN_MOOV))
			PREFIX_PATERN_MOOV = PREFIX_PATERN_MOOV_DEFAULT;
	}

	public static void save(Context context) {
		try {
			new Settings(context).save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void appendCustomerPhone(Context context, String phone) {
		try {
			new Settings(context).appendCustomerPhone(phone);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Context context;
	final static String DIR_NAME = "Android/data/cnp/",
			CONFIG_FILE_NAME = "cnp.conf";
	File cacheDir;

	Settings() {
	}

	Settings(Context context) {
		this.context = context;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			// if SDCARD is mounted (SDCARD is present on device and mounted)
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					DIR_NAME);
		} else {
			// if checking on simulator the create cache dir in your application
			// context
			cacheDir = context.getCacheDir();
		}
	}

	void save() throws JSONException {
		JSONObject json = loadToJson();
		save(json);
	}

	void appendCustomerPhone(String phone) {
		try {
			JSONObject json = loadToJson();
			JSONObject jsonUserPhone = json.optJSONObject("user_phones") != null ? json
					.optJSONObject("user_phones") : new JSONObject();
			jsonUserPhone.put(phone, ToolKit.stickDate());
			json.put("user_phones", jsonUserPhone);
			save(json);
		} catch (Exception e) {

		}
	}

	private JSONObject loadToJson() {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		try {
			File file = new File(cacheDir, CONFIG_FILE_NAME);
			if (file.exists()) {
				FileInputStream inp = new FileInputStream(file);
				String data = Stream.streamToString(inp);
				json = new JSONObject(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	void save(JSONObject json) throws JSONException {
		JSONObject json_syntax = new JSONObject();
		JSONObject json_prefix = new JSONObject();
		JSONObject json_phone = new JSONObject();
		json_syntax.put(Purchase.PAYMENT_METHOD_ORANGE_MONEY,
				getOrangeUssdSyntax());
		json_syntax.put(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY,
				getMtnUssdSyntax());

		json_prefix.put(Purchase.PAYMENT_METHOD_ORANGE_MONEY,
				getOrangePhonePrefix());
		json_prefix.put(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY,
				getMtnPhonePrefix());
		json_prefix.put(Purchase.PAYMENT_METHOD_MOOV_FLOOZ,
				getMoovPhonePrefix());

		json_phone.put(Purchase.PAYMENT_METHOD_ORANGE_MONEY, getOrangePhone());
		json_phone.put(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY, getMtnPhone());
		json_phone.put(Purchase.PAYMENT_METHOD_MOOV_FLOOZ, getMoovPhone());

		json.put(FIELD_OPERATOR_SYNTAX, json_syntax);
		json.put(FIELD_OPERATOR_PREFIX, json_prefix);
		json.put(FIELD_OPERATOR_PHONES, json_phone);
		String config = json.toString();
		File file = new File(cacheDir, CONFIG_FILE_NAME);
		writeString(config, file);
	}

	boolean load() {
		try {
			File file = new File(cacheDir, CONFIG_FILE_NAME);
			if (!file.exists()) {
				Log.e("Cinetpay:Settings:load",
						"Oups config file doesn't exist at: " + file.exists());
				return false;
			}
			Log.i("Cinetpay:Settings:load",
					"Reading config file at: " + file.exists());
			FileInputStream inp = new FileInputStream(file);
			String data = Stream.streamToString(inp);
			prepar(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	void parseString(String config) throws JSONException {
		JSONObject json = new JSONObject(config);
		json = json.getJSONObject("service");
		JSONObject json_prefix = json.optJSONObject(FIELD_OPERATOR_PREFIX);
		if (json_prefix != null) {
			String orange = json_prefix
					.optString(Purchase.PAYMENT_METHOD_ORANGE_MONEY);
			String mtn = json_prefix
					.optString(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY);
			String moov = json_prefix
					.optString(Purchase.PAYMENT_METHOD_MOOV_FLOOZ);
			setOrangePhonePrefix(orange);
			setMtnPhonePrefix(mtn);
			setMoovPhonePrefix(moov);
		}
		JSONObject json_syntax = json.optJSONObject(FIELD_OPERATOR_SYNTAX);
		if (json_syntax != null) {
			SYNTAX_USSD_MTN = json_syntax.optString(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY);
			SYNTAX_USSD_ORANGE = json_syntax.optString(Purchase.PAYMENT_METHOD_ORANGE_MONEY);
		}
		JSONObject json_phones = json.optJSONObject(FIELD_OPERATOR_PHONES);
		if (json_phones != null) {
			String phoneOrange = json_phones
					.optString(Purchase.PAYMENT_METHOD_ORANGE_MONEY);
			String phoneMtn = json_phones
					.optString(Purchase.PAYMENT_METHOD_MTN_MOBILE_MONEY);
			String phoneMoov = json_phones
					.optString(Purchase.PAYMENT_METHOD_MOOV_FLOOZ);
			setOrangePhone(phoneOrange);
			setMtnPhone(phoneMtn);
			setMoovPhone(phoneMoov);
		}
	}

	public static void writeString(String content, File file) {
		try {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}