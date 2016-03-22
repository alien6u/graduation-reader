package com.jayqqaa12.abase.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;

/**
 * 如果 不想 每次都 传递 Context 请继承这个类 然后在 清单文件中配置 如果 直接 通过 getContext 获得 application
 * Context 对象 当然 application Context 不能 成为 生命周期 短的 类的 属性 否则 会使那个类 得不到 销毁。。
 * 
 * 如果不想 继承 AbaseApp 又想 使用 Abase （工具类也一样） 记得 在你 当前的 application 类 里面 设置
 * Abase.setContext(context) 并且 传递 一个 context 给 他，
 * 
 * 如果 发现 getContext NullPonintException @see setContext()
 * 
 * @author jayqqaa12
 * @date 2013-5-14
 */
public class AApp extends Application
{
	private static Context applicationContext;

	/**
	 * 存放 一些 临时 要传递 的 数据
	 */
	private static Map<String, Object> tempMap = new HashMap<String, Object>();

	@Override
	public void onCreate()
	{
		super.onCreate();
		applicationContext = getApplicationContext();
		Abase.setContext(applicationContext);
	}

	/**
	 * 
	 * 这个 方法的 意义 在于 Test 时候的 context 会先于 application 创建 前 创建 所以 在此之前 调用一些 方法
	 * 可能会出错
	 * 
	 * @param context
	 * @return
	 */
	public static void setContext(Context context)
	{
		applicationContext = context;
	}

	public static Context getContext()
	{
		return applicationContext;
	}

	/**
	 * 这个方法 用来存放  intent  不能 传递的 对象 （因为 要传递 必需 序列化 有的 序列化不了可以用这个 办法）
	 * @param key
	 * @param obj
	 */
	public static void setObject(String key, Object obj)
	{
		tempMap.put(key, obj);
	}
	
	public static <T> T getObject(String key)
	{
		return (T) tempMap.get(key);
	}
	
	public static void removeObject(String key)
	{
		tempMap.remove(key);
	}
	
	/**
	 * 用完以后 清理 一下  temp 
	 */
	public static void clearTemp()
	{
		tempMap.clear();
	}
	/**
	 * 设置  异常的 处理器 应该在 app 的onCreate 里面设置 
	 * @param handler
	 */
	public static void setUncaughtExceptionHandler(UncaughtExceptionHandler handler)
	{
		Thread.currentThread().setUncaughtExceptionHandler(handler);
	}

}
