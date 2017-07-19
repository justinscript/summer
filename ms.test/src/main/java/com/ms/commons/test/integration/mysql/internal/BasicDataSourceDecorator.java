/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.integration.mysql.internal;

import mockit.Mock;
import mockit.MockClass;

import org.apache.commons.dbcp.BasicDataSource;

import com.ms.commons.test.classloader.IntlTestProperties;
import com.ms.commons.test.constants.IntlTestGlobalConstants;

/**
 * @author zxc Apr 13, 2013 11:45:36 PM
 */
@MockClass(realClass = BasicDataSource.class)
public class BasicDataSourceDecorator {

    public BasicDataSource it;

    @Mock(reentrant = true)
    synchronized public void setUrl(String url) {
        if (url != null && IntlTestProperties.isAntxFlagOn(IntlTestGlobalConstants.TESTCASE_DUMP_MYSQL)) {
            // check is mysql
            if (url.trim().toLowerCase().startsWith("jdbc:mysql://")) {
                url += url.contains("?") ? "&" : "?";
                url += "statementInterceptors=" + DumpSqlStatementInterceptor.class.getName();
            }
        }
        it.setUrl(url);
    }
}
