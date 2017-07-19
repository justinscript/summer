/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.assertion.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ms.commons.test.annotation.napi.Optional;
import com.ms.commons.test.annotation.napi.Required;
import com.ms.commons.test.annotation.napi.SupplyBy;
import com.ms.commons.test.assertion.Assert;
import com.ms.commons.test.assertion.Assertion;
import com.ms.commons.test.memorydb.MemoryDatabase;

/**
 * @author zxc Apr 13, 2013 11:13:51 PM
 */
public class DataBaseAssertion implements Assertion {

    @Optional(supply = SupplyBy.Framework)
    private JdbcTemplate   jdbcTemplate;
    @Optional(supply = SupplyBy.Framework)
    private MemoryDatabase database;
    @Required
    private String         table;
    @Optional(supply = SupplyBy.None, comment = "�����õ�ʱ���ѯ��ݿ��Ӧ������м�¼")
    private String         where;
    @Optional(supply = SupplyBy.None, comment = "�����õ�ʱ��������")
    private String         sort;
    @Optional(supply = SupplyBy.None)
    private Object[]       arguments;
    @Optional(supply = SupplyBy.None)
    private String[]       columns;

    public DataBaseAssertion jdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }

    public DataBaseAssertion database(MemoryDatabase database) {
        this.database = database;
        return this;
    }

    public DataBaseAssertion table(String table) {
        this.table = table;
        return this;
    }

    public DataBaseAssertion from(String table) {
        this.table = table;
        return this;
    }

    public DataBaseAssertion where(String where) {
        this.where = where;
        return this;
    }

    public DataBaseAssertion sort(String sort) {
        this.sort = sort;
        return this;
    }

    public DataBaseAssertion orderBy(String sort) {
        this.sort = sort;
        return this;
    }

    public DataBaseAssertion arguments(Object... arguments) {
        this.arguments = arguments;
        return this;
    }

    public DataBaseAssertion columns(String... columns) {
        this.columns = columns;
        return this;
    }

    public DataBaseAssertion column(String columnSplitByComma) {
        String[] cols = columnSplitByComma.split(",");
        if (cols != null) {
            List<String> colList = new ArrayList<String>();
            for (String col : cols) {
                String trimedCol = col.trim();
                if (trimedCol.length() > 0) {
                    colList.add(trimedCol);
                }
            }
            if (colList.size() > 0) {
                this.columns = colList.toArray(new String[0]);
            }
        }

        return this;
    }

    public void doAssert() {
        Assert.assertResultTable(jdbcTemplate, database, table, where, sort, arguments, columns);
    }
}
