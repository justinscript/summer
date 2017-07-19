/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.database.sql;

import java.io.Serializable;

/**
 * @author zxc Apr 13, 2013 11:39:29 PM
 */
public class SqlInsertQuery implements Serializable {

    private static final long serialVersionUID = -3386140912304235902L;
    private String            sql;
    private Object[]          params;

    public SqlInsertQuery(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
