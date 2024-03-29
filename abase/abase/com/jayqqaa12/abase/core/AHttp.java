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

package com.jayqqaa12.abase.core;

import android.text.TextUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.*;
import com.lidroid.xutils.http.callback.HttpRedirectHandler;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.RetryHandler;
import com.lidroid.xutils.http.client.entity.GZipDecompressingEntity;
import com.lidroid.xutils.http.client.DefaultSSLSocketFactory;

import org.androidannotations.annotations.EBean;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/***
 * 修改内存
 * 添加 acache 硬盘缓存
 * 增加一个 excetor 防止下载时候 线程满了
 * 
 * @author 12
 *
 */
@EBean
public class AHttp
{
	/***
	 * 内存 缓存
	 */
	public final static HttpCache sHttpCache = new HttpCache();
	private final DefaultHttpClient httpClient;
	private final HttpContext httpContext = new BasicHttpContext();

	private HttpRedirectHandler httpRedirectHandler;

	public AHttp()
	{
		HttpParams params = new BasicHttpParams();

		ConnManagerParams.setTimeout(params, AHttp.DEFAULT_CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, AHttp.DEFAULT_CONN_TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(params, AHttp.DEFAULT_CONN_TIMEOUT);

		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));
		ConnManagerParams.setMaxTotalConnections(params, 10);

		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory.getSocketFactory(), 443));

		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);

		httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_RETRY_TIMES));

		httpClient.addRequestInterceptor(new HttpRequestInterceptor()
		{
			@Override
			public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException,
					IOException
			{
				if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING))
				{
					httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
			}
		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor()
		{
			@Override
			public void process(HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException
			{
				final HttpEntity entity = response.getEntity();
				if (entity == null) { return; }
				final Header encoding = entity.getContentEncoding();
				if (encoding != null)
				{
					for (HeaderElement element : encoding.getElements())
					{
						if (element.getName().equalsIgnoreCase("gzip"))
						{
							response.setEntity(new GZipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}
		});
	}

	// ************************************ default settings & fields
	// ****************************

	private String responseTextCharset = HTTP.UTF_8;

	/***
	 * Acache cache time
	 */
	private int acacheExpiry ;

	private final static int DEFAULT_CONN_TIMEOUT = 1000 * 5; // 5s

	private final static int DEFAULT_RETRY_TIMES = 5;

	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	private static final ThreadFactory sThreadFactory = new ThreadFactory()
	{
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r)
		{
			Thread thread = new Thread(r, "HttpKit #" + mCount.getAndIncrement());
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return thread;
		}
	};

	private static int threadPoolSize = 3;

	private static Executor downloadExecutor = Executors.newFixedThreadPool(threadPoolSize, sThreadFactory);

	/***
	 * 添加一个 excetor 解决 下载的时候 其他线程打不开的问题
	 */
	private static Executor sendExecutor = Executors.newFixedThreadPool(threadPoolSize, sThreadFactory);

	public HttpClient getHttpClient()
	{
		return this.httpClient;
	}

	// ***************************************** config
	// *******************************************

	public AHttp configResponseTextCharset(String charSet)
	{
		if (!TextUtils.isEmpty(charSet))
		{
			this.responseTextCharset = charSet;
		}
		return this;
	}

	public AHttp configHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler)
	{
		this.httpRedirectHandler = httpRedirectHandler;
		return this;
	}

	public AHttp configHttpCacheSize(int httpCacheSize)
	{
		sHttpCache.setCacheSize(httpCacheSize);
		return this;
	}


	public AHttp configCookieStore(CookieStore cookieStore)
	{
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		return this;
	}

	public AHttp configUserAgent(String userAgent)
	{
		HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
		return this;
	}

	public AHttp configTimeout(int timeout)
	{
		final HttpParams httpParams = this.httpClient.getParams();
		ConnManagerParams.setTimeout(httpParams, timeout);
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		return this;
	}

	public AHttp configRegisterScheme(Scheme scheme)
	{
		this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
		return this;
	}

	public AHttp configSSLSocketFactory(SSLSocketFactory sslSocketFactory)
	{
		Scheme scheme = new Scheme("https", sslSocketFactory, 443);
		this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
		return this;
	}

	public AHttp configRequestRetryCount(int count)
	{
		this.httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
		return this;
	}

	public AHttp configRequestThreadPoolSize(int threadPoolSize)
	{
		if (threadPoolSize > 0 && threadPoolSize != AHttp.threadPoolSize)
		{
			AHttp.threadPoolSize = threadPoolSize;
			AHttp.sendExecutor = Executors.newFixedThreadPool(threadPoolSize, sThreadFactory);
		}
		return this;
	}

	// **** 12 add

	public <T> HttpHandler<T> get(String url, RequestCallBack<T> callBack)
	{
		return send(HttpRequest.HttpMethod.GET, url, null, callBack);
	}

	/**
	 * ACache.TIME_NONE 的时候 关闭缓存
	 * @param url
	 * @param cacheTime
	 * @param callBack
	 * @return
	 */
	public <T> HttpHandler<T> get(String url, int cacheTime, RequestCallBack<T> callBack)
	{
		if(cacheTime==ACache.TIME_NONE)ACache.create().remove(url);
		this.acacheExpiry = cacheTime;
		return send(HttpRequest.HttpMethod.GET, url, null, callBack);
	}

	/***
	 * 刷新缓存 重新发送请求并缓存
	 * @param url
	 * @param cacheTime
	 * @param reflsh
	 * @param callBack
	 * @return
	 */
	public <T> HttpHandler<T> getRefresh(String url, int cacheTime , RequestCallBack<T> callBack)
	{
	     ACache.create().remove(url);
		this.acacheExpiry = cacheTime;
		return send(HttpRequest.HttpMethod.GET, url, null, callBack);
	}
	public <T> HttpHandler<T> post(String url, RequestParams params, RequestCallBack<T> callBack)
	{
		return send(HttpRequest.HttpMethod.POST, url, params, callBack);
	}

	// ***************************************** send request
	// *******************************************

	public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestCallBack<T> callBack)
	{
		return send(method, url, null, callBack);
	}

	public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestParams params, RequestCallBack<T> callBack)
	{
		if (url == null) throw new IllegalArgumentException("url may not be null");

		HttpRequest request = new HttpRequest(method, url);
		return sendRequest(request, params, callBack);
	}

	public ResponseStream sendSync(HttpRequest.HttpMethod method, String url) throws HttpException
	{
		return sendSync(method, url, null);
	}

	public ResponseStream sendSync(HttpRequest.HttpMethod method, String url, RequestParams params) throws HttpException
	{
		if (url == null) throw new IllegalArgumentException("url may not be null");

		HttpRequest request = new HttpRequest(method, url);
		return sendSyncRequest(request, params);
	}

	// ***************************************** download
	// *******************************************

	public HttpHandler<File> download(String url, String target, RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, null, false, false, 0, callback);
	}

	public HttpHandler<File> download(String url, String target, boolean autoResume, RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, false, 0, callback);
	}

	public HttpHandler<File> download(String url, String target, boolean autoResume, boolean autoRename, long total,
			RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, autoRename, total, callback);
	}

	public HttpHandler<File> download(String url, String target, boolean autoResume, boolean autoRename, RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, autoRename, 0, callback);
	}

	public HttpHandler<File> download(String url, String target, RequestParams params, RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, params, false, false, 0, callback);
	}

	public HttpHandler<File> download(String url, String target, RequestParams params, boolean autoResume, RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, params, autoResume, false, 0, callback);
	}

	public HttpHandler<File> download(String url, String target, RequestParams params, boolean autoResume, boolean autoRename,
			RequestCallBack<File> callback)
	{
		return download(HttpRequest.HttpMethod.GET, url, target, params, autoResume, autoRename, 0, callback);
	}

	public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target, RequestParams params,
			RequestCallBack<File> callback)
	{
		return download(method, url, target, params, false, false, 0, callback);
	}

	public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target, RequestParams params, boolean autoResume,
			RequestCallBack<File> callback)
	{
		return download(method, url, target, params, autoResume, false, 0, callback);
	}

	public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target, RequestParams params, boolean autoResume,
			boolean autoRename, long totalLength, RequestCallBack<File> callback)
	{

		if (url == null) throw new IllegalArgumentException("url may not be null");
		if (target == null) throw new IllegalArgumentException("target may not be null");

		HttpRequest request = new HttpRequest(method, url);

		HttpHandler<File> handler = new HttpHandler<File>(httpClient, httpContext, responseTextCharset, totalLength, callback);

		handler.setExpiry(acacheExpiry);
		handler.setHttpRedirectHandler(httpRedirectHandler);
		request.setRequestParams(params, handler);

		handler.executeOnExecutor(downloadExecutor, request, target, autoResume, autoRename);
		return handler;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	private <T> HttpHandler<T> sendRequest(HttpRequest request, RequestParams params, RequestCallBack<T> callBack)
	{

		HttpHandler<T> handler = new HttpHandler<T>(httpClient, httpContext, responseTextCharset, callBack);

		handler.setExpiry(acacheExpiry);
		handler.setHttpRedirectHandler(httpRedirectHandler);
		request.setRequestParams(params, handler);

		  handler.executeOnExecutor(sendExecutor, request);
		return handler;
	}

	private ResponseStream sendSyncRequest(HttpRequest request, RequestParams params) throws HttpException
	{

		SyncHttpHandler handler = new SyncHttpHandler(httpClient, httpContext, responseTextCharset);

		handler.setExpiry(acacheExpiry);
		handler.setHttpRedirectHandler(httpRedirectHandler);
		request.setRequestParams(params);

		return handler.sendRequest(request);
	}
}
