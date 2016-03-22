package com.jayqqaa12.abase.kit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentKit
{
	
	public static void replace(int id,Fragment f,FragmentManager context){
		context.beginTransaction().replace(id, f).commitAllowingStateLoss();
		
	}
	
	public static void add(int id,Fragment f,FragmentManager context){
		context.beginTransaction().add(id, f).commitAllowingStateLoss();
	}
	
	public static void remove(Fragment f,FragmentManager context){
		context.beginTransaction().remove( f).commitAllowingStateLoss();
	}
	
	
}
