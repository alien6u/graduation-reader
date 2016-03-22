package com.jayqqaa12.abase.kit;

import java.text.DecimalFormat;

public class TextKit 
{


	/**
	 * 转换文件大小
	 * 返回byte的数据大小对应的文本
	 * @param bytes
	 * @return
	 */
	public static String getDataSize(long bytes)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (bytes < 1024)
		{
			fileSizeString = df.format((double) bytes) + "";
		}
		else if (bytes < 1048576)
		{
			fileSizeString = df.format((double) bytes / 1024) + "";
		}
		else if (bytes < 1073741824)
		{
			fileSizeString = df.format((double) bytes / 1048576) + "";
		}
		else
		{
			fileSizeString = df.format((double) bytes / 1073741824) + "";
		}
		return fileSizeString;
	}

	/**
	 * 
	 * 
	 * @param kb
	 * @return
	 */
	public static String getKbToMb(long kb)
	{
		if (kb <= 0) { return "0.0"; }
		DecimalFormat formater = new DecimalFormat("####.0");
		float mb = kb / 1024f;
		String result = formater.format(mb) + "";
		if (result.startsWith(".")) result = "0" + result;

		return result.equals("0.0") ? "0.1" : result;

	}

	/**
	 * 
	 * 
	 * @param bits
	 * @return
	 */
	public static String getBitToMb(long bits)
	{
		if (bits <= 0) { return "0.0"; }
		DecimalFormat formater = new DecimalFormat("####.0");
		float mb = bits / 8f / 1024f / 1024f;
		String result = formater.format(mb) + "";

		if (result.startsWith(".")) result = "0" + result;

		return result.equals("0.0") ? "0.1" : result;

	}

	/**
	 * 
	 * 
	 * @param bytes
	 * @return
	 */
	public static String getByteToMb(long bytes)
	{
		if (bytes <= 0) { return "0.0"; }
		DecimalFormat formater = new DecimalFormat("####.0");
		float mb = bytes / 1024f / 1024f;
		String result = formater.format(mb) + "";

		if (result.startsWith(".")) result = "0" + result;

		return result.equals("0.0") ? "0.1" : result;

	}

}
