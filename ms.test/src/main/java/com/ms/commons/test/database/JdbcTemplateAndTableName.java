/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * JdbcTemplate and table name
 * 
 * @author zxc Apr 13, 2013 11:38:12 PM
 */
public class JdbcTemplateAndTableName {

    private JdbcTemplate jdbcTemplate;
    private String       tableName;

    public JdbcTemplateAndTableName(JdbcTemplate jdbcTemplate, String tableName) {
        super();
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
