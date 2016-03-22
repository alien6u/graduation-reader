package com.jayqqaa12.abase.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.jayqqaa12.abase.core.ADao;
import com.jayqqaa12.abase.core.AHttp;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class DownloadManager<T extends DownloadInfo>
{
	private List<T> downloadInfoList;
	private int maxDownloadThread = 3;

	private ADao db;

	public DownloadManager(Class<?> clazz)
	{
		ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class, new HttpHandlerStateConverter());
		db = new ADao();
		downloadInfoList = db.findAll(Selector.from(clazz));
		if (downloadInfoList == null) downloadInfoList = new ArrayList<T>();
	}

	public List<T> getDownloadList()
	{
		for (T info : downloadInfoList)
		{
			if (info.state == null)
			{
				if (info.fileLength > 0)
				{
					if (info.progress < info.fileLength) info.state = HttpHandler.State.STOPPED;
					else info.state = HttpHandler.State.SUCCESS;
				}
				else info.state = HttpHandler.State.STOPPED;
			}
		}
		return downloadInfoList;
	}

	public int getDownloadInfoListCount()
	{
		return downloadInfoList.size();
	}

	public T getDownloadInfo(int index)
	{
		return downloadInfoList.get(index);
	}

	public void addNewDownload(T info, final RequestCallBack<File> callback)
	{
		AHttp http = new AHttp();
		http.configRequestThreadPoolSize(maxDownloadThread);
		
		HttpHandler<File> handler = http.download(info.downloadUrl, info.fileSavePath, info.autoResume, info.autoRename,info.fileLength,
				new ManagerCallBack(info, callback));
		
		info.setHandler(handler);
		info.setState(handler.getState());
		downloadInfoList.add(info);
		db.saveBindingId(info);
	}

	public void resumeDownload(int index, final RequestCallBack<File> callback)
	{
		final T downloadInfo = downloadInfoList.get(index);
		resumeDownload(downloadInfo, callback);
	}

	public void resumeDownload(T downloadInfo, final RequestCallBack<File> callback)
	{
		AHttp http = new AHttp();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(downloadInfo.downloadUrl, downloadInfo.fileSavePath, downloadInfo.autoResume,
				downloadInfo.autoRename,downloadInfo.fileLength, new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		db.saveOrUpdate(downloadInfo);
	}

	public void removeDownload(int index)
	{
		removeDownload(downloadInfoList.get(index));
	}

	public void removeDownload(T downloadInfo)
	{
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isStopped()) handler.stop();
		downloadInfoList.remove(downloadInfo);
		db.delete(downloadInfo);
	}

	public void stopDownload(int index)
	{
		stopDownload(downloadInfoList.get(index));
	}

	public void stopDownload(T downloadInfo)
	{
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isStopped()) handler.stop();
		else downloadInfo.setState(HttpHandler.State.STOPPED);
		db.saveOrUpdate(downloadInfo);
	}

	public void stopAllDownload()
	{
		for (T downloadInfo : downloadInfoList)
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null && !handler.isStopped()) handler.stop();
			else downloadInfo.setState(HttpHandler.State.STOPPED);
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public void backupDownloadInfoList()
	{
		for (T downloadInfo : downloadInfoList)
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) downloadInfo.setState(handler.getState());
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public int getMaxDownloadThread()
	{
		return maxDownloadThread;
	}

	public void setMaxDownloadThread(int maxDownloadThread)
	{
		this.maxDownloadThread = maxDownloadThread;
	}

	public class ManagerCallBack extends RequestCallBack<File>
	{
		private DownloadInfo downloadInfo;
		private RequestCallBack<File> baseCallBack;

		public RequestCallBack<File> getBaseCallBack()
		{
			return baseCallBack;
		}

		public void setBaseCallBack(RequestCallBack<File> baseCallBack)
		{
			this.baseCallBack = baseCallBack;
		}

		private ManagerCallBack(DownloadInfo downloadInfo, RequestCallBack<File> baseCallBack)
		{
			this.baseCallBack = baseCallBack;
			this.downloadInfo = downloadInfo;
		}

		@Override
		public Object getUserTag()
		{
			if (baseCallBack == null) return null;
			return baseCallBack.getUserTag();
		}

		@Override
		public void setUserTag(Object userTag)
		{
			if (baseCallBack == null) return;
			baseCallBack.setUserTag(userTag);
		}

		@Override
		public void onStart()
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) downloadInfo.setState(handler.getState());
			db.saveOrUpdate(downloadInfo);
			if (baseCallBack != null) baseCallBack.onStart();
		}

		@Override
		public void onStopped()
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) downloadInfo.setState(handler.getState());
			db.saveOrUpdate(downloadInfo);
			if (baseCallBack != null) baseCallBack.onStopped();
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading)
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) downloadInfo.setState(handler.getState());
			downloadInfo.setFileLength(total);
			downloadInfo.setProgress(current);
			db.saveOrUpdate(downloadInfo);

			if (baseCallBack != null) baseCallBack.onLoading(total, current, isUploading);
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo)
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) downloadInfo.setState(handler.getState());
			db.saveOrUpdate(downloadInfo);
			if (baseCallBack != null) baseCallBack.onSuccess(responseInfo);
		}

		@Override
		public void onFailure(HttpException error, String msg)
		{
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) downloadInfo.setState(handler.getState());

			db.saveOrUpdate(downloadInfo);
			if (baseCallBack != null) baseCallBack.onFailure(error, msg);
		}
	}

	private class HttpHandlerStateConverter implements ColumnConverter<HttpHandler.State>
	{

		@Override
		public HttpHandler.State getFieldValue(Cursor cursor, int index)
		{
			return HttpHandler.State.valueOf(cursor.getInt(index));
		}

		@Override
		public HttpHandler.State getFieldValue(String fieldStringValue)
		{
			if (fieldStringValue == null) return null;
			return HttpHandler.State.valueOf(fieldStringValue);
		}

		@Override
		public Object fieldValue2ColumnValue(HttpHandler.State fieldValue)
		{
			return fieldValue.value();
		}

		@Override
		public ColumnDbType getColumnDbType()
		{
			return ColumnDbType.INTEGER;
		}
	}
}
