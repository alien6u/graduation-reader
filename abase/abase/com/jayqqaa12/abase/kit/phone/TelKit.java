package com.jayqqaa12.abase.kit.phone;

import java.util.List;
import java.util.UUID;

import android.net.wifi.WifiInfo;
import android.os.Build;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.ConfigKit;
import com.jayqqaa12.abase.kit.ManageKit;
import com.jayqqaa12.abase.kit.common.Validate;


/***
 * 获取手机信息
 * @author 12
 *
 */
public class TelKit
{
	/**
	 * 没有sim卡
	 */
	public static final int NETWORK_NONE = -1;
	public static final int NETWORK_OTHER = 0;
	public static final int CHINA_MOBILE = 10;
	public static final int CHINA_UNICOM = 11;
	public static final int CHINA_TELECOM = 12;

	public static final int NETWORK_2G = 2;
	public static final int NETWORK_3G = 3;
	public static final int NETWORK_4G = 4;

	/**
	 * 电话状态： 1.tm.CALL_STATE_IDLE=0 无活动 2.tm.CALL_STATE_RINGING=1 响铃
	 * 3.tm.CALL_STATE_OFFHOOK=2 摘机
	 */
	public static int getCallState()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getCallState();
	}

	/***
	 * get mac address
	 * 
	 * @return
	 */
	public static String getMacAddress()
	{
		WifiInfo info = ManageKit.getWifiManger().getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 电话方位：
	 * 
	 */
	public static CellLocation getCellLocation()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getCellLocation();
	}

	/**
	 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
	 * available.
	 */
	public static String getDeviceId()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getDeviceId();
	}

	/**
	 * 设备的软件版本号： 例如：the IMEI/SV(software version) for GSM phones. Return null if
	 * the software version is not available.
	 */
	public static String getDeviceSoftwareVersion()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getDeviceSoftwareVersion();//
	}

	/**
	 * 就是手机号： GSM手机的 MSISDN. Return null if it is unavailable.
	 */

	public static String getLine1Number()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getLine1Number();

	}

	/**
	 * 附近的电话的信息: 类型：List<NeighboringCellInfo>
	 * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
	 */

	public static List<NeighboringCellInfo> getNeighboringCellInfo()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getNeighboringCellInfo();
	}

	/**
	 * 获取ISO标准的国家码，即国际长途区号。 注意：仅当用户已在网络注册后有效。 在CDMA网络中结果也许不可靠。
	 */
	public static String getNetworkCountryIso()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getNetworkCountryIso();
	}

	/**
	 * MCC+MNC(mobile country code + mobile network code) 注意：仅当用户已在网络注册时有效。
	 * 在CDMA网络中结果也许不可靠。
	 */
	public static String getNetworkOperator()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getNetworkOperator();
	}

	/**
	 * 按照字母次序的current registered operator(当前已注册的用户)的名字 注意：仅当用户已在网络注册时有效。
	 * 在CDMA网络中结果也许不可靠。
	 */
	public static String getNetworkOperatorName()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getNetworkOperatorName();
	}

	/**
	 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络 1
	 * NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3 NETWORK_TYPE_HSDPA
	 * HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9 NETWORK_TYPE_HSPA HSPA网络 10
	 * NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO网络,
	 * revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6
	 * NETWORK_TYPE_1xRTT 1xRTT网络 7
	 */
	public static int getNetworkType()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getNetworkType();//
	}

	/**
	 * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA CDMA信号
	 */
	public static int getPhoneType()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getPhoneType();//
	}

	/**
	 * Returns the ISO country code equivalent for the SIM provider's country
	 * code. 获取ISO国家码，相当于提供SIM卡的国家码。
	 */
	public static String getSimCountryIso()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getSimCountryIso();//
	}

	/**
	 * Returns the MCC+MNC (mobile country code + mobile network code) of the
	 * provider of the SIM. 5 or 6 decimal digits.
	 * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. SIM卡的状态必须是
	 * SIM_STATE_READY(使用getSimState()判断).
	 */
	public static int getSimOperator()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();

		String operator = tm.getSimOperator();

		if (operator != null)
		{
			if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007"))
			{
				// 中国移动

				return CHINA_MOBILE;

			}
			else if (operator.equals("46001"))
			{
				// 中国联通

				return CHINA_UNICOM;

			}
			else if (operator.equals("46003"))
			{
				// 中国电信

				return CHINA_TELECOM;
			}
			else return NETWORK_OTHER;

		}

		return NETWORK_NONE;

	}

	/**
	 * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
	 */
	public static String getSimOperatorName()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getSimOperatorName();
	}

	/**
	 * SIM卡的序列号： 需要权限：READ_PHONE_STATE
	 */
	public static String getSimSerialNumber()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getSimSerialNumber();
	}

	/**
	 * SIM的状态信息： SIM_STATE_UNKNOWN 未知状态 0 SIM_STATE_ABSENT 没插卡 1
	 * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 SIM_STATE_PUK_REQUIRED
	 * 锁定状态，需要用户的PUK码解锁 3 SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
	 * SIM_STATE_READY 就绪状态 5
	 */
	public static int getSimState()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getSimState();// int
	}

	/**
	 * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
	 */
	public static String getSubscriberId()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getSubscriberId();
	}

	/**
	 * 取得和语音邮件相关的标签，即为识别符 需要权限：READ_PHONE_STATE
	 */
	public static String getVoiceMailAlphaTag()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.getVoiceMailAlphaTag();
	}

	/**
	 * 获取语音邮件号码： 需要权限：READ_PHONE_STATE
	 */

	public static String getVoiceMailNumber()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();

		return tm.getVoiceMailNumber();
	}

	/**
	 * ICC卡是否存在
	 */

	public static boolean hasIccCard()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.hasIccCard();
	}

	/**
	 * 是否漫游: (在GSM用途下)
	 */
	public static boolean isNetworkRoaming()
	{
		TelephonyManager tm = ManageKit.getTelephonyManager();
		return tm.isNetworkRoaming();
	}

	/**
	 * 构建一个uuid根据手机 imei 和 android id
	 * 
	 * @return
	 */
	public static String getUUID()
	{
		final TelephonyManager tm = ManageKit.getTelephonyManager();
		final String androidId = ""
				+ android.provider.Settings.Secure.getString(Abase.getContext().getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		return new UUID(androidId.hashCode(), ((long)(""+ tm.getDeviceId()).hashCode() << 32)).toString();

	}

	/**
	 * 返回 一个 id 也许是IMEI IMSI 等等
	 * 
	 * 只要 插过一次卡 以后就可以读出来
	 * 
	 * 变更后会自动 更新
	 * 
	 * @return
	 */
	public static String getId()
	{
		String uid = getDeviceId();
		if (uid == null) uid = TelKit.getSubscriberId();
		if (uid == null) uid = TelKit.getLine1Number();

		if (uid != null)
		{
			final String ABASE_DEVICE_ID = "ABASE_DEVICE_ID";
			String old = ConfigKit.getString(ABASE_DEVICE_ID, null);
			if (!Validate.equals(old, uid)) ConfigKit.setValue(ABASE_DEVICE_ID, uid);
		}
		else uid = ConfigKit.getString("ABASE_DEVICE_ID", null);

		return uid;
	}

	public static String getModel(){
		
		
		return Build.MODEL;
	}
	
public static String getBrand(){
		
		return Build.BRAND;
	}
}
