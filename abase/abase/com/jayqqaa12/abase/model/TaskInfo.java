package com.jayqqaa12.abase.model;

import android.graphics.drawable.Drawable;

public class TaskInfo<T>
{

	public String appName;
	public Drawable icon;
	public int pid; // process id 进程的id
	public String memory;
	public long memorySize;
	public String packname;
	public boolean system;
	public boolean check;
	
	
	/**
	 * 这个对象 可以用来 存放 一些 其他 数据  
	 * 
	 * 用在 adapter 的时候 很好用
	 * 
	 */
	public T obj;
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + pid;
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TaskInfo other = (TaskInfo) obj;
		if (pid != other.pid) return false;
		return true;
	}
	
	
	
	
	
	
	
}
