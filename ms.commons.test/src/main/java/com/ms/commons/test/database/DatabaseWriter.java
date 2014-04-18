/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.common.convert.DataBaseTypeConvertUtil;
import com.ms.commons.test.common.dbencoding.DbEncodingUtil;
import com.ms.commons.test.database.sql.SqlInsertQuery;
import com.ms.commons.test.database.type.FieldType;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;

/**
 * @author zxc Apr 13, 2013 11:38:51 PM
 */
public class DatabaseWriter {

    static Logger log = Logger.getLogger(DatabaseWriter.class);

    public static void clearAll(JdbcTemplate jdbcTemplate) {
        // this method will never implement, because it so dangerous
    }

    public static void clearTable(JdbcTemplate jdbcTemplate, String table) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ");
        sql.append(table);
        log.error("Execute `" + sql.toString() + "` for clear table.");

        jdbcTemplate.execute(sql.toString());
    }

    public static void deleteTableDataByPKs(JdbcTemplate jdbcTemplate, String table, List<String> pkList,
                                            List<Object> pkValueList) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ");
        sql.append(table);
        sql.append(" where ");
        for (int i = 0; i < pkList.size(); i++) {
            if (i > 0) {
                sql.append(" and ");
            }
            sql.append(pkList.get(i)).append(" = ? ");
        }
        log.error("Execute `" + sql.toString() + "` for delete data. Data:[" + pkValueList + "]");

        jdbcTemplate.update(sql.toString(), pkValueList.toArray());
    }

    public static void deleteTableData(JdbcTemplate jdbcTemplate, String table, String pk, Object pkValue) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ");
        sql.append(table);
        sql.append(" where ").append(pk).append(" = ?");
        log.error("Execute `" + sql.toString() + "` for delete data .Data:[" + pkValue + "]");

        jdbcTemplate.update(sql.toString(), new Object[] { pkValue });
    }

    public static void writeData(JdbcTemplate jdbcTemplate, MemoryDatabase database) {
        for (MemoryTable table : database.getTableList()) {
            writeData(jdbcTemplate, table);
        }
    }

    public static void writeData(JdbcTemplate jdbcTemplate, MemoryTable table) {
        for (MemoryRow row : table.getRowList()) {
            SqlInsertQuery insertQuery = createSqlInsertQuery(jdbcTemplate, table, row);
            log.error("Execute `" + insertQuery.getSql() + "` for insert data.");

            jdbcTemplate.update(insertQuery.getSql(), insertQuery.getParams());
        }
    }

    public static SqlInsertQuery createSqlInsertQuery(JdbcTemplate jdbcTemplate, MemoryTable table, MemoryRow row) {

        StringBuilder sql = new StringBuilder();
        List<Object> paramList = new ArrayList<Object>();

        List<String> fieldList = new ArrayList<String>();
        List<String> quotList = new ArrayList<String>();
        for (int i = 0; i < row.getFieldList().size(); i++) {
            String fieldName = row.getFieldList().get(i).getName().toUpperCase().trim();
            FieldType fieldType = DatabaseReader.readTableField(jdbcTemplate, table.getName(), fieldName);
            fieldList.add(fieldName);
            quotList.add("?");
            Object convertedValue = DataBaseTypeConvertUtil.convert(fieldType, row.getFieldList().get(i).getValue());
            convertedValue = DbEncodingUtil.encode(table.getName(), fieldName, convertedValue);
            paramList.add(convertedValue);
        }

        sql.append(" insert into ");
        sql.append(table.getName());
        sql.append(" (" + StringUtil.join(fieldList.toArray(new String[] {}), ", ") + ") ");
        sql.append(" values (" + StringUtil.join(quotList.toArray(new String[] {}), ", ") + ") ");

        return new SqlInsertQuery(sql.toString(), paramList.toArray());
    }
}
