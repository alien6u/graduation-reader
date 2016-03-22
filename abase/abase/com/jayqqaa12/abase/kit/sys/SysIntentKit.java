package com.jayqqaa12.abase.kit.sys;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

/**
 * 
 ******* System Action_View************
 * 
 * 如有 特殊 需求 请改 FLAG
 * 
 * @author jayqqaa12
 * @date 2013-5-15
 */
public class SysIntentKit
{

	/**
	 * FLAG_ACTIVITY_NEW_TASK
	 * 
	 * @param activity
	 * @param path
	 */
	public static void install(Context activity, String path)
	{
		Intent intent = getInstallIntent(path);
		intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}

	public static Intent getInstallIntent(String path)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
		return intent;
	}

	/**
	 * FLAG_ACTIVITY_NEW_TASK
	 * 
	 * @param path
	 * @return
	 */
	public static void uninstall(Context context, String pkg)
	{
		if (pkg == null) { return; }
		Intent intent = getUnInstallIntent(pkg);
		intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static Intent getUnInstallIntent(String pkg)
	{
		Uri uri = Uri.fromParts("package", pkg, null);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		return intent;
	}

	public static void callPhone(Context context, String number)
	{
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));

		context.startActivity(intent);
	}

	public static void goPhone(Context context, String number)
	{
		Uri uri = Uri.parse("tel:" + number);
		Intent intent = new Intent(Intent.ACTION_DIAL, uri);
		context.startActivity(intent);
	}

	/**
	 * 直接发短信
	 * 
	 * @param context
	 * @param number
	 * @param smsBody
	 */
	public static void sendSms(Context context, String number, String smsBody)
	{

		Uri smsToUri = Uri.parse("smsto:" + number);

		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

		intent.putExtra("sms_body", smsBody);
		context.startActivity(intent);

	}

	/**
	 * 进入 发短信界面
	 * 
	 * @param context
	 * @param number
	 */
	public static void intoSms(Context context, String number)
	{
		Uri smsToUri = Uri.parse("smsto:" + number);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", "");
		context.startActivity(intent);

	}

	/**
	 * 返回 桌面
	 * 
	 * @param context
	 */
	public static void goHome(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);

	}

	/**
	 * 重启 手机 需要 系统级 签名
	 * 
	 * @param context
	 */
	public static void reboot(Context context)
	{

		Intent intent = new Intent(Intent.ACTION_REBOOT);
		intent.putExtra("nowait", 1);
		intent.putExtra("interval", 1);
		intent.putExtra("window", 0);
		context.sendBroadcast(intent);

	}

	/**
	 * 进入 设置的 管理 app 界面
	 */
	public static void goSettingsManageApp(Context context)
	{
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.applications.ManageApplications");
		context.startActivity(intent);

	}

	/**
	 * 进入 设置的 管理 app 界面
	 */
	public static void goSettings(Context context)
	{
		Intent intent = new Intent(Settings.ACTION_SETTINGS);
		context.startActivity(intent);

	}

	/**
	 * 进入网络设置 界面
	 * 
	 * @param context
	 */
	public static void intoNetworkSetting(Context context)
	{
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

	}

	/**
	 * 打开 图片 查看界面
	 * 
	 * @param context
	 */
	public static void goImageView(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		context.startActivity(intent);

	}

	public static void goWeb(Context context, String url)
	{
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);

	}

}
