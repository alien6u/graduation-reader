/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.xutils.db.sqlite;

import com.jayqqaa12.abase.core.DbKit;
import com.lidroid.xutils.db.table.*;
import com.lidroid.xutils.exception.DbException;

import java.util.*;

/**
 * Build "insert", "replace",，"update", "delete" and "create" sql.
 */
public class SqlInfoBuilder {

    private SqlInfoBuilder() {
    }

    //*********************************************** insert sql ***********************************************

    public static SqlInfo buildInsertSqlInfo(DbKit db, Object entity) throws DbException {

        List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.size() == 0) return null;

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("INSERT INTO ");
        sqlBuffer.append(TableUtils.getTableName(entity.getClass()));
        sqlBuffer.append(" (");
        for (KeyValue kv : keyValueList) {
            sqlBuffer.append(kv.getKey()).append(",");
            result.addBindArgWithoutConverter(kv.getValue());
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");

        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(")");

        result.setSql(sqlBuffer.toString());

        return result;
    }

    //*********************************************** replace sql ***********************************************

    public static SqlInfo buildReplaceSqlInfo(DbKit db, Object entity) throws DbException {

        List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.size() == 0) return null;

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("REPLACE INTO ");
        sqlBuffer.append(TableUtils.getTableName(entity.getClass()));
        sqlBuffer.append(" (");
        for (KeyValue kv : keyValueList) {
            sqlBuffer.append(kv.getKey()).append(",");
            result.addBindArgWithoutConverter(kv.getValue());
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(") VALUES (");

        int length = keyValueList.size();
        for (int i = 0; i < length; i++) {
            sqlBuffer.append("?,");
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(")");

        result.setSql(sqlBuffer.toString());

        return result;
    }

    //*********************************************** delete sql ***********************************************

    private static String buildDeleteSqlByTableName(String tableName) {
        return "DELETE FROM " + tableName;
    }

    public static SqlInfo buildDeleteSqlInfo(Object entity) throws DbException {
        SqlInfo result = new SqlInfo();

        Class<?> entityType = entity.getClass();
        String tableName = TableUtils.getTableName(entityType);
        Id id = TableUtils.getId(entityType);
        Object idValue = id.getColumnValue(entity);

        if (idValue == null) {
            throw new DbException("this entity[" + entity.getClass() + "]'s id value is null");
        }
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(tableName));
        sb.append(" WHERE ").append(WhereBuilder.b(id.getColumnName(), "=", idValue));

        result.setSql(sb.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(Class<?> entityType, Object idValue) throws DbException {
        SqlInfo result = new SqlInfo();

        String tableName = TableUtils.getTableName(entityType);
        Id id = TableUtils.getId(entityType);

        if (null == idValue) {
            throw new DbException("this entity[" + entityType + "]'s id value is null");
        }
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(tableName));
        sb.append(" WHERE ").append(WhereBuilder.b(id.getColumnName(), "=", idValue));

        result.setSql(sb.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(Class<?> entityType, WhereBuilder whereBuilder) throws DbException {
        String tableName = TableUtils.getTableName(entityType);
        StringBuilder sb = new StringBuilder(buildDeleteSqlByTableName(tableName));

        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            sb.append(" WHERE ").append(whereBuilder.toString());
        }

        return new SqlInfo(sb.toString());
    }

    //*********************************************** update sql ***********************************************

    public static SqlInfo buildUpdateSqlInfo(DbKit db, Object entity, String... updateColumnNames) throws DbException {

        List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.size() == 0) return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }

        Class<?> entityType = entity.getClass();
        String tableName = TableUtils.getTableName(entityType);
        Id id = TableUtils.getId(entityType);
        Object idValue = id.getColumnValue(entity);

        if (null == idValue) {
            throw new DbException("this entity[" + entity.getClass() + "]'s id value is null");
        }

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.getKey())) {
                sqlBuffer.append(kv.getKey()).append("=?,");
                result.addBindArgWithoutConverter(kv.getValue());
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" WHERE ").append(WhereBuilder.b(id.getColumnName(), "=", idValue));

        result.setSql(sqlBuffer.toString());
        return result;
    }

    public static SqlInfo buildUpdateSqlInfo(DbKit db, Object entity, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException {

        List<KeyValue> keyValueList = entity2KeyValueList(db, entity);
        if (keyValueList.size() == 0) return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }

        Class<?> entityType = entity.getClass();
        String tableName = TableUtils.getTableName(entityType);

        SqlInfo result = new SqlInfo();
        StringBuffer sqlBuffer = new StringBuffer("UPDATE ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" SET ");
        for (KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.getKey())) {
                sqlBuffer.append(kv.getKey()).append("=?,");
                result.addBindArgWithoutConverter(kv.getValue());
            }
        }
        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            sqlBuffer.append(" WHERE ").append(whereBuilder.toString());
        }

        result.setSql(sqlBuffer.toString());
        return result;
    }

    //*********************************************** others ***********************************************

    public static SqlInfo buildCreateTableSqlInfo(Class<?> entityType) throws DbException {
        String tableName = TableUtils.getTableName(entityType);
        Id id = TableUtils.getId(entityType);

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
        sqlBuffer.append(tableName);
        sqlBuffer.append(" ( ");

        if (id.isAutoIncrement()) {
            sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append("INTEGER PRIMARY KEY AUTOINCREMENT,");
        } else {
            sqlBuffer.append("\"").append(id.getColumnName()).append("\"  ").append(id.getColumnDbType()).append(" PRIMARY KEY,");
        }

        Collection<Column> columns = TableUtils.getColumnMap(entityType).values();
        for (Column column : columns) {
            if (column instanceof Finder) {
                continue;
            }
            sqlBuffer.append("\"").append(column.getColumnName()).append("\"  ");
            sqlBuffer.append(column.getColumnDbType());
            if (ColumnUtils.isUnique(column.getColumnField())) {
                sqlBuffer.append(" UNIQUE");
            }
            if (ColumnUtils.isNotNull(column.getColumnField())) {
                sqlBuffer.append(" NOT NULL");
            }
            String check = ColumnUtils.getCheck(column.getColumnField());
            if (check != null) {
                sqlBuffer.append(" CHECK(").append(check).append(")");
            }
            sqlBuffer.append(",");
        }

        sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
        sqlBuffer.append(" )");
        return new SqlInfo(sqlBuffer.toString());
    }

    private static KeyValue column2KeyValue(Object entity, Column column) {
        KeyValue kv = null;
        String key = column.getColumnName();
        Object value = column.getColumnValue(entity);
        value = value == null ? column.getDefaultValue() : value;
        if (key != null) {
            kv = new KeyValue(key, value);
        }
        return kv;
    }

    public static List<KeyValue> entity2KeyValueList(DbKit db, Object entity) {

        List<KeyValue> keyValueList = new ArrayList<KeyValue>();

        Class<?> entityType = entity.getClass();
        Id id = TableUtils.getId(entityType);

        if (!id.isAutoIncrement()) {
            Object idValue = id.getColumnValue(entity);
            KeyValue kv = new KeyValue(id.getColumnName(), idValue);
            keyValueList.add(kv);
        }

        Collection<Column> columns = TableUtils.getColumnMap(entityType).values();
        for (Column column : columns) {
            if (column instanceof Finder) {
                ((Finder) column).db = db;
                continue;
            } else if (column instanceof Foreign) {
                ((Foreign) column).db = db;
            }
            KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }
}
