package com.jayqqaa12.abase.kit.sys;

import java.lang.reflect.Method;



/**
 * 获得 android.os.SystemProperties 相关 属性
 * 
 * @author jayqqaa12
 *
 */
public class SysPropKit {

	
	/**
	 * android系统  唯一 id  可能会 重复 
	 * @return
	 */
	public static String getAndroidId()
	{
		
		
		String serialnum = null;                                                                                                                                        
		try {                                                           
		 Class<?> c = Class.forName("android.os.SystemProperties"); 
		 Method get = c.getMethod("get", String.class, String.class );     
		 serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );   
		}                                                                                
		catch (Exception ignored)                                                        
		{                              
		}
		
		return serialnum;
	}
	
	
	
	/**
	 * 获取当前固件版本号
	 * @return
	 */
	public static String getLocalFirmVersion() {

		try {

			Class cl = Class.forName("android.os.SystemProperties");
			Object invoker = cl.newInstance();
			Method m = cl.getMethod("get", new Class[] { String.class,
					String.class });
			Object result = m.invoke(invoker, new Object[] {
					"ro.custom.build.version", "0" });
			String phoneInfo = (String) result;
			return phoneInfo;
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 手机型号
	 * 
	 * @return
	 */
	public static String getPhoneModel() {

		try {
			Class cl = Class.forName("android.os.SystemProperties");
			Object invoker = cl.newInstance();
			Method m = cl.getMethod("get", new Class[] { String.class,
					String.class });
			Object result = m.invoke(invoker, new Object[] {
					"ro.product.model", "0" });
			String phoneInfo = (String) result;
			return phoneInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
