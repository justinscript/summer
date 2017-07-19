/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.jdbc;

/**
 * @author zxc Apr 12, 2013 5:04:40 PM
 */
public class DataSourceException extends RuntimeException {

    private static final long serialVersionUID = -6785028341641345558L;

    public DataSourceException(String msg) {
        super(msg);
    }

    public DataSourceException(Throwable t) {
        super(t);
    }

    public DataSourceException(String msg, Throwable t) {
        super(msg, t);
    }
}
