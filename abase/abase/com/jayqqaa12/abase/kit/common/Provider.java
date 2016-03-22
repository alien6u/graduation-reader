package com.jayqqaa12.abase.kit.common;

import android.net.Uri;

public class Provider
{
	public static final String CONTENT ="content://";
	public static final String SPACE_DELETE="/delete";
	public static final String SPACE_INSERT="/insert";
	public static final String SPACE_QUERY ="/query";
	
	public static final String DELETE="delete";
	public static final String INSERT="insert";
	public static final String QUERY ="query";
	public static final String UPDATE = "update";
	
	/** 全部的APN */
	public static final Uri ALL_APN_URI = Uri.parse("content://telephony/carriers");
	/** 当前default的APN记录。 */
	public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	

}
