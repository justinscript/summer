/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.integration.mysql.internal;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptor;

/**
 * @author zxc Apr 13, 2013 11:45:28 PM
 */
public class DumpSqlStatementInterceptor implements StatementInterceptor {

    private Logger log = Logger.getLogger(DumpSqlStatementInterceptor.class);

    public void init(Connection conn, Properties props) throws SQLException {
    }

    public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
                                                                                                                 throws SQLException {
        return null;
    }

    public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement,
                                                ResultSetInternalMethods originalResultSet, Connection connection)
                                                                                                                  throws SQLException {
        log.info("Execute SQL:" + sql);
        return null;
    }

    public boolean executeTopLevelOnly() {
        return false;
    }

    public void destroy() {
    }
}
