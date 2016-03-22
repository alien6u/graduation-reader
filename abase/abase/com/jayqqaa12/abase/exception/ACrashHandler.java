package com.jayqqaa12.abase.exception;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import android.os.Environment;

import com.jayqqaa12.abase.kit.common.L;
import com.jayqqaa12.abase.kit.sys.AppInfoKit;
import com.jayqqaa12.abase.kit.sys.SysKit;



/**
 *  继承 这个类 重写  doHandler 来进行 异常 信息的 处理
* @author jayqqaa12 
* @date 2013-6-8
 */
public abstract class ACrashHandler implements UncaughtExceptionHandler
{
	/**
	 * 程序发生异常的时候的方法
	 */
	public void uncaughtException(Thread thread, Throwable ex)
	{
		
		
		StringBuilder sb = new StringBuilder();
		try
		{
			ex.printStackTrace();
			
			sb.append("程序的版本号为" + AppInfoKit.getVersionNo());
			sb.append("\n");
			sb.append(SysKit.getSysInfo());
			sb.append(L.getExceptionStackTrace(ex));
			doHandler(sb.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	/**
	 * 重写 这个类  处理 异常 信息 
	 * 比如  发送给 服务器 
	 * @param msg  错误信息
	 */
	public abstract void doHandler(String exceptionMsg);
	
	
	
	
	

	/**
	 * 保存异常日志 到 sd 卡 
	 * @param excp
	 */
	public void saveErrorLog(Exception excp) {
		String errorlog = "errorlog.txt";
		String savePath = "";
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			//判断是否挂载了SD卡
			String storageState = Environment.getExternalStorageState();		
			if(storageState.equals(Environment.MEDIA_MOUNTED)){
				savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/abase/Log/";
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdirs();
				}
				logFilePath = savePath + errorlog;
			}
			//没有挂载SD卡，无法写文件
			if(logFilePath == ""){
				return;
			}
			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile,true);
			pw = new PrintWriter(fw);
			pw.println("--------------------"+(new Date().toLocaleString())+"---------------------");	
			excp.printStackTrace(pw);
			pw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();		
		}finally{ 
			if(pw != null){ pw.close(); } 
			if(fw != null){ try { fw.close(); } catch (IOException e) { }}
		}

	}


}
