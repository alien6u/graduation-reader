package com.jayqqaa12.abase.kit.sys;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.jayqqaa12.abase.kit.MsgKit;
import com.jayqqaa12.abase.kit.common.L;

/**
 *root 工具包
 * 
 * @author jayqqaa12
 * @date 2013-5-17
 */
public class RootKit
{

	/**
	 * 获得 root 权限 的输出流
	 * 
	 * @return 是否成功
	 */
	public static synchronized DataOutputStream getRootOutputStream()
	{
		Process process = null;
		DataOutputStream os = null;
		try
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = process.waitFor();
			if (exitValue == 0) { return os; }

		}
		catch (Exception e)
		{
			Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 启动 任何的activity
	 * 
	 * 包名/包名＋类名
	 * 
	 * @param packname
	 *            包名
	 * @param actvityName
	 *            包名＋类名
	 */
	public static void startIntent(final String packname, final String actvityName)
	{
		new Thread() {

			public void run()
			{
				Process process = null;
				OutputStream out = null;
				try
				{
					process = Runtime.getRuntime().exec("su");
					out = process.getOutputStream();
					// 调用安装
					out.write(("am start -n " + packname + "/" + actvityName + "\n").getBytes());

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						process.destroy();
						out.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			};

		}.start();

	}

	/***
	 * 
	 * 调用 Root 可能会失败
	 * 
	 * @param context
	 * @param path
	 * @param handler
	 * @param msgwhat
	 * @param callback
	 */
	public static void install(final Context context, final String path, final Handler callback)
	{
		new Thread() {
			public void run()
			{
				Process process = null;
				OutputStream out = null;
				InputStream in = null;
				try
				{
					process = Runtime.getRuntime().exec("su");
					out = process.getOutputStream();
					// 调用安装
					out.write(("pm install -r " + path + "\n").getBytes());

					in = process.getInputStream();
					byte[] bs = new byte[256];
					int len = in.read(bs);
					
					L.i("root status =" +len);
					if (-1 == len && callback != null) MsgKit.sendMessage(callback, MsgKit.MSG_FAIL, null);
					else if(callback!=null)  MsgKit.sendMessage(callback, MsgKit.MSG_SUCCESS, null);
				}
				catch (Exception e)
				{
				}
				finally
				{
					try
					{
						if (process != null)	process.destroy();
						if (out != null)	out.close();
						if (in != null) in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

				}
			}

		}.start();
	}

	/***
	 * 
	 * 调用 Root 失败 会使用 intent 方式
	 * 
	 * @param context
	 * @param path
	 * @param msgwhat
	 * @param callback
	 */
	public static void install(final Context context, final List<String> paths, final Object msgobj,
			final Handler callback)
	{
		new Thread() {
			public void run()
			{
				Process process = null;
				OutputStream out = null;
				InputStream in = null;
				try
				{
					for (String path : paths)
					{
						// 请求root
						process = Runtime.getRuntime().exec("su");
						if (callback != null) MsgKit.sendMessage(callback, MsgKit.MSG_START, msgobj);
						out = process.getOutputStream();
						// 调用安装
						out.write(("pm install -r " + path + "\n").getBytes());
						in = process.getInputStream();
						byte[] bs = new byte[256];
						int len = in.read(bs);
						// root fail
						if (-1 == len) SysIntentKit.install(context, path);
					}

					if (callback != null) MsgKit.sendMessage(callback, MsgKit.MSG_FINISH, null);

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if (out != null)
						{
							out.flush();
							out.close();
						}
						if (in != null)
						{
							in.close();
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/***
	 * Root uninstall
	 * 
	 * 调用 Root 失败 会使用 intent 方式
	 * 
	 * 如果不是 root 方式的 可以通过 receiver 来 监听
	 * 
	 * @param context
	 * @param pkg
	 * @param msgwhat
	 * @param callback
	 */
	public static void uninstall(final Context context, final List<String> packageNames, final Object msgobj,
			final Handler callback)
	{
		new Thread() {
			public void run()
			{
				Process process = null;
				OutputStream out = null;
				InputStream in = null;
				try
				{
					for (String pkg : packageNames)
					{// 请求root
						process = Runtime.getRuntime().exec("su");
						if (callback != null) MsgKit.sendMessage(callback, MsgKit.MSG_START, msgobj);
						out = process.getOutputStream();
						// 调用卸载
						out.write(("pm uninstall " + pkg + "\n").getBytes());
						in = process.getInputStream();
						byte[] bs = new byte[256];
						int len = in.read(bs);
						// 取得root失败
						if (-1 == len) SysIntentKit.uninstall(context, pkg);
					}
					if (callback != null) MsgKit.sendMessage(callback, MsgKit.MSG_FINISH, -1);

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						if (out != null)
						{
							out.flush();
							out.close();
						}
						if (in != null)
						{
							in.close();
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}


}
