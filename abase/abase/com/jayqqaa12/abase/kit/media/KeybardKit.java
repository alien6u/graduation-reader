package com.jayqqaa12.abase.kit.media;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import com.jayqqaa12.abase.kit.ManageKit;

public class KeybardKit 
{
	
	/**
	 * 是否 为 锁屏 状态 
	 * 
	 * @return
	 */
	public static boolean isKeyguardRestricted()
	{
		return ManageKit.getKeyguardManager().inKeyguardRestrictedInputMode();
	}

	
	public static void hideKeybard(Activity activiy){
		
		ManageKit.getInputMethodManager().hideSoftInputFromWindow(activiy.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
	}
	

}
