package com.jayqqaa12.abase.kit.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.ManageKit;

/**
 * 手机和 内存获取
 *
 * */
public class MemoryKit 
{

	/**
	 * ROM 获取手机可用的内存空间 返回 单位 M
	 * 
	 * @return
	 */
	public static String getAvailablePhoneRom()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(Abase.getContext(), (availableBlocks * blockSize));
	}

	/**
	 * 获取当前可用内存大小
	 * @return
	 */
	public static String getAvailableRam()
	{

		ActivityManager am = ManageKit.getActivityManager();
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return Formatter.formatFileSize(Abase.getContext(), mi.availMem);// 将获取的内存大小规格化
	}

	/**
	 * 返回 总 Ram
	 * 
	 * @return
	 */
	public static String getTotalRam()
	{
		String fileName = "/proc/meminfo";// 系统内存信息文件
		String info;
		String[] arrayOfString;
		long totalMemory = 0;

		try
		{
			FileReader localFileReader = new FileReader(fileName);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			info = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			
			info = info.replaceAll(" ", "");
			info =info.replace("kB","");
			info=info.replace("MemTotal:","");
			totalMemory = Integer.valueOf(info).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		}
		catch (IOException e)
		{
		}
		return Formatter.formatFileSize(Abase.getContext(), totalMemory);// Byte转换为KB或者MB，内存大小规格化
	}

}