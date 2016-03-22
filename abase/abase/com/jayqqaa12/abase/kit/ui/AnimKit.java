package com.jayqqaa12.abase.kit.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;


/**
 * 常见动画 工具
* @author jayqqaa12 
* @date 2013-6-5
 */
public class AnimKit  
{
	/**
	 * alpha 动画  淡入 
	 * @param view
	 */
	public static void alphaIn(View view)
	{
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		view.setAnimation(aa);
		view.startAnimation(aa);
	}
	/**
	 * alpha 动画  淡出 
	 * @param view
	 */
	public static void alphaOut(View view)
	{
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0f);
		aa.setDuration(2000);
		view.setAnimation(aa);
		view.startAnimation(aa);
	}
	
	/**
	 * 一种 摇摆的 动画
	 * @param view
	 * @param listener
	 */
	public static  void swing(View view,AnimationListener listener)
	{
		TranslateAnimation ta = translate();
		ta.setAnimationListener(listener);		
		view.startAnimation(ta);
	}

	public static  void swing(View view)
	{
		TranslateAnimation ta = translate();
		view.startAnimation(ta);
	}
	
	private static TranslateAnimation translate()
	{
		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		ta.setDuration(200);
		return ta;
	}
	
	/**
	 * 渐入加 旋转 
	 * @param view
	 */
	public static void apphaAndScaleIn(View view)
	{
		view.setVisibility(View.VISIBLE);
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(1000);
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
		sa.setDuration(1000);
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(sa);
		set.addAnimation(aa);
		view.setAnimation(set);
		view.startAnimation(set);
	}
	
	/**
	 * 渐出 加 旋转 
	 * @param view
	 */
	public static void apphaAndScaleOut(View view)
	{
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		aa.setDuration(1000);
		ScaleAnimation sa = new ScaleAnimation(1.0f, 0.0f, 1.0f,0.0f);
		sa.setDuration(1000);
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(sa);
		set.addAnimation(aa);
		view.setAnimation(set);
		view.startAnimation(set);
		view.setVisibility(View.INVISIBLE);
	}
	
	

}
