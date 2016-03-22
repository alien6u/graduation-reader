package com.jayqqaa12.abase.kit.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.common.L;
import com.jayqqaa12.abase.kit.common.Provider;
import com.jayqqaa12.abase.kit.common.Validate;
import com.jayqqaa12.abase.kit.phone.TelKit;

/**
 * 1.点击"Network"将输出本机所处的网络环境。 2.点击"WAP"将设定 移动网络接入点为CMWAP。 3.点击"GPRS"将设定
 * 移动网络接入点为CMNET。 注：自定义移动网络接入点的前提是“设置”→“无线和网络”→“移动网络”处已打勾。
 * 
 * 必需 为 系统应用 或者有系统权限
 */
public class ApnKit  
{

	

	/**
	 * 因为 wap 访问不了 www 所以 要改一下 接入点
	 * 必需 为 系统应用 或者有系统权限
	 * 暂不支持  中国电信
	 */
	public static  void changeWAPToNet()
	{
		int operator = TelKit.getSimOperator();

		L.i("operator =  " + operator + " net work type =" + NetworkKit.getNetworkType());
		L.i(" now apn is =" + ApnKit.isWapApn());
		
		if(!ApnKit.isWapApn()) return ;

		// 以下 几种 网络 访问 不了 www
		switch (NetworkKit.getNetworkType())
		{
		case TelephonyManager.NETWORK_TYPE_GPRS: // mobile and unicom 2g gprs

			if (operator == TelKit.CHINA_MOBILE)
			{
				L.i("china mobile ");
				ApnKit.setCmNetApn();

			}
			else if (operator == TelKit.CHINA_UNICOM)
			{
				L.i("china unicom");
				ApnKit.setUniNet();
			}
			break;

		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSDPA:

			L.i("china unicom 3g wap ");
			ApnKit.setUni3gNet();
			break;

		}

	}
	
	
	/***
	 * 设置 中国移动的接入点 为 cmnet
	 */
	public static void setCmNetApn()
	{

		ContentValues cmnet = new ContentValues();
		cmnet.put("name", "中国移动");
		cmnet.put("apn", "cmnet");
		cmnet.put("type", "default");
		cmnet.put("mmsprotocol", "2.0");
		cmnet.put("mcc", "460");
		cmnet.put("mnc", "02");
		cmnet.put("numeric", "46002");

		setDefaultAPN(cmnet);
	}

	/**
	 * 设置 中国移动的 接入点 为 cmwap
	 */
	public static void setCmWapApn()
	{
		ContentValues cvWAP = new ContentValues();
		cvWAP.put("name", "移动梦网");
		cvWAP.put("apn", "cmwap");
		cvWAP.put("type", "default");
		cvWAP.put("proxy", "10.0.0.172");
		cvWAP.put("port", "80");
		cvWAP.put("mmsproxy", "10.0.0.172");
		cvWAP.put("mmsport", "80");
		cvWAP.put("mmsprotocol", "2.0");
		cvWAP.put("mmsc", "http://mmsc.monternet.com");
		cvWAP.put("mcc", "460");
		cvWAP.put("mnc", "02");
		cvWAP.put("numeric", "46002");

		setDefaultAPN(cvWAP);

	}

	/**
	 * 中国联通 2g net
	 */
	public static void setUniNet()
	{
		ContentValues uniNet = new ContentValues();
		uniNet.put("name", "中国联通");
		uniNet.put("apn", "uninet");
		uniNet.put("type", "default");
		uniNet.put("mmsprotocol", "2.0");
		uniNet.put("mcc", "460");
		uniNet.put("mnc", "01");
		uniNet.put("numeric", "46001");
		setDefaultAPN(uniNet);
	}

	/**
	 * 中国联通 3g net
	 */
	public static void setUni3gNet()
	{
		ContentValues uni3gNet = new ContentValues();
		uni3gNet.put("name", "中国联通3g");
		uni3gNet.put("apn", "3gnet");
		uni3gNet.put("type", "default");
		uni3gNet.put("mmsprotocol", "2.0");
		uni3gNet.put("mcc", "460");
		uni3gNet.put("mnc", "01");
		uni3gNet.put("numeric", "46001");

		setDefaultAPN(uni3gNet);
	}

	/**
	 * 
	 * 判断 当前 apn 是否 为 wap
	 * 
	 */
	public static boolean isWapApn()
	{
		boolean result = false;

		ContentResolver contentResolver = Abase.getContext().getContentResolver();
		Cursor cursor = contentResolver.query(Provider.PREFERRED_APN_URI, null, null, null, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				String apn = cursor.getString(cursor.getColumnIndex("apn"));
				if (apn.contains("wap")) result = true;
			}
			if (cursor != null) cursor.close();
		}

		return result;
	}

	private static void setDefaultAPN(ContentValues value)
	{
		int _id = findAPNId(value);
		if (_id == -1)
		{
			_id = insertAPN(value);
		}
		ContentValues values = new ContentValues();
		values.put("apn_id", _id);
		ContentResolver resolver = Abase.getContext().getContentResolver();
		resolver.update(Provider.PREFERRED_APN_URI, values, null, null);
	}

	private static int findAPNId(ContentValues cv)
	{
		int id = -1;
		ContentResolver contentResolver = Abase.getContext().getContentResolver();
		Cursor cursor = contentResolver.query(Provider.ALL_APN_URI, null, null, null, null);
		if (cursor != null)
		{

			while (cursor.moveToNext())
			{
				String apn = cursor.getString(cursor.getColumnIndex("apn"));
				String numeric = cursor.getString(cursor.getColumnIndex("numeric"));

				if ( Validate.equals(cv.getAsString("apn"), apn)
						&& Validate.equals(cv.getAsString("numeric"), numeric))
				{
					id = cursor.getShort(cursor.getColumnIndex("_id"));
					break;
				}
			}
			if (cursor != null) cursor.close();
		}

		return id;
	}

	private static int insertAPN(ContentValues value)
	{
		int apn_Id = -1;
		ContentResolver resolver = Abase.getContext().getContentResolver();

		Uri newRow = resolver.insert(Provider.ALL_APN_URI, value);
		if (newRow != null)
		{
			Cursor cursor = resolver.query(newRow, null, null, null, null);
			int idIdx = cursor.getColumnIndex("_id");
			cursor.moveToFirst();
			apn_Id = cursor.getShort(idIdx);

			if (cursor != null) cursor.close();
		}

		return apn_Id;
	}

}
