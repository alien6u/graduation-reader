package com.jayqqaa12.abase.core;

import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.jayqqaa12.abase.kit.common.ReflectKit;


/***
 * 结合 androidannotations 
 * 
 * 使用方法  new APopup(BindView.class)
 * 
 * 其中 bindview 为继承 bindview的子类
 * 
 * 原理类似 adapter的 itemview
 * 
 * @author 12
 *
 */
public class APopup extends PopupWindow
{
	private boolean isMenu = true;

	/***
	 * 必需 重写 initView
	 */
	public APopup()
	{
		setView();
		View view = initView();
		this.setContentView(view);
		setBack(view);
	}


	public APopup(Class<? extends BindView> clazz)
	{
		setView();
		View view = null;
		try
		{
			Method m = ReflectKit.getSubClass(clazz).getMethod("build", new Class[] { Context.class });
			view = (BindView) m.invoke(clazz, new Object[] { Abase.getContext() });

			((BindView) view).bind();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		this.setContentView(view);
		setBack(view);
	}

	public APopup(View view)
	{
		setView();
		this.setContentView(view);
		setBack(view);
	}

	
	/**
	 * 使用 bindview 的时候使用
	 * @return
	 */
	public BindView getBindView(){
		return (BindView) getContentView();
	}

	
	protected View initView()
	{

		return null;
	};

	
	
	private void setBack(View view)
	{
		if (isMenu)
		{

			view.setFocusableInTouchMode(true);
			view.setOnKeyListener(new OnKeyListener()
			{
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event)
				{
					if ((keyCode == KeyEvent.KEYCODE_MENU) && APopup.this.isShowing())
					{
						APopup.this.dismiss();
						return true;
					}
					return false;
				}
			});
		}
	}

	private void setView()
	{
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setOutsideTouchable(true);
	}
	public void setIsMenu(boolean isMenu)
	{

		this.isMenu = isMenu;
	}
}
