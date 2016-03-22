package com.jayqqaa12.abase.kit.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

public class ShortcutKit
{

	/**
	 * 创建一个 快捷方式
	 * 
	 * 点击后 回到 Task Top
	 * 
	 * @param activity
	 * @param iconResId
	 * @param appnameResId
	 */
	public static void createShortCut(Context context, int iconResId, int appnameResId)
	{

		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appnameResId));
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context.getApplicationContext(), iconResId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
				new Intent(context.getApplicationContext(), context.getClass()));

		context.sendBroadcast(shortcutintent);

	}

	/**
	 * 创建一个 快捷方式
	 * 
	 * 入口 是 传入的 activity
	 * 
	 * @param activity
	 * @param iconResId
	 * @param appnameResId
	 */
	public static void createShortCutToMain(Activity activity, int iconResId, int appnameResId)
	{

		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, activity.getString(appnameResId));
		Parcelable icon = Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), iconResId);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口

		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(activity.getApplicationContext(), activity.getClass()));

		activity.sendBroadcast(shortcutintent);

	}
}