/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.database;

/**
 * @author zxc Apr 13, 2013 11:37:35 PM
 */
public class SecondarySetting {

    private JdbcManagementTool    jdbcManagementTool;
    private SecondaryPreareFilter secondaryPreareFilter;

    public SecondarySetting(JdbcManagementTool jdbcManagementTool, SecondaryPreareFilter secondaryPreareFilter) {
        this.jdbcManagementTool = jdbcManagementTool;
        this.secondaryPreareFilter = secondaryPreareFilter;
    }

    public JdbcManagementTool getJdbcManagementTool() {
        return jdbcManagementTool;
    }

    public void setJdbcManagementTool(JdbcManagementTool jdbcManagementTool) {
        this.jdbcManagementTool = jdbcManagementTool;
    }

    public SecondaryPreareFilter getSecondaryPreareFilter() {
        return secondaryPreareFilter;
    }

    public void setSecondaryPreareFilter(SecondaryPreareFilter secondaryPreareFilter) {
        this.secondaryPreareFilter = secondaryPreareFilter;
    }
}
