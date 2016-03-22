package com.jayqqaa12.abase.kit;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.jayqqaa12.abase.core.Abase;

public class ManageKit
{
	
	public static DownloadManager getDownloadManger(){
		return  (DownloadManager) Abase.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
	}
	

	public static DevicePolicyManager getDevicePolicyManager()
	{
		return (DevicePolicyManager) Abase.getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
	}

	public static KeyguardManager getKeyguardManager()
	{
		return (KeyguardManager) Abase.getContext().getSystemService(Context.KEYGUARD_SERVICE);
	}

	public static void registAdminDevice(Context context, Class<? extends DeviceAdminReceiver> admin)
	{
		DevicePolicyManager manager = ManageKit.getDevicePolicyManager();
		ComponentName adminName = new ComponentName(context, admin);
		// is exist admin active ?
		if (!manager.isAdminActive(adminName))
		{
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
			context.startActivity(intent);
		}

	}
	
	public static AlarmManager getAlarmManager(){
		
		return (AlarmManager)  Abase.getContext().getSystemService(Context.ALARM_SERVICE);
	}

	public static LocationManager getLocationManager()
	{
		return (LocationManager) Abase.getContext().getSystemService(Context.LOCATION_SERVICE);
	}

	public static WindowManager getWindowManager()
	{

		return (WindowManager) Abase.getContext().getSystemService(Context.WINDOW_SERVICE);
	}

	public static ActivityManager getActivityManager()
	{
		return (ActivityManager) Abase.getContext().getSystemService(Context.ACTIVITY_SERVICE);
	}

	public static NotificationManager getNotificationManager()
	{
		return (NotificationManager) Abase.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public static PackageManager getPackManager()
	{

		return Abase.getContext().getPackageManager();
	}

	public static Bundle getMetaData()
	{
		try
		{
			return getPackManager().getApplicationInfo(Abase.getContext().getPackageName(), PackageManager.GET_META_DATA).metaData;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static ConnectivityManager getConnectivtyManager()
	{
		return (ConnectivityManager) Abase.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public static PowerManager getPowerManager()
	{
		return (PowerManager) Abase.getContext().getSystemService(Context.POWER_SERVICE);
	}

	public static TelephonyManager getTelephonyManager()
	{
		return (TelephonyManager) Abase.getContext().getSystemService(Context.TELEPHONY_SERVICE);
	}

	public static WifiManager getWifiManger()
	{
		return (WifiManager) Abase.getContext().getSystemService(Context.WIFI_SERVICE);
	}

	public static LayoutInflater getInflater()
	{
		return (LayoutInflater) Abase.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public static InputMethodManager getInputMethodManager()
	{
		return ((InputMethodManager) Abase.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
	}

}
