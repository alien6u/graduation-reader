package com.jayqqaa12.abase.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jayqqaa12.abase.exception.AException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

/**
 * 有关 handler msg 的 工具
 * 
 * @author jayqqaa12
 * @date 2013-6-8
 */
public class MsgKit
{
	public static final int MSG_ADD = 1;
	public static final int MSG_REMOVE = 2;
	public static final int MSG_SAVE = 3;
	public static final int MSG_DELETE = 4;
	public static final int MSG_UPDATE = 5;
	public static final int MSG_SERACH = 6;
	public static final int MSG_FLASH = 7;
	public static final int MSG_CREATE = 8;
	public static final int MSG_FAIL = 9;
	public static final int MSG_SUCCESS = 10;
	public static final int MSG_FINISH = 12;
	public static final int MSG_STOP = 13;
	public static final int MSG_PAUSE = 14;
	public static final int MSG_DESTORY = 15;
	public static final int MSG_UP = 16;
	public static final int MSG_DOWN = 17;
	public static final int MSG_RIGHT = 18;
	public static final int MSG_LEFT = 19;
	public static final int MSG_START = 20;
	public static final int MSG_FIND = 21;
	public static final int MSG_LOAD = 22;
	public static final int MSG_REFRESH = 23;
	public static final int MSG_INIT = 24;
	public static final int MSG_ADD_REFRESH = 25;
	public static final int MSG_RETRY=26;
	public static final int MSG_ALERY_INIT=27;
	public static final int MSG_RESUME=28;

	public static Bundle getBundle(String[] keys, Object[] values)
	{

		Bundle bundle = new Bundle();

		int i = 0;
		for (Object v : values)
		{
			putBundle(bundle, keys[i++], v);
		}

		return bundle;
	}

	private static void putBundle(Bundle bundle, String key, Object v)
	{
		if (v instanceof String)
		{
			bundle.putString(key, (String) v);
		}
		else if (v instanceof Boolean)
		{
			bundle.putBoolean(key, (Boolean) v);

		}
		else if (v instanceof Integer)
		{

			bundle.putInt(key, (Integer) v);
		}
		else if (v instanceof Float)
		{
			bundle.putFloat(key, (Float) v);
		}
		else if (v instanceof Serializable)
		{
			bundle.putSerializable(key, (Serializable) v);
		}
		else
		{
			throw new AException("object must implements Serializable");
		}
	}

	public static List<Bundle> getBundle(String key, Object[] values)
	{

		List<Bundle> list = new ArrayList<Bundle>();

		for (Object v : values)
		{
			Bundle bundle = new Bundle();
			putBundle(bundle, key, v);
			list.add(bundle);
		}

		return list;
	}

	public static List<Bundle> getBundle(String[] key, Object[]... values)
	{

		List<Bundle> list = new ArrayList<Bundle>();

		int i=0;
		for (String k : key)
		{
			for (Object v : values[i++])
			{
				Bundle bundle = new Bundle();
				putBundle(bundle, k, v);
				list.add(bundle);
			}
		}

		return list;
	}

	public static void sendMessage(Callback callback, int what, Object obj)
	{

		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		callback.handleMessage(msg);

	}

	public static void sendMessage(Handler callback, int what)
	{
		Message msg = new Message();
		msg.what = what;
		msg.setTarget(callback);
		callback.sendMessage(msg);

	}

	public static void sendMessage(Handler callback, int what, int arg1)
	{
		Message msg = new Message();
		msg.what = what;
		msg.arg1 = arg1;
		msg.setTarget(callback);
		callback.sendMessage(msg);

	}

	public static void sendMessage(Handler callback, int what, Object obj)
	{

		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		msg.setTarget(callback);
		callback.sendMessage(msg);

	}

}
