/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ms.commons.test.annotation.SecondaryJdbcSetting;
import com.ms.commons.test.classloader.IntlTestProperties;
import com.ms.commons.test.common.ExceptionUtil;

/**
 * @author zxc Apr 13, 2013 11:38:40 PM
 */
public class JdbcManagementTool {

    private static final Logger        log                   = Logger.getLogger(JdbcManagementTool.class);

    private SecondaryJdbcSetting       jdbcSetting;
    private DataSource                 dataSource;
    private PlatformTransactionManager transactionManager;
    private TransactionDefinition      transactionDefinition = new DefaultTransactionDefinition();

    private boolean                    defaultRollback       = true;
    private boolean                    complete              = false;
    private int                        transactionsStarted   = 0;
    protected TransactionStatus        transactionStatus;

    public JdbcManagementTool(SecondaryJdbcSetting jdbcSetting) {
        if (jdbcSetting == null) {
            throw new RuntimeException("Jdbc setting cannot be null.");
        }
        this.jdbcSetting = jdbcSetting;

        init();
    }

    public JdbcManagementTool(DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    public void startTransaction() throws Exception {
        this.complete = !this.defaultRollback;

        if (this.transactionManager == null) {
            log.info("No transaction manager set: test will NOT run within a transaction");
        } else if (this.transactionDefinition == null) {
            log.info("No transaction definition set: test will NOT run within a transaction");
        } else {
            startNewTransaction();
        }
    }

    public void finishTrasaction() throws Exception {
        if (this.transactionStatus != null && !this.transactionStatus.isCompleted()) {
            endTransaction();
        }
    }

    public void setComplete() throws UnsupportedOperationException {
        if (this.transactionManager == null) {
            throw new IllegalStateException("No transaction manager set");
        }
        this.complete = true;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    protected void startNewTransaction() throws TransactionException {
        if (this.transactionStatus != null) {
            throw new IllegalStateException("Cannot start new transaction without ending existing transaction: "
                                            + "Invoke endTransaction() before startNewTransaction()");
        }
        if (this.transactionManager == null) {
            throw new IllegalStateException("No transaction manager set");
        }

        this.transactionStatus = this.transactionManager.getTransaction(this.transactionDefinition);
        ++this.transactionsStarted;
        this.complete = !this.defaultRollback;

        if (log.isInfoEnabled()) {
            log.info("Began transaction (" + this.transactionsStarted + "): transaction manager ["
                     + this.transactionManager + "]; default rollback = " + this.defaultRollback);
        }
    }

    protected void endTransaction() {
        if (this.transactionStatus != null) {
            try {
                if (!this.complete) {
                    this.transactionManager.rollback(this.transactionStatus);
                    log.info("Rolled back transaction after test execution");
                } else {
                    this.transactionManager.commit(this.transactionStatus);
                    log.info("Committed transaction after test execution");
                }
            } finally {
                this.transactionStatus = null;
            }
        }
    }

    public void preventTransaction() {
        this.transactionDefinition = null;
    }

    public boolean isDefaultRollback() {
        return defaultRollback;
    }

    public void setDefaultRollback(boolean defaultRollback) {
        this.defaultRollback = defaultRollback;
    }

    synchronized public void init() {
        if (dataSource != null) {
            return;
        }
        try {
            Class.forName(getValue(jdbcSetting.driver()));
            dataSource = new SingleConnectionDataSource(getValue(jdbcSetting.url()), getValue(jdbcSetting.username()),
                                                        getValue(jdbcSetting.password()), false);
            transactionManager = new DataSourceTransactionManager(dataSource);

            log.info("Init secondary jdbc connection: " + getValue(jdbcSetting.driver()));
        } catch (Exception e) {
            throw ExceptionUtil.wrapToRuntimeException(e);
        }
    }

    synchronized public void destory() {
        transactionManager = null;
        transactionDefinition = null;
        dataSource = null;
    }

    private String getValue(String key) {
        if (key.trim().toLowerCase().startsWith("key:")) {
            String k = key.substring("key:".length()).trim();
            return IntlTestProperties.getAntxProperty(k);
        } else {
            return key;
        }
    }
}
