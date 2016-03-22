package com.jayqqaa12.abase.kit.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.os.RemoteException;
import android.provider.CallLog.Calls;

import com.android.internal.telephony.ITelephony;
import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.ManageKit;
import com.jayqqaa12.abase.kit.phone.CallKit;

/**
 * 
 * notification 工具集
 * 
 */
public class NotifiyKit
{

	public static void deleteNotification(int id)
	{
		ManageKit.getNotificationManager().cancel(id);;
	}

	/***
	 * 
	 * @param iconId
	 * @param notifiTitile
	 * @param title
	 * @param desc
	 * @param contentIntent
	 * @param context
	 */
	public static void showNotification( Context context,int id,int iconId, String title, String desc, PendingIntent contentIntent,boolean canCancel)
	{
		NotificationManager manager = ManageKit.getNotificationManager();
		Notification notification = new Notification(iconId, title, System.currentTimeMillis());
		// sound and vibrate and led light
		notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
		
		if(canCancel)notification.flags |= Notification.FLAG_AUTO_CANCEL;
		else  notification.flags |= Notification.FLAG_NO_CLEAR;
	
		notification.setLatestEventInfo(context, title, desc, contentIntent);
		manager.notify(id, notification);
	}

	/**
	 * 删除 电话的 notifi
	 */
	public static void deleteMissedCallsNotification()
	{

		try
		{

			ITelephony telephony = CallKit.getITelephony();
			if (telephony != null)
			{
				telephony.cancelMissedCallsNotification();
				// 修改 数据库记录
				StringBuilder where = new StringBuilder("type=");
				where.append(Calls.MISSED_TYPE);
				where.append(" AND new=1");
				ContentValues values = new ContentValues(1);
				values.put(Calls.NEW, "0");
				Abase.getContext().getContentResolver().update(Calls.CONTENT_URI, values, where.toString(), null);

			}

		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (RemoteException e)
		{
			e.printStackTrace();
		}

	}

}
