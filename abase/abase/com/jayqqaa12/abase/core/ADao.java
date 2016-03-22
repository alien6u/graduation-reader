package com.jayqqaa12.abase.core;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.EBean;

import com.jayqqaa12.abase.kit.common.L;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

@EBean
public class ADao
{
	private static DbKit db = DbKit.create(Abase.getContext());


	public boolean save(Object entity)
	{
		try
		{
			db.save(entity);
			return true;
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 保存并查询 返回
	 * 
	 * @param entity
	 * @return
	 */

	public <T> T saveAndFind(Object entity, Selector selector)
	{
		try
		{
			db.save(entity);
			return db.findFirst(selector);

		} catch (Exception e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();

			return null;
		}
	}

	public boolean saveBindingId(Object entity)
	{
		try
		{
			return db.saveBindingId(entity);

		} catch (DbException e)
		{
			e.printStackTrace();

			return false;
		}
	}

	public boolean saveAll(List<?> entity)
	{

		try
		{
			db.saveAll(entity);
			return true;
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public boolean delete(Object entity)
	{

		try
		{
			db.delete(entity);
			return true;
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteAll(Class<?> clazz)
	{

		try
		{
			db.deleteAll(clazz);
			return true;
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(Class<?> entityType, WhereBuilder whereBuilder)
	{

		try
		{
			db.delete(entityType, whereBuilder);
			return true;
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(Object entity, String updateColumnNames)
	{

		try
		{
			db.update(entity, updateColumnNames);
			return true;
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public <T> T find(Class<T> entityType, Object idValue)
	{

		try
		{
			return db.findById(entityType, idValue);
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public <T> T findFirst(Class<T> entityType)
	{

		try
		{
			return db.findFirst(entityType);
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public <T> T findFirst(Selector selector)
	{

		try
		{
			return db.findFirst(selector);
		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public <T> List<T> findAll(Class<T> entityType)
	{

		try
		{
			List<T> list = db.findAll(entityType);
			if (list == null) return new ArrayList<T>(1);
			else return list;

		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<T>(1);
		}
	}

	public <T> List<T> findAll(Selector selector)
	{

		try
		{
			List<T> list = db.findAll(selector);
			if (list == null) return new ArrayList<T>(1);
			else return list;

		} catch (DbException e)
		{
			L.e(" read db error " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<T>(1);
		}
	}

	public boolean saveOrUpdate(Object entity)
	{

		try
		{
			db.saveOrUpdate(entity);

			return true;
		} catch (DbException e)
		{
			e.printStackTrace();
			return false;
		}

	}

	public boolean saveOrUpdateAll(List<?> entities)
	{

		try
		{
			db.saveOrUpdateAll(entities);
			return true;
		} catch (DbException e)
		{
			e.printStackTrace();
			return false;
		}

	}

}
