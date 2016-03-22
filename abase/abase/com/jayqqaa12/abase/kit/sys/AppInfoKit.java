package com.jayqqaa12.abase.kit.sys;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.ManageKit;
import com.jayqqaa12.abase.kit.common.L;
import com.jayqqaa12.abase.model.AppInfo;

/**
 * apk 的 相关 信息
 * 
 * @author 12
 * 
 */
public class AppInfoKit
{

	/***
	 * 判断 apk 包 是否能解析
	 * 
	 * @param apkPath
	 * @return
	 */
	public static boolean isRightApk(String apkPath)
	{
		
		if(!new File(apkPath).exists())return false;
		
		return ManageKit.getPackManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES) != null;
	}

	/***
	 * 
	 * 从apk 文件中获取 启动的 intent
	 * 
	 * 如果null 说明 有问题
	 * 
	 * @param path
	 * @return
	 */
	public static Intent getIntentFromApk(String path)
	{
		return getIntentFromPackage(getApkInfo(path).packageName);
	}
	
	
	/***
	 * 
	 * 从 包名 中获取 启动的 intent
	 * 
	 * 如果null 说明 有问题
	 * 
	 * @param path
	 * @return
	 */
	public static Intent getIntentFromPackage(String packagename)
	{
		Intent intent = null;
		try
		{
			intent = ManageKit.getPackManager().getLaunchIntentForPackage( packagename);
		} catch (Exception e)
		{
			return null;
		}

		return intent;

	}

	/**
	 * 获得 版本号
	 * 
	 * @return
	 */
	public static String getVersionNo()
	{
		try
		{

			PackageInfo info = Abase.getContext().getPackageManager().getPackageInfo(Abase.getContext().getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
			return "0";
		}

	}

	/**
	 * 通过包名获取应用程序的名称。
	 * 
	 * 加载 程序 做不到 异步！！
	 * 
	 * @param context
	 *            Context对象。
	 * @param packageName
	 *            包名。
	 * @return 返回包名所对应的应用程序的名称。
	 */
	public static String getProgramNameByPackageName(String packageName)
	{
		PackageManager pm = Abase.getContext().getPackageManager();
		String name = null;
		try
		{
			name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 获取有关本程序的packname。
	 * 
	 * @return 有关本程序的信息。
	 */
	public static String getMyApkPackname()
	{
		ApplicationInfo applicationInfo = Abase.getContext().getApplicationInfo();
		return applicationInfo.packageName;

	}

	/**
	 * 
	 * 获得 有桌面 图标的 应用
	 * 
	 * @return
	 */
	public static List<AppInfo> getLauncherApp()
	{
		List<AppInfo> apks = new ArrayList<AppInfo>();

		PackageManager pm = ManageKit.getPackManager();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resovleInfos = pm.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

		for (ResolveInfo info : resovleInfos)
		{
			try
			{
				AppInfo apk = getAppInfo(pm, pm.getPackageInfo(info.activityInfo.packageName, 0));
				apk.appName = info.loadLabel(pm).toString();
				apk.iconDrawable = info.loadIcon(pm);
				apk.packageName = info.activityInfo.packageName;

				apks.add(apk);
			} catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return apks;

	}

	/**
	 * 获取有关本程序的信息。
	 * 
	 * @return 有关本程序的信息。
	 */
	public static AppInfo getMyApkInfo()
	{
		AppInfo apkInfo = new AppInfo();
		Context context = Abase.getContext();

		ApplicationInfo applicationInfo = context.getApplicationInfo();
		apkInfo.packageName = applicationInfo.packageName;

		apkInfo.iconId = applicationInfo.icon;
		Resources mResources = context.getResources();
		apkInfo.iconDrawable = mResources.getDrawable(apkInfo.iconId);
		apkInfo.appName = mResources.getText(applicationInfo.labelRes).toString();
		apkInfo.appSize = new File(applicationInfo.publicSourceDir).length();
		apkInfo.size = Formatter.formatFileSize(Abase.getContext(), apkInfo.appSize);
		apkInfo.date = new Date(new File(applicationInfo.sourceDir).lastModified());

		PackageInfo packageInfo = null;
		try
		{
			packageInfo = context.getPackageManager().getPackageInfo(apkInfo.packageName, 0);
			apkInfo.versionCode = packageInfo.versionCode;
			apkInfo.versionName = packageInfo.versionName;

		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return apkInfo;
	}

	/**
	 * 获取有关本程序的信息。
	 * 
	 * @return 有关本程序的信息。
	 */
	public static String getMyApkName()
	{
		Context context = Abase.getContext();
		ApplicationInfo applicationInfo = context.getApplicationInfo();
		return context.getResources().getText(applicationInfo.labelRes).toString();

	}

	/**
	 * 返回当前手机里面安装的所有的程序信息的集合
	 * 
	 * @return 应用程序的集合
	 */
	public static List<AppInfo> getAllApps()
	{
		List<AppInfo> list = new ArrayList<AppInfo>();
		PackageManager packmanager = ManageKit.getPackManager();

		List<PackageInfo> packinfos = packmanager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

		for (PackageInfo info : packinfos)
		{
			AppInfo apkinfo = getAppInfo(packmanager, info);

			list.add(apkinfo);
		}
		return list;
	}

	/**
	 * 
	 * 返回 文件夹里的apk 信息
	 * 
	 * @param apkpath
	 * @return
	 */
	public static List<AppInfo> getApkInfoList(String dirPath)
	{
		List<AppInfo> list = new ArrayList<AppInfo>();
		File[] files = new File(dirPath).listFiles();

		if (files == null) return new ArrayList<AppInfo>(1);

		for (File f : files)
		{
			if (f.getPath().endsWith(".apk"))
			{
				AppInfo info = AppInfoKit.getApkInfo(f.toString());
				if (info != null) list.add(info);
			}
		}
		return list;
	}

	/**
	 * 根据 路径 返回 apk 信息
	 * 
	 * @param apkpath
	 * @return
	 */
	public static AppInfo getApkInfo(String apkpath)
	{
		AppInfo apk = null;
		PackageManager pm = Abase.getContext().getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkpath, PackageManager.GET_ACTIVITIES);
		if (info != null) apk = getApkinfo(apkpath, pm, info);

		return apk;
	}
	
	
	public static AppInfo getApkinfo(String path, PackageManager pm, PackageInfo info){
		
		AppInfo apk = new AppInfo();
		apk.path = path;

		
	    if(apk != null){     
	    	
	        ApplicationInfo appInfo = info.applicationInfo;     
	        appInfo.sourceDir=path;
	        appInfo.publicSourceDir=path;
	        apk.appName = pm.getApplicationLabel(appInfo).toString(); 
	        apk.packageName = appInfo.packageName;  //得到安装包名称   
	        apk.versionName=info.versionName;       //得到版本信息        
	        apk.versionCode = info.versionCode;
	       apk.iconDrawable=pm.getApplicationIcon(appInfo);//得到图标信息   
	       
	       apk.isInstall = isInstall(pm, apk.packageName, apk.versionCode);

	    }     
	    
	    L.i(apk);
	    
	    
	    return apk;
	}
	

	/**
	 * 读取安装包信息
	 */
//	public static AppInfo getApkinfo(String path, PackageManager pm, PackageInfo info)
//	{
//		AppInfo appinfo = new AppInfo();
//		appinfo.path = path;
//		try
//		{
//			ApplicationInfo applicationInfo = null;// 安装包版本信息管理器
//			String PATH_PackageParser = null;
//			String PATH_AssetManager = null;
//			if (path.endsWith(".apk"))
//			{
//				if (info != null)
//				{
//					CharSequence label = null;// 名字
//
//					applicationInfo = info.applicationInfo;
//					appinfo.packageName = applicationInfo.packageName;
//					appinfo.versionName = info.versionName;
//					appinfo.versionCode = info.versionCode;
//
//					PATH_PackageParser = "android.content.pm.PackageParser";
//					PATH_AssetManager = "android.content.res.AssetManager";
//					try
//					{// 获取安装包的图标和显示的名称
//						Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
//						Class<?>[] typeArgs = new Class[1];
//						typeArgs[0] = String.class;
//						Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
//						Object[] valueArgs = new Object[1];
//						valueArgs[0] = path.toString();
//						Object pkgParser = pkgParserCt.newInstance(valueArgs);
//						DisplayMetrics metrics = new DisplayMetrics();
//						typeArgs = new Class[4];
//						typeArgs[0] = File.class;
//						typeArgs[1] = String.class;
//						typeArgs[2] = DisplayMetrics.class;
//						typeArgs[3] = Integer.TYPE;
//						Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);
//						valueArgs = new Object[4];
//						valueArgs[0] = new File(path.toString());
//						valueArgs[1] = path.toString();
//						valueArgs[2] = metrics;
//						valueArgs[3] = 0;
//						Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
//						// 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
//						Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");
//						ApplicationInfo infor = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
//
//						Class<?> assetMagCls = Class.forName(PATH_AssetManager);
//						Constructor<?> assetMagCt = assetMagCls.getConstructor((Class[]) null);
//						Object assetMag = assetMagCt.newInstance((Object[]) null);
//						typeArgs = new Class[1];
//						typeArgs[0] = String.class;
//						Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);
//						valueArgs = new Object[1];
//						valueArgs[0] = path.toString();
//						assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
//						Resources res = Abase.getContext().getResources();
//						typeArgs = new Class[3];
//						typeArgs[0] = assetMag.getClass();
//						typeArgs[1] = res.getDisplayMetrics().getClass();
//						typeArgs[2] = res.getConfiguration().getClass();
//						Constructor<Resources> resCt = Resources.class.getConstructor(typeArgs);
//						valueArgs = new Object[3];
//						valueArgs[0] = assetMag;
//						valueArgs[1] = res.getDisplayMetrics();
//						valueArgs[2] = res.getConfiguration();
//						res = (Resources) resCt.newInstance(valueArgs);
//						if (infor.labelRes != 0) label = res.getText(infor.labelRes);
//						if (label == null) label = "无此apk标签信息";
//
//						appinfo.appName = label.toString();
//						if (infor.icon != 0) appinfo.iconDrawable = res.getDrawable(infor.icon);
//
//						appinfo.isInstall = isInstall(pm, appinfo.packageName, appinfo.versionCode);
//
//					} catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//
//				}
//
//			}
//
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		return appinfo;
//	}

	/***
	 * 判断是否安装
	 * 
	 * @param pm
	 * @param packageName
	 * @param versionCode
	 * @return
	 */
	public static boolean isInstall(PackageManager pm, String packageName, int versionCode)
	{
		for (PackageInfo pi : pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES))
		{
			if (packageName.endsWith(pi.packageName) && versionCode == pi.versionCode) return true;
		}

		return false;
	}
	
	
	
	/**
	 * 判断是否 安装 不检测 版本
	 * @param packageName
	 * @return
	 */
	public static boolean isInstall( String packageName) {  
	    boolean installed =false;  
	    try {  
	    	
	        ManageKit.getPackManager().getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);  
	        installed =true;  
	    } catch(PackageManager.NameNotFoundException e) {  
	        installed =false;  
	    }  
	    return installed;  
	}  

	private static AppInfo getAppInfo(PackageManager packmanager, PackageInfo packageInfo)
	{
		AppInfo info = new AppInfo();
		info.versionCode = packageInfo.versionCode;
		info.versionName = packageInfo.versionName;
		String packname = packageInfo.packageName;
		info.packageName = packname;
		ApplicationInfo applicationInfo = packageInfo.applicationInfo;
		Drawable icon = applicationInfo.loadIcon(packmanager);
		info.iconId = applicationInfo.icon;
		info.iconDrawable = icon;
		info.uid = applicationInfo.uid;
		if (applicationInfo.publicSourceDir != null)
		{
			info.appSize = new File(applicationInfo.publicSourceDir).length();
			info.size = Formatter.formatFileSize(Abase.getContext(), info.appSize);
		}
		info.appName = applicationInfo.loadLabel(packmanager).toString();

		if (!filterApp(applicationInfo))
		{
			info.isSysApp = true;

		}
		else if (applicationInfo.publicSourceDir != null)
		{
			// 系统应用 不知道时间
			info.date = new Date(new File(applicationInfo.sourceDir).lastModified());
		}

		// TODO 判断 ROOT 进来的 SYSTEM app 目前的办法是 需要 有一个 常用 第三方 应用的 名单 然后 排除掉
		// 但是应该有更好的办法

		return info;
	}

	/**
	 * 返回 我安装的 可以卸载的软件
	 * 
	 * @return 应用程序的集合
	 */
	public static List<AppInfo> getMyApps()
	{
		return getMyApps(getAllApps());
	}

	/**
	 * 如题
	 * 
	 * @return
	 */
	public static List<AppInfo> getNotSysApps()
	{
		return getNotSysApps(getAllApps());
	}

	/**
	 * 如题
	 * 
	 * @return
	 */
	public static List<AppInfo> getNotSysApps(List<AppInfo> apks)
	{

		List<AppInfo> list = new ArrayList<AppInfo>();

		for (AppInfo info : apks)
		{
			if (!info.isSysApp)
			{
				list.add(info);
			}
		}
		return list;

	}

	/**
	 * 返回 我安装的 可以卸载的软件
	 * 
	 * @return 应用程序的集合
	 */
	public static List<AppInfo> getMyApps(List<AppInfo> apps)
	{

		List<AppInfo> list = new ArrayList<AppInfo>();

		for (AppInfo info : apps)
		{
			if (!info.isSysApp && !info.isPreloadApp)
			{
				list.add(info);
			}
		}
		return list;
	}

	/**
	 * 返回 系统 App
	 * 
	 * @return 应用程序的集合
	 */
	public static List<AppInfo> getSysApps()
	{
		return getSysApps(getAllApps());
	}

	/**
	 * 
	 * 返回 pre load App
	 * 
	 * @return
	 */

	public static List<AppInfo> getPreloadApps()
	{

		return getPreloadApps(getAllApps());
	}

	/**
	 * 返回 系统 App
	 * 
	 * @return 应用程序的集合
	 */
	public static List<AppInfo> getSysApps(List<AppInfo> apps)
	{
		List<AppInfo> list = new ArrayList<AppInfo>();

		for (AppInfo info : apps)
		{
			if (info.isSysApp)
			{
				list.add(info);
			}
		}
		return list;
	}

	public static List<AppInfo> getPreloadApps(List<AppInfo> apps)
	{
		List<AppInfo> list = new ArrayList<AppInfo>();

		for (AppInfo info : apps)
		{
			if (info.isPreloadApp)
			{
				list.add(info);
			}
		}
		return list;
	}

	/**
	 * 判断某个应用程序是 不是三方的应用程序
	 * 
	 * @param info
	 * @return
	 */
	public static boolean filterApp(ApplicationInfo info)
	{

		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) return true;
		else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { return true; }
		return false;
	}

}
