package com.jayqqaa12.abase.model;

import java.io.Serializable;

public class ConnBean implements Serializable
{

	public String url;
	public int cacheTime;
	public int status;

	public ConnBean()
	{}

	public ConnBean(String url, int cacheTime, int status)
	{
		this.url = url;
		this.cacheTime = cacheTime;
		this.status = status;
	}

}
