package com.cinetpay.sdk.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * From Istat Lib open Dev: allow to listen incomming SMS and decode it to an
 * SmsPart instance than you can manage easily.
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
public class SmsWatcher {
	public static String INTENT_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	Context context;

	public SmsWatcher(Context context) {
		this.context = context;
	}

	public static SmsPart decode(Context context, Intent intent) {

		if (intent.getAction().equals(INTENT_SMS_RECEIVED)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");

				final SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				if (messages.length > -1) {
					String msg = "";
					for (int i = 0; i < messages.length; i++) {
						msg = msg + messages[i].getMessageBody();
					}
					return new SmsPart(
							messages[0].getDisplayOriginatingAddress(), msg);

				}
			}
		}
		return new SmsPart(null, null);
	}

	public void setSmsListener(SmsListener listener, int priority) {
		this.mListener = listener;
		IntentFilter filter = new IntentFilter(INTENT_SMS_RECEIVED);
		filter.setPriority(priority);
		context.registerReceiver(mIncomeReceiver, filter);
	}

	public void setSmsListener(SmsListener listener) {
		this.mListener = listener;
		IntentFilter filter = new IntentFilter(INTENT_SMS_RECEIVED);
		context.registerReceiver(mIncomeReceiver, filter);
	}

	public boolean unregisterSmsListener() {
		boolean out = mListener != null;
		try {
			if (out) {
				context.unregisterReceiver(mIncomeReceiver);
				mListener = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public interface SmsListener {
		public void onReceiveSms(SmsPart sms, BroadcastReceiver receiver);
	}

	private BroadcastReceiver mIncomeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mListener.onReceiveSms(decode(context, intent), this);
		}
	};
	private SmsListener mListener;

	public static class SmsPart {
		public String address = "", body = "";

		public SmsPart(String address, String body) {
			this.address = address;
			this.body = body;
		}
	}
}
