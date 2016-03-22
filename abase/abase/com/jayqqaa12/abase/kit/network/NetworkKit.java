package com.jayqqaa12.abase.kit.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;

import com.jayqqaa12.abase.kit.ManageKit;
import com.jayqqaa12.abase.kit.phone.TelKit;


/***
 * 判断网络类型  网络连接等
 * @author 12
 *
 */
public class NetworkKit  
{
	
	
	
	/***
	 * 返回 网络 类型    用 TelephonyManager 里面的常量进行 判断 
	 * 
	 * 只有 手机 网络好使 wifi 返回 时 或者  没有网络 返回 -1
	 * 
	 * @return
	 */
	public static int getNetworkType()
	{
		NetworkInfo info = ManageKit.getConnectivtyManager().getActiveNetworkInfo();

		return info!=null ?  info.getSubtype():TelKit.NETWORK_NONE;
	}
	
	/**
	 * 判断 当前 网络 是否是 edge 
	 * 
	 * @return
	 */
	public static boolean isEDGE()
	{

		NetworkInfo info = ManageKit.getConnectivtyManager().getActiveNetworkInfo();

		if (info != null && info.isAvailable())
		{
			if (TelephonyManager.NETWORK_TYPE_EDGE == info.getSubtype()) return true;
		}

		return false;
	}

	
	
	/***
	 * 返回 网络 类型   2G 3G or 4G
	 * @param networkType
	 * @return
	 */
	 public static int getNetworkClass() {
		 
		 int networkType =getNetworkType();
		 
	        switch (networkType) {
	            case TelephonyManager.NETWORK_TYPE_GPRS:
	            case TelephonyManager.NETWORK_TYPE_EDGE:
	            case TelephonyManager.NETWORK_TYPE_CDMA:
	            case TelephonyManager.NETWORK_TYPE_1xRTT:
	            case TelephonyManager.NETWORK_TYPE_IDEN:
	                return TelKit.NETWORK_2G;
	            case TelephonyManager.NETWORK_TYPE_UMTS:
	            case TelephonyManager. NETWORK_TYPE_EVDO_0:
	            case TelephonyManager.NETWORK_TYPE_EVDO_A:
	            case TelephonyManager.NETWORK_TYPE_HSDPA:
	            case TelephonyManager.NETWORK_TYPE_HSUPA:
	            case TelephonyManager. NETWORK_TYPE_HSPA:
//	            case TelephonyManager.NETWORK_TYPE_EVDO_B:
//	            case TelephonyManager.NETWORK_TYPE_EHRPD:
//	            case TelephonyManager.NETWORK_TYPE_HSPAP:
	                return TelKit.NETWORK_3G;
//	            case TelephonyManager.NETWORK_TYPE_LTE:
//	                return TelUtil.NETWORK_4G;
	            default:
	                return TelKit.NETWORK_OTHER;
	        }
	    }

	
	/**
	 * 判断 当前 网络 是否是 edge 
	 * 
	 * @return
	 */
	public static boolean isGPRS()
	{

		NetworkInfo info = ManageKit.getConnectivtyManager().getActiveNetworkInfo();

		if (info != null && info.isAvailable())
		{
			if (TelephonyManager.NETWORK_TYPE_GPRS == info.getSubtype()) return true;
		}

		return false;
	}
	
	
	
	/**
	 * 判断 wifi 是否 连接
	 * 
	 * @param cm
	 * @return
	 */
	public static boolean isWifiConnecting()
	{
		return ManageKit.getConnectivtyManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
	}

	/**
	 * 判断 mobile 网络 是否连接
	 * 
	 * @param cm
	 * @return
	 */
	public static boolean isMobileConnecting()
	{
		return ManageKit.getConnectivtyManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
	}

	/**
	 * 检测是否已经连接网络。
	 * 
	 * 需要 权限 android.permission.ACCESS_NETWORK_STATE.
	 * 
	 * @return 当且仅当连上网络时返回true,否则返回false。
	 */
	public static boolean isConnectingToInternet()
	{
		ConnectivityManager connectivityManager = ManageKit.getConnectivtyManager();
		if (connectivityManager == null) { return false; }
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return (info != null) && info.isAvailable();
	}

	/**
	 * 
	 * 获得 本机 ip 如果 没有联网 returne null
	 * 
	 * @return
	 */
	public static String getIpAddress()
	{
		try
		{
			String ipv4;
			List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface ni : nilist)
			{
				List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
				for (InetAddress address : ialist)
				{
					if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) { return ipv4; }
				}

			}

		}
		catch (SocketException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得 所有 网络的 流量 包括 发送和接受的
	 * 
	 * @return
	 */
	public static long getTotalBytes()
	{
		return getTotalRxBytes() + getTotalTxBytes();
	}

	/**
	 * 获取总的接受字节数，包含Mobile和WiFi等
	 * 
	 * @return
	 */
	public static long getTotalRxBytes()
	{
		return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getTotalRxBytes();
	}

	/**
	 * 总的发送字节数，包含Mobile和WiFi等
	 * 
	 * @return
	 */
	public static long getTotalTxBytes()
	{
		return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getTotalTxBytes();
	}

	/**
	 * Mobile总的发送和接受字节数，
	 * 
	 * @return
	 */

	public static long getTotalMobileBytes()
	{
		return getMobileRxBytes() + getMobileTxBytes();
	}

	/**
	 * wifi总的发送和接受字节数，
	 * 
	 * @return
	 */
	public static long getTotalWifiBytes()
	{
		return getTotalBytes() - getTotalMobileBytes();
	}

	/**
	 * 获取通过Mobile连接收到的字节总数，不包含WiFi
	 * 
	 */
	public static long getMobileRxBytes()
	{
		return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileRxBytes();
	}

	/**
	 * 获取通过Mobile连接收到的字节总数，不包含WiFi
	 * 
	 */
	public static long getMobileTxBytes()
	{
		return TrafficStats.getMobileTxBytes() == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getMobileTxBytes();
	}

	// ////////////////////////---------------UID--------- ////////////////

	/**
	 * 获得 uid 所有 网络的 流量 包括 发送和接受的
	 * 
	 * @return
	 */
	public static long getUidTotalBytes(int uid)
	{
		return getUidRxBytes(uid) + getUidTxBytes(uid);
	}

	/**
	 * 获取uid 的接受字节数，包含Mobile和WiFi等
	 * 
	 * @return
	 */
	public static long getUidRxBytes(int uid)
	{
		return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getUidRxBytes(uid);
	}

	/**
	 * 获取uid 的发送字节数，包含Mobile和WiFi等
	 * 
	 * @return
	 */
	public static long getUidTxBytes(int uid)
	{
		return TrafficStats.getUidTxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getUidTxBytes(uid);
	}

}
