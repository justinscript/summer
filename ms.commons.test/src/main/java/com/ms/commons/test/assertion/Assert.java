/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.assertion;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.assertion.exception.AssertException;
import com.ms.commons.test.common.ReflectUtil;
import com.ms.commons.test.common.comparator.CompareUtil;
import com.ms.commons.test.common.convert.TypeConvertUtil;
import com.ms.commons.test.common.dbencoding.DbEncodingUtil;
import com.ms.commons.test.database.DatabaseReader;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryField;
import com.ms.commons.test.memorydb.MemoryFieldType;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:12:56 PM
 */
public class Assert {

    public static void assertResult(MemoryDatabase aspect, Object bean) {
        assertResult(aspect, bean, null);
    }

    public static void assertResult(MemoryDatabase aspect, Object bean, String table) {
        assertResult(aspect, bean, table, 0);
    }

    public static void assertResult(MemoryDatabase aspect, Object bean, String table, int row) {
        MemoryTable memoryTable = getMemoryTable(aspect, table);
        if (memoryTable == null) {
            throw new AssertException("Aspect " + messageTableName(table) + " not exists.");
        }
        if (memoryTable.getRowList().size() <= row) {
            throw new AssertException("Aspect " + messageTableName(table) + " row not exists.");
        }
        assertRowEqualsToBean(bean, memoryTable.getRowList().get(row), table, 0);
    }

    public static void assertResultList(MemoryDatabase aspect, List<?> beanList) {
        assertResultList(aspect, beanList, null);
    }

    public static void assertResultList(MemoryDatabase aspect, List<?> beanList, String table) {
        assertResultList(aspect, beanList, table, null);
    }

    public static void assertResultList(MemoryDatabase aspect, List<?> beanList, String table, String[] columns) {
        MemoryTable memoryTable = getMemoryTable(aspect, table);
        if (memoryTable == null) {
            throw new AssertException("Aspect " + messageTableName(table) + " not exists.");
        }
        if (memoryTable.getRowList().size() != beanList.size()) {
            throw new AssertException("Aspect " + messageTableName(table) + " row size is "
                                      + memoryTable.getRowList().size() + ", but actual is " + beanList.size() + ".");
        }
        for (int i = 0; i < memoryTable.getRowList().size(); i++) {
            assertRowEqualsToBean(beanList.get(i), memoryTable.getRowList().get(i), table, i, columns);
        }
    }

    public static void assertResultTable(JdbcTemplate jdbcTemplate, MemoryDatabase aspect, String table) {
        assertResultTable(jdbcTemplate, aspect, table, null, null, null, null);
    }

    public static void assertResultTable(JdbcTemplate jdbcTemplate, MemoryDatabase aspect, String table,
                                         String[] columns) {
        assertResultTable(jdbcTemplate, aspect, table, null, null, null, columns);
    }

    public static void assertResultTable(JdbcTemplate jdbcTemplate, MemoryDatabase aspect, String table,
                                         String whereSql, String sortSql, Object[] args) {
        assertResultTable(jdbcTemplate, aspect, table, whereSql, sortSql, args, null);
    }

    public static void assertResultTable(JdbcTemplate jdbcTemplate, MemoryDatabase aspect, String table,
                                         String whereSql, String sortSql, Object[] args, String[] columns) {
        List<Map<?, ?>> dataList = DatabaseReader.readTableData(jdbcTemplate, table, whereSql, sortSql, args);

        MemoryTable memoryTable = getMemoryTable(aspect, table);
        if (memoryTable == null) {
            throw new AssertException("Aspect " + messageTableName(table) + " not exists.");
        }
        if (memoryTable.getRowList().size() != dataList.size()) {
            throw new AssertException("Aspect " + messageTableName(table) + " row size is "
                                      + memoryTable.getRowList().size() + ", but actual is " + dataList.size() + ".");
        }

        String actualTable = getActualSqlTable(whereSql, table);
        for (int i = 0; i < memoryTable.getRowList().size(); i++) {
            assertRowEqualsToMap(dataList.get(i), memoryTable.getRowList().get(i), actualTable, i, columns);
        }
    }

    protected static void assertRowEqualsToMap(Map<?, ?> map, MemoryRow row, String table, int index) {
        assertRowEqualsToMap(map, row, table, index, null);
    }

    protected static void assertRowEqualsToMap(Map<?, ?> map, MemoryRow row, String table, int index, String[] columns) {
        Set<String> columnSet = toUpperCaseSet(columns);

        for (int i = 0; i < row.getFieldList().size(); i++) {
            MemoryField field = row.getFieldList().get(i);
            if (columnSet == null || columnSet.contains(field.getName().toUpperCase())) {
                Object value = (field.getType() == MemoryFieldType.Null) ? null : field.getValue();
                Object mapValue = DbEncodingUtil.decode(table, field.getName(), map.get(field.getName().toUpperCase()));
                Class<?> mfc = (mapValue == null) ? null : mapValue.getClass();
                Object convertedValue = (mfc == null) ? value : TypeConvertUtil.convert(mfc, value);
                if (!CompareUtil.isObjectEquals(mapValue, convertedValue)) {
                    throw new AssertException("Aspect `" + convertedValue + "` but actual value is `" + mapValue
                                              + "` at " + messageTableName(table) + " [row:" + index + ", col:" + i
                                              + "].");
                }
            }
        }
    }

    protected static void assertRowEqualsToBean(Object bean, MemoryRow row, String table, int index) {
        assertRowEqualsToBean(bean, row, table, index, null);
    }

    protected static void assertRowEqualsToBean(Object bean, MemoryRow row, String table, int index, String[] columns) {
        Set<String> columnSet = toUpperCaseSet(columns);

        for (int i = 0; i < row.getFieldList().size(); i++) {
            MemoryField field = row.getFieldList().get(i);
            if (columnSet == null || columnSet.contains(field.getName().toUpperCase())) {
                Object value = (field.getType() == MemoryFieldType.Null) ? null : field.getValue();
                if (!ReflectUtil.isValueEqualsBean(bean, field.getName().trim(), value)) {
                    throw new AssertException("Aspect `"
                                              + ReflectUtil.getValueAccroudBean(bean, field.getName().trim(), value)
                                              + "` but actual value is `"
                                              + ReflectUtil.getValueFromBean(bean, field.getName().trim()) + "` at "
                                              + messageTableName(table) + " [row:" + index + ", col:" + i + "].");
                }
            }
        }
    }

    private static Set<String> toUpperCaseSet(String[] columns) {
        Set<String> columnSet = null;
        if (columns != null) {
            columnSet = new HashSet<String>(columns.length);
            for (String col : columns) {
                columnSet.add(col.toUpperCase());
            }
        }
        return columnSet;
    }

    protected static MemoryTable getMemoryTable(MemoryDatabase database, String name) {
        if (name == null) {
            return database.getTableList().get(0);
        }
        for (MemoryTable table : database.getTableList()) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }

    protected static String messageTableName(String table) {
        return (table == null) ? "default table" : "table `" + table + "`";
    }

    protected static String getActualSqlTable(String whereSql, String table) {
        String defaultTable = table;
        if ((whereSql == null) || (whereSql.trim().length() == 0)) {
            return defaultTable;
        }
        String lowerWhereSql = whereSql.trim().toLowerCase();
        if (!lowerWhereSql.startsWith("from")) {
            return defaultTable;
        }
        Matcher m = Pattern.compile("\\s*from\\s+(\\w+)(\\s+.*)?").matcher(lowerWhereSql);
        if (!m.matches()) {
            return defaultTable;
        }
        return m.group(1);
    }
}
