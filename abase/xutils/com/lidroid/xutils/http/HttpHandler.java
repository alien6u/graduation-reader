/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.xutils.http;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

import com.jayqqaa12.abase.core.ACache;
import com.jayqqaa12.abase.core.AHttp;
import com.jayqqaa12.abase.kit.common.L;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.callback.DefaultHttpRedirectHandler;
import com.lidroid.xutils.http.callback.FileDownloadHandler;
import com.lidroid.xutils.http.callback.HttpRedirectHandler;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.callback.RequestCallBackHandler;
import com.lidroid.xutils.http.callback.StringDownloadHandler;
import com.lidroid.xutils.util.OtherUtils;
import com.lidroid.xutils.util.core.CompatibleAsyncTask;

public class HttpHandler<T> extends CompatibleAsyncTask<Object, Object, Void> implements RequestCallBackHandler
{

	private final AbstractHttpClient client;
	private final HttpContext context;

	private final StringDownloadHandler mStringDownloadHandler = new StringDownloadHandler();
	private final FileDownloadHandler mFileDownloadHandler = new FileDownloadHandler();

	private HttpRedirectHandler httpRedirectHandler;

	public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler)
	{
		if (httpRedirectHandler != null)
		{
			this.httpRedirectHandler = httpRedirectHandler;
		}
	}

	private String requestUrl;
	private String requestMethod;
	private HttpRequestBase request;
	private boolean isUploading = true;
	private RequestCallBack<T> callback;

	private int retriedCount = 0;
	private String fileSavePath = null;
	private boolean isDownloadingFile = false;
	private boolean autoResume = false; // Whether the downloading could
										// continue from the point of
										// interruption.
	private boolean autoRename = false; // Whether rename the file by response
										// header info when the download
										// completely.
	private String charset; // The default charset of response header info.

	private int expiry;
	private long totalLenth;
	private State state = State.WAITING;

	public HttpHandler(AbstractHttpClient client, HttpContext context, String charset, RequestCallBack<T> callback)
	{
		this.client = client;
		this.context = context;
		this.callback = callback;
		this.charset = charset;
		this.client.setRedirectHandler(notUseApacheRedirectHandler);
	}

	public HttpHandler(AbstractHttpClient client, HttpContext context, String charset, long totalLength, RequestCallBack<T> callback)
	{
		this(client, context, charset, callback);
		this.totalLenth = totalLength;
	}

	public State getState()
	{
		return state;
	}

	/***
	 * 设置 要下载文件的长度 特殊服务器的需求
	 * 
	 * @param totalLength
	 */
	public void setTotalLength(long totalLength)
	{

		this.totalLenth = totalLength;
	}

	/***
	 * 设置 缓存时间
	 * 
	 * @param expiry
	 */
	public void setExpiry(int expiry)
	{
		this.expiry = expiry;
	}

	public void setRequestCallBack(RequestCallBack<T> callback)
	{
		this.callback = callback;
	}

	public RequestCallBack<T> getRequestCallBack()
	{
		return this.callback;
	}

	// 执行请求
	@SuppressWarnings("unchecked")
	private ResponseInfo<T> sendRequest(HttpRequestBase request) throws HttpException
	{

		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
		while (true)
		{

			if (autoResume && isDownloadingFile)
			{
				File downloadFile = new File(fileSavePath);
				long fileLen = 0;
				if (downloadFile.isFile() && downloadFile.exists())
				{
					fileLen = downloadFile.length();
				}
				// if (fileLen > 0) {
				// request.setHeader("RANGE", "bytes=" + fileLen + "-");
				// }
				/**
				 * 12 modify 实践中 部分 服务器 只支持 这种 方式的断点续传 要提前知道文件长度
				 */
				if (fileLen > 0)
				{
					if (totalLenth == 0) request.setHeader("RANGE", "bytes=" + fileLen + "-");
					else request.setHeader("RANGE", "bytes=" + fileLen + "-" + (totalLenth - 1));

				}

			}

			boolean retry = true;
			IOException exception = null;
			try
			{
				requestMethod = request.getMethod();
				if (AHttp.sHttpCache.isEnabled(requestMethod))
				{
					String result = AHttp.sHttpCache.get(requestUrl);
					if (result == null ) result = ACache.create().getAsString(requestUrl);
					if (result != null) return new ResponseInfo<T>(null, (T) result, true);
					
				}
				
				ResponseInfo<T> responseInfo = null;
				if (!isCancelled())
				{
					HttpResponse response = client.execute(request, context);
					responseInfo = handleResponse(response);
				}
				return responseInfo;
			} catch (UnknownHostException e)
			{
				exception = e;
				retry = retryHandler.retryRequest(exception, ++retriedCount, context);
			} catch (IOException e)
			{
				exception = e;
				retry = retryHandler.retryRequest(exception, ++retriedCount, context);
			} catch (NullPointerException e)
			{
				exception = new IOException(e.getMessage());
				exception.initCause(e);
				retry = retryHandler.retryRequest(exception, ++retriedCount, context);
			} catch (HttpException e)
			{
				throw e;
			} catch (Throwable e)
			{
				exception = new IOException(e.getMessage());
				exception.initCause(e);
				retry = retryHandler.retryRequest(exception, ++retriedCount, context);
			}
			if (!retry) { throw new HttpException(exception); }
		}
	}

	@Override
	protected Void doInBackground(Object... params)
	{
		if (this.state == State.STOPPED || params == null || params.length == 0) return null;

		if (params.length > 3)
		{
			fileSavePath = String.valueOf(params[1]);
			isDownloadingFile = fileSavePath != null;
			autoResume = (Boolean) params[2];
			autoRename = (Boolean) params[3];
		}

		try
		{
			if (this.state == State.STOPPED) return null;
			// init request & requestUrl
			request = (HttpRequestBase) params[0];
			requestUrl = request.getURI().toString();
			if (callback != null)
			{
				callback.setRequestUrl(requestUrl);
			}

			this.publishProgress(UPDATE_START);

			lastUpdateTime = SystemClock.uptimeMillis();

			ResponseInfo<T> responseInfo = sendRequest(request);
			if (responseInfo != null)
			{
				this.publishProgress(UPDATE_SUCCESS, responseInfo);
				return null;
			}
		} catch (HttpException e)
		{
			this.publishProgress(UPDATE_FAILURE, e, e.getMessage());
		}

		return null;
	}

	private final static int UPDATE_START = 1;
	private final static int UPDATE_LOADING = 2;
	private final static int UPDATE_FAILURE = 3;
	private final static int UPDATE_SUCCESS = 4;

	@Override
	@SuppressWarnings("unchecked")
	protected void onProgressUpdate(Object... values)
	{
		if (this.state == State.STOPPED || values == null || values.length == 0 || callback == null) return;
		switch ((Integer) values[0])
		{
		case UPDATE_START:
			this.state = State.STARTED;
			callback.onStart();
			break;
		case UPDATE_LOADING:
			if (values.length != 3) return;
			this.state = State.LOADING;
			callback.onLoading(Long.valueOf(String.valueOf(values[1])), Long.valueOf(String.valueOf(values[2])), isUploading);
			break;
		case UPDATE_FAILURE:
			if (values.length != 3) return;
			this.state = State.FAILURE;
			callback.onFailure((HttpException) values[1], (String) values[2]);
			break;
		case UPDATE_SUCCESS:
			if (values.length != 2) return;
			this.state = State.SUCCESS;
			callback.onSuccess((ResponseInfo<T>) values[1]);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private ResponseInfo<T> handleResponse(HttpResponse response) throws HttpException, IOException
	{
		if (response == null) { throw new HttpException("response is null"); }
		if (isCancelled()) return null;

		StatusLine status = response.getStatusLine();
		int statusCode = status.getStatusCode();
		if (statusCode < 300)
		{
			Object result = null;
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				isUploading = false;
				if (isDownloadingFile)
				{
					autoResume = autoResume && OtherUtils.isSupportRange(response);
					String responseFileName = autoRename ? OtherUtils.getFileNameFromHttpResponse(response) : null;
					result = mFileDownloadHandler.handleEntity(entity, this, fileSavePath, autoResume, responseFileName);
				}
				else
				{
					result = mStringDownloadHandler.handleEntity(entity, this, charset);
					if (AHttp.sHttpCache.isEnabled(requestMethod)&&expiry!=ACache.TIME_NONE)
					{
						AHttp.sHttpCache.put(requestUrl, (String) result, HttpCache.getDefaultExpiryTime());
						if (expiry > 60) ACache.create().put(requestUrl, (String) result, expiry);
					}
				}
			}
			return new ResponseInfo<T>(response, (T) result, false);
		}
		else if (statusCode == 301 || statusCode == 302)
		{
			if (httpRedirectHandler == null)
			{
				httpRedirectHandler = new DefaultHttpRedirectHandler();
			}
			HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
			if (request != null) { return this.sendRequest(request); }
		}
		else if (statusCode == 416)
		{
			throw new HttpException(statusCode, "maybe the file has downloaded completely");
		}
		else
		{
			throw new HttpException(statusCode, status.getReasonPhrase());
		}
		return null;
	}

	/**
	 * stop request task.
	 */
	public void stop()
	{
		this.state = State.STOPPED;

		if (request != null && !request.isAborted())
		{
			try
			{
				request.abort();
			} catch (Throwable e)
			{}
		}
		if (!this.isCancelled())
		{
			try
			{
				this.cancel(true);
			} catch (Throwable e)
			{}
		}

		if (callback != null)
		{
			callback.onStopped();
		}
	}

	public boolean isStopped()
	{
		return this.state == State.STOPPED;
	}

	private long lastUpdateTime;

	@Override
	public boolean updateProgress(long total, long current, boolean forceUpdateUI)
	{
		if (callback != null && this.state != State.STOPPED)
		{
			if (forceUpdateUI)
			{
				this.publishProgress(UPDATE_LOADING, total, current);
			}
			else
			{
				long currTime = SystemClock.uptimeMillis();
				if (currTime - lastUpdateTime >= callback.getRate())
				{
					lastUpdateTime = currTime;
					this.publishProgress(UPDATE_LOADING, total, current);
				}
			}
		}
		return this.state != State.STOPPED;
	}

	public enum State
	{
		WAITING(0), STARTED(1), LOADING(2), FAILURE(3), STOPPED(4), SUCCESS(5);
		private int value = 0;

		State(int value)
		{
			this.value = value;
		}

		public static State valueOf(int value)
		{
			switch (value)
			{
			case 0:
				return WAITING;
			case 1:
				return STARTED;
			case 2:
				return LOADING;
			case 3:
				return FAILURE;
			case 4:
				return STOPPED;
			case 5:
				return SUCCESS;
			default:
				return FAILURE;
			}
		}

		public int value()
		{
			return this.value;
		}
	}

	private static final NotUseApacheRedirectHandler notUseApacheRedirectHandler = new NotUseApacheRedirectHandler();

	private static final class NotUseApacheRedirectHandler implements RedirectHandler
	{
		@Override
		public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext)
		{
			return false;
		}

		@Override
		public URI getLocationURI(HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException
		{
			return null;
		}
	}
}
