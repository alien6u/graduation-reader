package com.jayqqaa12.abase.core;

import java.lang.reflect.Method;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.jayqqaa12.abase.core.activity.AAdapter;

@EViewGroup
public class ItemView<T> extends LinearLayout
{
	@Bean
	protected Abus bus;
	
	public void bind(T item)
	{

	}

	public void bind(T item, AAdapter<T> adapter)
	{

	}

	public ItemView(Context context)
	{
		super(context);
	}

	public static ItemView bindView(Context context, Class clazz, View convertView, Object bindObj, AAdapter adapter)
	{

		ItemView view = null;
		try
		{
			if (clazz == null || context == null) throw new NullPointerException(" must set item view class and context");

			if (convertView == null)
			{
				Method m = clazz.getMethod("build", new Class[] { Context.class });
				view = (ItemView) m.invoke(clazz, new Object[] { context });
			}
			else view = (ItemView) convertView;
			view.bind(bindObj);
			view.bind(bindObj, adapter);

		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)

		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return view;

	}

	/***
	 * 此方法可bindview
	 * 
	 * @return
	 */
	public static ItemView bindView(Context context, Class clazz, View convertView, Object bindObj)
	{

		ItemView view = null;
		try
		{
			if (clazz == null || context == null) throw new NullPointerException(" must set item view class and context");

			if (convertView == null)
			{
				Method m = clazz.getMethod("build", new Class[] { Context.class });
				view = (ItemView) m.invoke(clazz, new Object[] { context });
			}
			else view = (ItemView) convertView;
			view.bind(bindObj);

		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)

		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return view;

	}

}
