package com.jayqqaa12.abase.kit.phone;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.jayqqaa12.abase.core.Abase;

public class SmsKit
{

	public static Context getContext()
	{
		return Abase.getContext();
	}

	public static void sendSms(String address, String text)
	{
		SmsManager smsmanager = SmsManager.getDefault();
		smsmanager.sendTextMessage(address, null, text, null, null);

	}

	public static Object[] getPdus(Intent intent)
	{
		return (Object[]) intent.getExtras().get("pdus");
	}

	public static SmsMessage getSmsMessage(Object pdu)
	{
		return SmsMessage.createFromPdu((byte[]) pdu);
	}

}
