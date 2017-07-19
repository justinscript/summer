/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.prepare.impl;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.annotation.napi.Optional;
import com.ms.commons.test.annotation.napi.Required;
import com.ms.commons.test.annotation.napi.SupplyBy;
import com.ms.commons.test.memorydb.MemoryDatabase;
import com.ms.commons.test.prepare.Preparation;
import com.ms.commons.test.prepare.PrepareUtil;

/**
 * @author zxc Apr 14, 2013 12:22:28 AM
 */
public class DataBasePreparation implements Preparation {

    @Optional(supply = SupplyBy.Framework)
    private JdbcTemplate   jdbcTemplate;
    @Optional(supply = SupplyBy.Framework)
    private MemoryDatabase database;
    @Optional(supply = SupplyBy.None)
    boolean                clearTable = false;
    @Required
    private String[]       tables;

    public DataBasePreparation jdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }

    public DataBasePreparation database(MemoryDatabase database) {
        this.database = database;
        return this;
    }

    public DataBasePreparation clearTable(boolean clearTable) {
        this.clearTable = clearTable;
        return this;
    }

    public DataBasePreparation tables(String... tables) {
        this.tables = tables;
        return this;
    }

    public void prepare() {
        PrepareUtil.prepareDataBase(jdbcTemplate, database, clearTable, tables);
    }
}
