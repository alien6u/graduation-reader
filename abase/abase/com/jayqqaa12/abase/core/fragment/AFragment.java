package com.jayqqaa12.abase.core.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayqqaa12.abase.core.Abus;
import com.jayqqaa12.abase.kit.common.ReflectKit;

@EFragment
public class AFragment extends Fragment
{
	@Bean
	protected Abus bus;

	/***
	 * 查看这个值可 防止fragment 重复加载
	 */
	protected boolean aleryInit;

	
	
	public static Fragment newInstance(Class<? extends Fragment> clazz){
		
		clazz =  ReflectKit.getSubClass(clazz);
		Fragment f =null;
		try
		{
			 f = clazz.newInstance();
		} catch (java.lang.InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		return  f;
	}
	
	public static List<Fragment> newInstances(Class<? extends Fragment> ... clazz ){
		List<Fragment> list = new ArrayList<Fragment>();
		for(Class c :clazz){
			Fragment f =newInstance(c);
		list.add(f);
		}
		return  list;
	}
	
	public static List<Fragment> newInstances(List<Bundle> bundle,Class<? extends Fragment> ... clazz ){
		List<Fragment> list = newInstances(clazz);
	   
		int i=0;
		for(Fragment f :list){
		 f.setArguments(bundle.get(i++));
		}
		return  list;
	}
	
	public static Fragment newInstance(Class<? extends Fragment> clazz,Bundle bundle){
		Fragment f =newInstance(clazz);
		f.setArguments(bundle);
		return  f;
	}
	
	

	@AfterInject
	protected void afterInject()
	{
		bus.register(this);
	}

	@AfterViews
	protected void afterView()
	{
		init();
		connect();
		aleryInit=true;
	}

	protected void init()
	{}

	/***
	 * 填充数据 连接网络等
	 */
	protected void connect()
	{}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (bus != null) bus.unregister(this);
	}

	
	
	/**
	 * 一个bug
	 */
	@Override
	public void onDetach()
	{
		super.onDetach();
		try
		{
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		} catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}


}
