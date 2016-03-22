package com.jayqqaa12.abase.kit.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.jayqqaa12.abase.core.ACache;
import com.jayqqaa12.abase.core.Abase;
import com.jayqqaa12.abase.kit.ManageKit;
import com.jayqqaa12.abase.kit.common.L;

/**
 * 这里的方法都 没有线程
 * 记得不能在主线程中使用 可以用 AysnTask
 * 
 * downloadManger 部分手机 修改了系统 用不了
 * 
 * @author 12
 *
 */
public class DownLoadKit {

	/**
	 * 
	 * 用 downloadmanger 下载 要 api 11 以上
	 * 
	 * @param url
	 * @param dirName
	 * @param fileName
	 * @param notifyTile
	 */
	@SuppressLint("NewApi")
	public static long download(String url, String dirName, String fileName, String notifyTile) {

		String file = Environment.getExternalStoragePublicDirectory("recommend").getPath();
		if (!new File(file).exists()) new File(file).mkdirs();

		DownloadManager downloadManager = ManageKit.getDownloadManger();
		
		DownloadManager.Request request = new Request(Uri.parse(url));
		request.setVisibleInDownloadsUi(true);
		request.allowScanningByMediaScanner();
		request.setDestinationInExternalPublicDir(dirName, fileName);
		request.setTitle(notifyTile);
		request.setMimeType("application/vnd.android.package-archive");
		return downloadManager.enqueue(request);
	}

	/**
	 * 返回下载 的状态
	 * 
	 * @param id
	 * @return
	 */

	
	
	@SuppressLint("NewApi")
	public static int downloadStatus(String url) {
		int status = -1;
		DownloadManager downloadManager = ManageKit.getDownloadManger();

		Cursor cursor = downloadManager.query(new Query());
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
				if (uri.equals(Uri.parse(url).toString())) {
					status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
					break;
				}
			}
			cursor.close();
		}
		return status;

	}

	/**
	 * 获得要下载文件的 大小 断点续传的 时候用
	 * 
	 * 
	 * @param url
	 * @return
	 */
	public static long getFileLength(String url) {
		long length = -1;
		try {
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setReadTimeout(3000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			length = connection.getContentLength();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * 获得 网络 图片 非线程 需要 放在线程中使用
	 * 
	 * 带有acache缓存 不用考虑其他缓存
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmap(String url) {
		ACache cache = ACache.create();
		Bitmap bitmap = cache.getAsBitmap(url);

		if (bitmap == null) {
			try {
				URL u = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				InputStream is = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				cache.put(url, bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	/**
	 * 获得 网络 图片 非线程 需要 放在线程中使用 带有acache缓存 不用考虑其他缓存
	 * 
	 * @param timeDay
	 *            缓存过期时间
	 * @param
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Drawable getDrawable(String url, int timeDay) {
		ACache cache = ACache.create();
		Drawable dd = cache.getAsDrawable(url);

		if (dd == null) {
			try {
				URL u = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				InputStream is = conn.getInputStream();
				dd = Drawable.createFromStream(is, "src");
				if (dd != null) cache.put(url, dd, timeDay);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dd;
	}

	/**
	 * 获得 网络 图片 非线程 需要 放在线程中使用 带有acache缓存 不用考虑其他缓存
	 * 
	 * @param
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Drawable getDrawable(String url) {
		ACache cache = ACache.create();
		Drawable dd = cache.getAsDrawable(url);

		if (dd == null) {
			try {
				URL u = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				InputStream is = conn.getInputStream();
				dd = Drawable.createFromStream(is, "src");
				cache.put(url, dd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dd;
	}

	/**
	 * 下载指定的文本内容 超时为 3秒
	 * 
	 * @param url
	 *            请求下载的地址。
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String downloadString(String url) throws ParseException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 2555);
		HttpConnectionParams.setSoTimeout(httpParameters, 3000);
		request.setParams(httpParameters);
		HttpResponse response = httpClient.execute(request);
		return EntityUtils.toString(response.getEntity());
	}

}
