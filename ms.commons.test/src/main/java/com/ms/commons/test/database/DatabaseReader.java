/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.ms.commons.test.common.ExceptionUtil;
import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.database.type.FieldType;

/**
 * @author zxc Apr 13, 2013 11:38:59 PM
 */
public class DatabaseReader {

    static Logger                                    log           = Logger.getLogger(DatabaseReader.class);

    protected static final Map<String, List<String>> tablePkMap    = new HashMap<String, List<String>>();
    protected static final Map<String, FieldType[]>  tableMap      = new HashMap<String, FieldType[]>();
    protected static final Map<String, FieldType>    tableFieldMap = new HashMap<String, FieldType>();

    private static FieldType[] readTableFieldsFromCache(String table) {
        synchronized (tableMap) {
            FieldType[] fieldTypes = tableMap.get(table);
            if (fieldTypes != null) {
                return fieldTypes;
            }
        }
        return null;
    }

    public static FieldType[] readTableFields(JdbcTemplate jdbcTemplate, String table) {
        if (readTableFieldsFromCache(table) != null) {
            return readTableFieldsFromCache(table);
        }

        Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        try {
            return readTableFields(connection, table);
        } finally {
            DataSourceUtils.releaseConnection(connection, jdbcTemplate.getDataSource());
        }
    }

    public static FieldType[] readTableFields(Connection conn, String table) {
        if (readTableFieldsFromCache(table) != null) {
            return readTableFieldsFromCache(table);
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" select * from ");
        sql.append(table);
        sql.append(" where 1 = 2");
        log.info("Query database `" + sql.toString() + "` for table metadata.");

        try {
            ResultSet resultSet = readResultSet(conn, sql.toString());
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<FieldType> fieldTypeList = new ArrayList<FieldType>();
            for (int i = 1; i < (metaData.getColumnCount() + 1); i++) {
                String columnName = metaData.getColumnName(i);
                String columnType = metaData.getColumnTypeName(i);
                int size = metaData.getColumnDisplaySize(i);
                int scale = metaData.getScale(i);
                boolean isFloat = false;
                if ("NUMBER".equals(columnType.toUpperCase())) {
                    if (scale > 0) {
                        isFloat = true;
                    }
                }
                fieldTypeList.add(new FieldType(columnName, columnType, size, scale, isFloat));

                // System.out.p.frameworkn("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                // System.out.p.frameworkn("Column:" + i);
                // System.out.p.frameworkn("\t\t" + metaData.getColumnName(i));
                // System.out.p.frameworkn("\t\t" + metaData.getColumnLabel(i));
                // System.out.p.frameworkn("\t\t" + metaData.getColumnDisplaySize(i));
                // System.out.p.frameworkn("\t\t" + metaData.getColumnTypeName(i));
                // System.out.p.frameworkn("\t\t" + metaData.isReadOnly(i));
                // System.out.p.frameworkn("\t\t" + metaData.isNullable(i));
                // System.out.p.frameworkn("\t\t" + metaData.getSchemaName(i));
                // System.out.p.frameworkn("\t\t" + metaData.getPrecision(i));
                // System.out.p.frameworkn("\t\t" + metaData.getScale(i));
                // System.out.p.frameworkn("\t\t" + metaData.isAutoIncrement(i));
                // System.out.p.frameworkn("\t\t" + metaData.isCurrency(i));
                // System.out.p.frameworkn("\t\t" + metaData.isSearchable(i));
                // System.out.p.frameworkn("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            }

            FieldType[] fieldTypes = fieldTypeList.toArray(new FieldType[] {});
            synchronized (tableMap) {
                tableMap.put(table, fieldTypes);
            }

            return fieldTypes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static FieldType readTableFieldFromCache(String table, String field) {
        String upperTable = (table == null) ? null : table.toUpperCase();
        String upperField = (field == null) ? null : field.toUpperCase();

        synchronized (tableFieldMap) {
            FieldType fieldType = tableFieldMap.get(upperTable + ":" + upperField);
            if (fieldType != null) {
                return fieldType;
            }
        }
        return null;
    }

    public static FieldType readTableField(JdbcTemplate jdbcTemplate, String table, String field) {
        if (readTableFieldFromCache(table, field) != null) {
            return readTableFieldFromCache(table, field);
        }

        Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        try {
            return readTableField(connection, table, field);
        } finally {
            DataSourceUtils.releaseConnection(connection, jdbcTemplate.getDataSource());
        }
    }

    public static FieldType readTableField(Connection conn, String table, String field) {
        if (readTableFieldFromCache(table, field) != null) {
            return readTableFieldFromCache(table, field);
        }

        String upperTable = (table == null) ? null : table.toUpperCase();
        String upperField = (field == null) ? null : field.toUpperCase();

        FieldType[] fieldTypes = readTableFields(conn, table);
        for (FieldType fieldType : fieldTypes) {
            if (StringUtil.trimedIgnoreCaseEquals(fieldType.getName(), field)) {
                synchronized (tableFieldMap) {
                    tableFieldMap.put(upperTable + ":" + upperField, fieldType);
                }
                return fieldType;
            }
        }
        throw new RuntimeException("Field `" + field + "` cannot found in table `" + table + "`.");
    }

    private static List<String> readTablePKFromCache(String table) {
        // String trimedUpTable = table.trim().toUpperCase();
        synchronized (tablePkMap) {
            List<String> pkList = tablePkMap.get(table);
            if (pkList != null) {
                return (pkList.size() == 0) ? null : pkList;
            }
        }
        return null;
    }

    public static List<String> readTablePK(JdbcTemplate jdbcTemplate, String table) {
        if (readTablePKFromCache(table) != null) {
            return readTablePKFromCache(table);
        }

        Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        try {
            return readTablePK(connection, table);
        } finally {
            DataSourceUtils.releaseConnection(connection, jdbcTemplate.getDataSource());
        }
    }

    public static List<String> readTablePK(Connection conn, String table) {
        if (readTablePKFromCache(table) != null) {
            return readTablePKFromCache(table);
        }

        // String trimedUpTable = table;

        try {
            DatabaseMetaData dmd = conn.getMetaData();
            ResultSet rs = dmd.getPrimaryKeys(null, dmd.getUserName(), table);
            List<String> pkList = new ArrayList<String>();
            while (rs.next()) {
                pkList.add(rs.getString("COLUMN_NAME"));
                // System.err.p.frameworkn(rs.getString("TABLE_CAT"));
                // System.err.p.frameworkn(rs.getString("TABLE_SCHEM"));
                // System.err.p.frameworkn(rs.getString("TABLE_NAME"));
                // System.err.p.frameworkn(rs.getString("COLUMN_NAME"));
                // System.err.p.frameworkn(rs.getString("KEY_SEQ"));
                // System.err.p.frameworkn(rs.getString("PK_NAME"));
            }
            synchronized (tablePkMap) {
                tablePkMap.put(table, pkList);
            }
            return (pkList.size() == 0) ? null : pkList;
        } catch (SQLException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Map<?, ?>> readTableData(JdbcTemplate jdbcTemplate, String table, String whereSql,
                                                String sortSql, Object[] args) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * ");
        if (hasFromInWhereSql(whereSql)) {
            sql.append(whereSql);
        } else {
            sql.append(" from ");
            sql.append(table);
            if (whereSql != null) {
                sql.append(" where ");
                sql.append(" ");
                sql.append(whereSql);
                sql.append(" ");
            }
        }
        if (sortSql != null) {
            sql.append(" order by ");
            sql.append(sortSql);
        }
        log.info("Query database `" + sql.toString() + "` for data.");

        return (List<Map<?, ?>>) jdbcTemplate.queryForList(sql.toString(), ((args == null) ? (new Object[] {}) : args));
    }

    protected static boolean hasFromInWhereSql(String whereSql) {
        if (whereSql == null) {
            return false;
        }
        return (whereSql.toLowerCase().trim().startsWith("from"));
    }

    protected static ResultSet readResultSet(Connection conn, String sql) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(sql);
            return rset;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
