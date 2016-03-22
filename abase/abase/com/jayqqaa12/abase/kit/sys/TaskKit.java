package com.jayqqaa12.abase.kit.sys;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.text.format.Formatter;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.ManageKit;
import com.jayqqaa12.abase.model.TaskInfo;


/**
 * 当前 手机  进程的 相关 信息的 工具
 * @author  jayqqaa12
 *
 */
public class TaskKit  
{

	/**
	 * 返回 Task Stack 最上面的 activity 的packname
	 * 
	 * @param am
	 * @return
	 */
	public static String getTaskTopActivityPackName(ActivityManager am)
	{
		return am.getRunningTasks(1).get(0).topActivity.getPackageName();
	}

	public static void killProcess(ActivityManager am, String packname)
	{
		am.killBackgroundProcesses(packname);

	}

	public static void killProcess(String packname)
	{
		ManageKit.getActivityManager().killBackgroundProcesses(packname);

	}

	public static List<TaskInfo> getRunningAppProcesses()
	{
		List<TaskInfo> taskinfos = new ArrayList<TaskInfo>();
		PackageManager pm = Abase.getContext().getPackageManager();
		ActivityManager am = ManageKit.getActivityManager();

		for (RunningAppProcessInfo info : am.getRunningAppProcesses())
		{
			TaskInfo taskinfo;
			try
			{
				taskinfo = new TaskInfo();
				int pid = info.pid;
				taskinfo.pid = pid;
				String packname = info.processName;
				taskinfo.packname = packname;
				ApplicationInfo appinfo = pm.getPackageInfo(packname, 0).applicationInfo;
				Drawable icon = appinfo.loadIcon(pm);
				taskinfo.icon = icon;
				if (AppInfoKit.filterApp(appinfo)) taskinfo.system = false;
				else taskinfo.system = true;

				String appname = appinfo.loadLabel(pm).toString();
				taskinfo.appName = appname;
				MemoryInfo[] memoryinfos = am.getProcessMemoryInfo(new int[] { pid });
				int memory = memoryinfos[0].getTotalPrivateDirty();
				taskinfo.memorySize = memory * 1024;
				taskinfo.memory = Formatter.formatFileSize(Abase.getContext(), memory * 1024);
				taskinfos.add(taskinfo);
				taskinfo = null;
			}
			catch (NameNotFoundException e)
			{

			}
		}

		return taskinfos;
	}

	/**
	 * 判断 服务是否 在运行
	 * 
	 * 要是 很多要判断 记得 不要用这办法 应该用 getRunningServices（）然后 慢慢判断
	 * 
	 * @param serviceName
	 * @return
	 */
	public static boolean isRunningService(String serviceName)
	{
		boolean running = false;
		for (RunningServiceInfo service : ManageKit.getActivityManager().getRunningServices(Integer.MAX_VALUE))
		{
			if (service.service.getClassName().equals(serviceName))
			{
				running = true;
			}
		}

		return running;
	}

	/**
	 * 获得 所有正在 运行的 服务
	 * 
	 */
	public static List<RunningServiceInfo> getRunningServices()
	{
		return ManageKit.getActivityManager().getRunningServices(Integer.MAX_VALUE);

	}

	/**
	 * 注册 系统级 应用 才好使
	 * 
	 * @param packname
	 */
	public static void killProcessBySysApp(String packname)
	{
		ActivityManager am = ManageKit.getActivityManager();
		Method method = null;
		try
		{
			method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
			method.invoke(am, packname);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
