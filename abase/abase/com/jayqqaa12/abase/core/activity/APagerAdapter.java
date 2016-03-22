package com.jayqqaa12.abase.core.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.jayqqaa12.abase.core.ItemView;
import com.jayqqaa12.abase.kit.common.ReflectKit;


/***
 * 
 * 建议 view  使用 fragment 代替
 * @author 12
 *
 * @param <T>
 */
@Deprecated
public class APagerAdapter<T> extends PagerAdapter
{
	protected Map<Integer, View> views = new HashMap<Integer, View>();

	protected List<T> data = new ArrayList<T>();

	public Class<? extends ItemView<T>> clazz;
	public Context context;

	public void setData(List<T> data)
	{
		this.data = data;
		this.notifyDataSetChanged();
	}

	public APagerAdapter()
	{}
	
	public APagerAdapter(Context context,Class< ? extends ItemView<T>> clazz ,List<T> data){
		this.data=data;
		setItemView(clazz, context);
	}
	
	public APagerAdapter(Context context,Class< ? extends ItemView<T>> clazz ,T ... array){
		
		for(T t:array){
			data.add(t);
		}
		setItemView(clazz, context);
	}
	
	public void setItemView(Class<? extends ItemView<T>> clazz, Context context)
	{
		this.clazz = ReflectKit.getSubClass(clazz);
		this.context = context;
	}

	public APagerAdapter( Map<Integer, View> views)
	{
		this.views = views;
	}

	public APagerAdapter( int... layoutRes)
	{
		int i = 0;
		for (int layout : layoutRes)
			this.views.put(i++, View.inflate(context, layout, null));

	}

	public APagerAdapter( View... views)
	{
		int i = 0;
		for (View v : views)
			this.views.put(i++, v);
	}

	@Override
	public void destroyItem(View container, int position, Object object)
	{
		((ViewPager) container).removeView(views.get(position));

	}

	@Override
	public Object instantiateItem(View container, int position)
	{
		if (clazz != null&&views.get(position)==null)
		{
			View view = ItemView.bindView(context, clazz, null, data.get(position));
			views.put(position, view);
		}

		if (views.get(position).getParent() == null) ((ViewPager) container).addView(views.get(position), 0);
		else
		{
			((ViewGroup) views.get(position).getParent()).removeView(views.get(position));
			((ViewPager) container).addView(views.get(position), 0);
		}

		return views.get(position);
	}

	@Override
	public int getCount()
	{
		if(data.size()>0) return data.size();
		else return views.size();
	}

	@Override
	public boolean isViewFromObject(View v, Object o)
	{
		return v == o;
	}

}
