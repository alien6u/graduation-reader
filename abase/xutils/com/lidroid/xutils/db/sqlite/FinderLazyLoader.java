package com.lidroid.xutils.db.sqlite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jayqqaa12.abase.kit.common.L;
import com.lidroid.xutils.db.table.ColumnUtils;
import com.lidroid.xutils.db.table.Finder;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * Author: wyouflf Date: 13-9-10 Time: 下午10:50
 */
public class FinderLazyLoader<T> 
{
	
	private final Finder finderColumn;
	private final Object finderValue;

	public  List<T> load()
	{
		try
		{
			List<T> list =  this.getAllFromDb();

			if (list != null) return list;
			else return new ArrayList<T>(1);

		} catch (DbException e)
		{
			e.printStackTrace();
			L.e(" db error "+e.getMessage());
			return new ArrayList<T>(1);
		}

	}

	public FinderLazyLoader(Class<?> entityType, String fieldName, Object value)
	{
		this.finderColumn = (Finder) TableUtils.getColumnOrId(entityType, fieldName);
		this.finderValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
	}

	public FinderLazyLoader(Finder finderColumn, Object value)
	{
		this.finderColumn = finderColumn;
		this.finderValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
	}

	public List<T> getAllFromDb() throws DbException
	{
		List<T> entities = null;
		if (finderColumn != null && finderColumn.db != null)
		{
			entities = finderColumn.db.findAll(Selector.from(finderColumn.getTargetEntityType()).where(finderColumn.getTargetColumnName(),
					"=", finderValue));
		}
		return entities;
	}

	public T getFirstFromDb() throws DbException
	{
		T entity = null;
		if (finderColumn != null && finderColumn.db != null)
		{
			entity = finderColumn.db.findFirst(Selector.from(finderColumn.getTargetEntityType()).where(finderColumn.getTargetColumnName(),
					"=", finderValue));
		}
		return entity;
	}
}
