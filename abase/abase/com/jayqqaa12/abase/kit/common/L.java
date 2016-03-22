package com.jayqqaa12.abase.kit.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;

import android.util.Log;

/**
 * 打印 log
 * 
 * @author jayqqaa12
 * @date 2013-6-5
 */
public class L
{
	/**
	 * 改变 这个 值  当 设置 LOG_LEVEL = 0 时 不打印 任何 日志
	 */
	public static final int LOG_LEVEL = 10;
	
	
	private static final int ERROR = 6;
	private static final int DEBUG = 5;
	private static final int WARN = 4;
	private static final int INFO = 3;
	private static final int VERBOSE = 2;
	private static final int PRINTLN = 1;
	
	
	

	public static void d( Object message)
	{
		if (LOG_LEVEL > DEBUG) Log.d(TAG.DEBUG, message+"");
	}

	public static void e( Object message)
	{
		if (LOG_LEVEL > ERROR) Log.e(TAG.ERROR,  message+"");
	}
	public static void i( Object message)
	{
		if (LOG_LEVEL > INFO) Log.i(TAG.INFO,  message+"");
	}
	public static void v( Object message)
	{
		if (LOG_LEVEL > VERBOSE) Log.v(TAG.VERBOSE,  message+"");
	}
	public static void w( Object message)
	{
		
		if (LOG_LEVEL > WARN) Log.w(TAG.WARN,  message+"");
	}


	public static void d(String tag, Object message)
	{
		if (LOG_LEVEL > DEBUG) Log.d(tag,  message+"");
	}

	public static void e(String tag, Object message)
	{
		if (LOG_LEVEL > ERROR) Log.e(tag,  message+"");
	}

	public static void i(String tag, Object message)
	{
		if (LOG_LEVEL > INFO) Log.i(tag,  message+"");
	}

	public static void v(String tag, Object message)
	{
		if (LOG_LEVEL > VERBOSE) Log.v(tag,  message+"");
	}

	public static void w(String tag, Object message)
	{
		if (LOG_LEVEL > WARN) Log.w(tag,  message+"");
	}

	public static void println(int priority, String tag, Object message)
	{
		if (LOG_LEVEL > PRINTLN) Log.println(priority, tag,  message+"");
	}
	
	public static String getExceptionStackTrace(Throwable e){
		
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		String result = writer.toString();

		return result;
	}

}
