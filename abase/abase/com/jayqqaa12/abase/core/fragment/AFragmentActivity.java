package com.jayqqaa12.abase.core.fragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.jayqqaa12.abase.core.Abus;
import com.jayqqaa12.abase.kit.common.ReflectKit;

/***
 * tab +viewpage 建议使用 TabPageIndicator
 * 
 * @author 12
 * 
 */
@EActivity
public class AFragmentActivity extends FragmentActivity implements OnPageChangeListener,OnTabChangeListener

{
	@Bean
	protected Abus bus;

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (bus != null) bus.unregister(this);
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
	}

	protected void init()
	{}

	/***
	 * 填充数据 连接网络等
	 */
	protected void connect()
	{}

	
	

	
	/**
	 * 
	 * 获得 自定义的 tabhost 默认 使用 android.R.id.tabhost
	 * 
	 * @return
	 */
	protected FragmentTabHost getTabHost(int realtabcontentId)
	{
		FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), realtabcontentId);

		return mTabHost;
	}

	public Class[] getSubClass(Class... clazz)
	{

		int i = 0;
		for (Class c : clazz)
		{
			clazz[i++] = ReflectKit.getSubClass(c);
		}

		return clazz;
	}

	/**
	 * 没有使用 getTabHost() 方式时使用 ， 自定义 风格Tab时候用的 快捷方式 同时 实现 IOC 可实现 所有类型的自动 装配
	 * 
	 * @param tabHost
	 * @param tabWidgetLayoutId
	 * @param tabWidgetResIds
	 *            子控件Id的 资源 Id 应该是 {{icon},{text}...}这样的形式 和 tabWidgetIds 顺序
	 *            相对应;
	 * @param tabWidgetIds
	 *            子控件Id
	 * @param lables
	 *            标签
	 * @param Intents
	 *            内容
	 */
	protected FragmentTabHost initMyTab(FragmentTabHost tabHost, int tabWidgetLayoutId, int[][] tabWidgetResIds, int[] tabWidgetIds, Class[] fragments)
	{

		int length = fragments.length;
		int i = -1;

		while (++i < length)
		{
			View tab = (View) LayoutInflater.from(this).inflate(tabWidgetLayoutId, null);

			int j = -1;

			while (++j < tabWidgetIds.length)
			{
				View view = tab.findViewById(tabWidgetIds[j]);
				if (view instanceof TextView) ((TextView) view).setText(tabWidgetResIds[j][i]);
				if (view instanceof ImageView) ((ImageView) view).setImageResource(tabWidgetResIds[j][i]);
			}

			tabHost.addTab(tabHost.newTabSpec(i + "").setIndicator(tab), fragments[i], null);
		}

		tabHost.setOnTabChangedListener(this);
		
		return tabHost;
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{}

	@Override
	public void onPageSelected(int position)
	{}

	@Override
	public void onTabChanged(String tabId)
	{
		
	}
}
