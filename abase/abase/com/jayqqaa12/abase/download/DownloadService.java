package com.jayqqaa12.abase.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.sys.TaskKit;

/**
 * 
 * 
 * <service android:name="com.lidroid.xutils.download.DownloadService">
 * <intent-filter > <action android:name="download.service.action"/>
 * </intent-filter> </service>
 * 
 * @author 12
 * 
 */
public class DownloadService extends Service
{
	private static DownloadManager<? extends DownloadInfo> DOWNLOAD_MANAGER;

	
	public static void setDownloadManager(DownloadManager<? extends DownloadInfo> manager)
	{
		if (!TaskKit.isRunningService(DownloadService.class.getName()))
		{
			Intent downloadSvr = new Intent("download.service.action");
			Abase.getContext().startService(downloadSvr);
		}
		DOWNLOAD_MANAGER = manager;
	}
	
	public static  DownloadManager<? extends DownloadInfo>  getDownloadManager(){
		
		if (!TaskKit.isRunningService(DownloadService.class.getName()))
		{
			Intent downloadSvr = new Intent("download.service.action");
			Abase.getContext().startService(downloadSvr);
		}
		return DOWNLOAD_MANAGER;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		if (DOWNLOAD_MANAGER != null)
		{
			DOWNLOAD_MANAGER.stopAllDownload();
			DOWNLOAD_MANAGER.backupDownloadInfoList();
		}
		super.onDestroy();
	}

}
