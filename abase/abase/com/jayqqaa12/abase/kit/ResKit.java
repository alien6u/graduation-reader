package com.jayqqaa12.abase.kit;

import android.content.Context;

import com.jayqqaa12.abase.core.Abase;

/**
 * 
 * 资源管理器
 * 
 * 
 */
public class ResKit
{

	/**
	 * 根据资源的名字获取它的ID
	 * 
	 * @param name
	 *            要获取的资源的名字
	 * @param defType
	 *            资源的类型，如drawable, string 。。。
	 * @return 资源的id
	 */
	public int getResId(String name, String defType)
	{
		String packageName = getContext().getApplicationInfo().packageName;
		return getContext().getResources().getIdentifier(name, defType, packageName);

	}

	public static int[] getIntegerArray(int id)
	{

		return getContext().getResources().getIntArray(id);

	}

	public static String[] getStringArray(int id)
	{
		return getContext().getResources().getStringArray(id);
	}

	public static String[] getLocates()
	{
		return getContext().getAssets().getLocales();

	}

	
	public static Context getContext()
	{

		return Abase.getContext();

	}

	public static String[] getStringArray(int[] ids)
	{
		String [] s = new String[ids.length];
		
		int i =0;
		for(int id:ids )
		{
			s[i++] = getContext().getString(id);
		}
		
		return s;
		
	}

}
