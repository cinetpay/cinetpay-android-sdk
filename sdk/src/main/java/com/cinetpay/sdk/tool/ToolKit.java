package com.cinetpay.sdk.tool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
public final class ToolKit {
	public static String INTENT_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	public static String getAppLable(Context context) {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = null;
		try {
			applicationInfo = packageManager.getApplicationInfo(
					context.getApplicationInfo().packageName, 0);
		} catch (final NameNotFoundException e) {
		}
		return (String) (applicationInfo != null ? packageManager
				.getApplicationLabel(applicationInfo) : "Unknown");
	}

	@SuppressLint("SimpleDateFormat")
	public static String stickDate() {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		return f.format(date);
	}

	public static void vibrate(Context context, int duration) {
		if (havePermission(context, permission.VIBRATE)) {
			((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
					.vibrate(duration);

		}
	}

	public static void closeKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus() != null ? activity.getCurrentFocus()
						.getWindowToken() : null, 0);

	}

	public static boolean havePermission(Context context, String permission) {
		int res = context.checkCallingOrSelfPermission(permission);
		return (res == PackageManager.PERMISSION_GRANTED);
	}

	public static boolean isNetworkConnected(Context context) {
		final ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static void phoneCall(String n, Activity currentActivity) {
		char ench[] = n.toCharArray();
		String tel = "";
		for (int i = 0; i < ench.length; i++) {
			if (ench[i] == '#')
				tel = tel + Uri.encode("#");
			else
				tel = tel + ench[i];
		}
		String toDial = "tel:" + tel;// msgbox(Intent.ACTION_ALL_APPS);
		currentActivity.startActivityForResult(
				new Intent(havePermission(currentActivity,
						permission.CALL_PHONE) ? Intent.ACTION_CALL
						: Intent.ACTION_DIAL, Uri.parse(toDial)), 1024);

	}

	@SuppressLint("InlinedApi")
	public static void phoneDial(String n, Activity currentActivity) {
		char ench[] = n.toCharArray();
		String tel = "";
		for (int i = 0; i < ench.length; i++) {
			if (ench[i] == '#')
				tel = tel + Uri.encode("#");
			else
				tel = tel + ench[i];
		}
		String toDial = "tel:" + tel;// msgbox(Intent.ACTION_ALL_APPS);
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(toDial));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		currentActivity.startActivityForResult(intent, 1024);

	}

	public static String getOtpCodeOM(String message) {
		String word = message;
		List<String> out = new ArrayList<String>();
		Pattern p = Pattern.compile("(\\d){4}");// search le or Le ignore case
												// "(?i)le"
		Matcher m = p.matcher(word);
		while (m.find()) {
			out.add(m.group());
			System.out.println(m.group());
		}
		return out.size() > 0 ? out.get(0) : "";
	}

	public static String getOtpCodeMOMO(String message) {
		String word = message;
		List<String> out = new ArrayList<String>();
		Pattern p = Pattern.compile("(\\d){5}");// search le or Le ignore case
												// "(?i)le"
		Matcher m = p.matcher(word);
		while (m.find()) {
			out.add(m.group());
			System.out.println(m.group());
		}
		return out.size() > 0 ? out.get(0) : "";
	}

	public static class Screen {
		public static void setPortrait(Activity activity) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		public static void setLandScape(Activity activity) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		public static void setNoSensor(Activity activity) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}

		public static void setFullSensor(Activity activity) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}

	public static class Control {

		public static boolean isPhoneCinetPayCompatible(String phone) {
			return isOrangePhone(phone) || isMoovPhone(phone)
					|| isMtnPhone(phone);
		}

		public static boolean isOrangePhone(String phone) {
			if (phone == null)
				return false;
			return phone.matches(Settings.getOrangePhonePrefix());
		}

		public static boolean isMtnPhone(String phone) {
			if (phone == null)
				return false;
			return phone.matches(Settings.getMtnPhonePrefix());
		}

		public static boolean isMoovPhone(String phone) {
			if (phone == null)
				return false;
			return phone.matches(Settings.getMoovPhonePrefix());
		}

		public static String extractPhone(String phone) {
			return phone.replace(" ", "").replace("-", "")
					.replaceFirst("^(00225)", "").replaceFirst("^(\\+225)", "");
		}

		public static boolean isCinetPayContext(Context context) {
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			boolean isoCI = manager.getSimCountryIso().toLowerCase()
					.equals("ci")
					&& manager.getSimOperator().matches("^(612).+");
			return isoCI;
		}

	}

	public static class Memory {
		public static void SavePreferences(Context context, String File,
				String key, String value, int mode) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					File, mode);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key, value);
			editor.commit();

		}

		public static String LoadPreferences(Context context, String File,
				String key, String deflt) {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					File, 0);
			return sharedPreferences.getString(key, deflt);
		}
	}

	public static class Word {

		// String.format("%1$s %2$s %2$s %3$s", "a", "b", "c");
		public static String begginByUperCase(String word) {
			if (word.length() > 1) {
				String beggin = word.substring(0, 1).toUpperCase(
						Locale.getDefault());
				word = beggin + word.substring(1);
			}
			return word;
		}

		public static String toSentense(String word, String endingPontuation) {
			word = begginByUperCase(word);
			int index = word.indexOf(endingPontuation);
			if (index <= 0)
				return word + endingPontuation;

			return word;
		}

		public static String ShortWord(String word, int max) {
			if (word.length() <= max)
				return word;
			return word.substring(0, max) + "...";
		}

		/**
		 * SweetNumber is used in order to get sweet Number definit like: if a<9
		 * return "0a" else return just the number a
		 * 
		 * @param a
		 * @return a String that represent a "sweet" numeric value of that
		 *         number.
		 */
		public static String sweetNumber(int a) {
			if (a > 9)
				return "" + a;
			else
				return "0" + a;
		}

		public static String adjustNumber(float a) {
			if ((int) a == a)
				return "" + (int) a;
			else
				return "" + a;
		}

		public static boolean isInteger(String word) {
			try {
				int a = Integer.valueOf(word);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

}
