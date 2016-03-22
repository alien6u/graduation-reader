package com.jayqqaa12.abase.download;

import java.io.File;

import com.jayqqaa12.abase.model.Bean;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

@Table(name="abase_download")
public class DownloadInfo extends Bean
{

	public long id;

	@Transient
	public HttpHandler<File> handler;
	@Transient
	public HttpHandler.State state  ;
	public String downloadUrl;
	public String fileName;
	public String fileSavePath;
	public long progress;
	public long fileLength;
	public boolean autoResume;
	public boolean autoRename;

	public DownloadInfo()
	{
	}

	public DownloadInfo(String downloadUrl, String fileName, String savePath)
	{

		this.downloadUrl = downloadUrl;
		this.fileName = fileName;
		this.fileSavePath = savePath;

		autoResume = true;
		autoRename = false;

	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public HttpHandler<File> getHandler()
	{
		return handler;
	}

	public void setHandler(HttpHandler<File> handler)
	{
		this.handler = handler;
	}

	public HttpHandler.State getState()
	{
		return state;
	}

	public void setState(HttpHandler.State state)
	{
		this.state = state;
	}

	public String getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileSavePath()
	{
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath)
	{
		this.fileSavePath = fileSavePath;
	}

	public long getProgress()
	{
		return progress;
	}

	public void setProgress(long progress)
	{
		this.progress = progress;
	}

	public long getFileLength()
	{
		return fileLength;
	}

	public void setFileLength(long fileLength)
	{
		this.fileLength = fileLength;
	}

	public boolean isAutoResume()
	{
		return autoResume;
	}

	public void setAutoResume(boolean autoResume)
	{
		this.autoResume = autoResume;
	}

	public boolean isAutoRename()
	{
		return autoRename;
	}

	public void setAutoRename(boolean autoRename)
	{
		this.autoRename = autoRename;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof DownloadInfo)) return false;

		DownloadInfo that = (DownloadInfo) o;

		if (id != that.id) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		return (int) (id ^ (id >>> 32));
	}
}
