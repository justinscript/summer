/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database.tool;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author zxc Apr 13, 2013 11:39:20 PM
 */
public class JdbcTemplateTool {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateTool(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static JdbcTemplateTool getInstance(JdbcTemplate jdbcTemplate) {
        return new JdbcTemplateTool(jdbcTemplate);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplateTool clearSingleTable(String table, String whereSql) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from " + table);
        if (StringUtils.isNotBlank(whereSql)) {
            sql.append(" where ").append(whereSql);
        }
        getJdbcTemplate().execute(sql.toString());
        return this;
    }

    public JdbcTemplateTool clearTable(String... tables) {
        for (String t : tables) {
            clearSingleTable(t, null);
        }
        return this;
    }
}
