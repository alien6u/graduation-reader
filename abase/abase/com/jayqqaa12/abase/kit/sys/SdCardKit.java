package com.jayqqaa12.abase.kit.sys;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.TextKit;

/**
 * 
 * @author 12
 *
 */
public class SdCardKit  
{
	public static String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
	/**
	 * sd card
	 * @return
	 */
	public static boolean isCanUseSdCard() { 
	    try { 
	        return Environment.getExternalStorageState().equals( 
	                Environment.MEDIA_MOUNTED); 
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
	    return false; 
	} 
	
	
	/**
	 * 获取多张 sd卡的 路径
	 * @return
	 */
	public static String [] getSdCardPath(){
		
		StorageManager sm = (StorageManager)  Abase.getContext().getSystemService(Context.STORAGE_SERVICE);
		String[] paths=null;
		try
		{
			paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return paths;
	}
	
	/**
	 * 获取手机可用的内存空间 返回 单位 G  如  “1.5GB”
	 * 
	 * @return
	 */
	public static String getAvailableSDRomString()
	{
		if (!isCanUseSdCard()) return "0";
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(Abase.getContext(), availableBlocks * blockSize);
	}
	
	/**
	 * 同 getAvailableSDRomString 返回的是 int
	 * 
	 * @return
	 */
	public static float getAvailableSDRom()
	{

		if (!isCanUseSdCard()) return 0;
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long size = availableBlocks * blockSize;
		return Float.parseFloat( TextKit.getDataSize(size));
		
	}
	
}
