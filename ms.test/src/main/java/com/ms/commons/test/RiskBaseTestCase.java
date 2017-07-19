/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test;

import javax.sql.DataSource;

import org.junit.runner.RunWith;
import org.springframework.transaction.PlatformTransactionManager;

import com.ms.commons.test.integration.junit4.internal.IntlTestBlockJUnit4ClassRunner;

/**
 * @author zxc Apr 13, 2013 11:16:19 PM
 */
@RunWith(IntlTestBlockJUnit4ClassRunner.class)
public abstract class RiskBaseTestCase extends BaseTestCase {

    public void setRiskTransactionManager(PlatformTransactionManager ptm) {
        this.transactionManager = ptm;
    }

    public void setRiskDbcpDataSource(DataSource ds) {
        this.setDataSource(ds);
    }
}
