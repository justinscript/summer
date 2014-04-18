/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.cache.BuiltInCacheKey;
import com.ms.commons.test.cache.ThreadContextCache;
import com.ms.commons.test.common.ReflectUtil;
import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.database.DatabaseWriter;
import com.ms.commons.test.database.JdbcTemplateAndTableName;
import com.ms.commons.test.database.SecondarySetting;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.memorydb.MemoryRow;
import com.ms.commons.test.memorydb.MemoryTable;
import com.ms.commons.test.prepare.event.PrepareEvent;
import com.ms.commons.test.prepare.event.PrepareEventUtil;

/**
 * @author zxc Apr 14, 2013 12:21:32 AM
 */
public class PrepareUtil {

    static Logger log = Logger.getLogger(PrepareUtil.class);

    /**
     * @param jdbcTemplate
     * @deprecated see {@link DatabaseWriter#clearAll(JdbcTemplate)}
     */
    public static void clearAll(JdbcTemplate jdbcTemplate) {
        log.info("Prepare: clear all.");
        DatabaseWriter.clearAll(jdbcTemplate);
    }

    public static void clearTable(JdbcTemplate jdbcTemplate, String table) {
        log.info("Prepare: clear table `" + table + "`.");
        DatabaseWriter.clearTable(jdbcTemplate, table);
    }

    public static void prepareDataBase(JdbcTemplate jdbcTemplate, MemoryDatabase database) {
        prepareDataBase(jdbcTemplate, database, false);
    }

    public static void prepareDataBase(JdbcTemplate jdbcTemplate, MemoryDatabase database, boolean clearTable) {
        List<String> tableList = new ArrayList<String>();
        for (MemoryTable table : database.getTableList()) {
            tableList.add(table.getName());
        }
        prepareDataBase(jdbcTemplate, database, clearTable, tableList.toArray(new String[0]));
    }

    public static void prepareDataBase(JdbcTemplate jdbcTemplate, MemoryDatabase database, String... tables) {
        prepareDataBase(jdbcTemplate, database, false, tables);
    }

    public static void prepareDataBase(JdbcTemplate jdbcTemplate, MemoryDatabase database, boolean clearTable,
                                       String... tables) {
        PrepareEvent.PrepareEventContext context = new PrepareEvent.PrepareEventContext(jdbcTemplate, database,
                                                                                        clearTable, tables);

        List<PrepareEvent> events = PrepareEventUtil.getPrepareEvents();
        for (PrepareEvent event : events) {
            event.onPrepareDatabase(context);
        }

        for (String table : tables) {
            boolean tabledFound = false;
            for (MemoryTable tab : database.getTableList()) {
                if (StringUtil.trimedIgnoreCaseEquals(tab.getName(), table)) {
                    tabledFound = true;
                    for (PrepareEvent e : events) {
                        e.onPrepareDatabaseTable(new PrepareEvent.PrepareEventContext(context, tab.getName()));
                    }

                    prepareDataBaseTable(jdbcTemplate, tab, clearTable);

                    for (PrepareEvent e : events) {
                        e.onPrepareDatabaseTableFinish(new PrepareEvent.PrepareEventContext(context, tab.getName()));
                    }
                }
            }

            if (!tabledFound) {
                throw new RuntimeException("Table " + table + " not found!");
            }
        }

        for (PrepareEvent event : events) {
            event.onPrepareDatabaseFinish(context);
        }
    }

    public static void prepareDataBaseTable(JdbcTemplate jdbcTemplate, MemoryTable table, boolean clearTable) {

        JdbcTemplateAndTableName jdbcTemplateAndTableName = PrepareUtil.getJdbcTemplate(jdbcTemplate, table.getName());
        // change table name here
        MemoryTable newTable = new MemoryTable(jdbcTemplateAndTableName.getTableName());
        newTable.setRowList(table.getRowList());
        table = newTable;

        log.info("Prepare: prepare table `" + table + "` width clear table flag `" + clearTable + "`.");
        if (clearTable) {
            clearTable(jdbcTemplateAndTableName.getJdbcTemplate(), table.getName());
        }
        DatabaseWriter.writeData(jdbcTemplateAndTableName.getJdbcTemplate(), table);
    }

    public static JdbcTemplateAndTableName getJdbcTemplate(JdbcTemplate jdbcTemplate, String table) {
        SecondarySetting secondarySetting = ThreadContextCache.get(SecondarySetting.class,
                                                                   BuiltInCacheKey.SecondarySetting);
        if (secondarySetting != null) {
            StringBuilder newTableName = new StringBuilder();
            if (secondarySetting.getSecondaryPreareFilter().accept(table, newTableName)) {
                return new JdbcTemplateAndTableName(secondarySetting.getJdbcManagementTool().getJdbcTemplate(),
                                                    newTableName.toString());
            }
        }
        return new JdbcTemplateAndTableName(jdbcTemplate, table);
    }

    public static <T> T prepareObject(MemoryDatabase database, Class<T> clazz) {
        return prepareObjectList(database, clazz, null, 1).get(0);
    }

    public static <T> T prepareObject(MemoryDatabase database, Class<T> clazz, String table) {
        return prepareObjectList(database, clazz, table, 1).get(0);
    }

    public static <T> List<T> prepareObjectList(MemoryDatabase database, Class<T> clazz) {
        return prepareObjectList(database, clazz, null, Integer.MAX_VALUE);
    }

    public static <T> List<T> prepareObjectList(MemoryDatabase database, Class<T> clazz, String table) {
        return prepareObjectList(database, clazz, table, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> prepareObjectList(MemoryDatabase database, Class<T> clazz, String table, int limit) {
        try {
            List<Object> objectList = new ArrayList<Object>();
            List<MemoryRow> rowList = getMemoryTable(database, table).getRowList();
            for (int i = 0; (i < rowList.size()) && (i < limit); i++) {
                Object object = clazz.newInstance();
                ReflectUtil.setRowToBean(object, rowList.get(i));
                objectList.add(object);
            }
            return (List<T>) objectList;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

}
