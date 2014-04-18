/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.event;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.memorydb.MemoryDatabase;

/**
 * 准备数据事件
 * 
 * @author zxc Apr 14, 2013 12:22:47 AM
 */
public interface PrepareEvent {

    static class PrepareEventContext {

        JdbcTemplate   jdbcTemplate;
        MemoryDatabase database;
        boolean        clearTable;
        String[]       tables;
        String         currentTable;

        public PrepareEventContext(JdbcTemplate jdbcTemplate, MemoryDatabase database, boolean clearTable,
                                   String[] tables) {
            this(jdbcTemplate, database, clearTable, tables, null);
        }

        public PrepareEventContext(JdbcTemplate jdbcTemplate, MemoryDatabase database, boolean clearTable,
                                   String[] tables, String currentTable) {
            this.jdbcTemplate = jdbcTemplate;
            this.database = database;
            this.clearTable = clearTable;
            this.tables = tables;
            this.currentTable = currentTable;
        }

        public PrepareEventContext(PrepareEventContext context, String currentTable) {
            this(context.jdbcTemplate, context.database, context.clearTable, context.tables, currentTable);
        }

        public JdbcTemplate getJdbcTemplate() {
            return jdbcTemplate;
        }

        public MemoryDatabase getDatabase() {
            return database;
        }

        public boolean isClearTable() {
            return clearTable;
        }

        public String[] getTables() {
            return tables;
        }

        public String getCurrentTable() {
            return currentTable;
        }

    }

    void onPrepareDatabase(PrepareEventContext context);

    void onPrepareDatabaseTable(PrepareEventContext context);

    void onPrepareDatabaseFinish(PrepareEventContext context);

    void onPrepareDatabaseTableFinish(PrepareEventContext context);
}
