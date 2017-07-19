/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.jdbc;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 5:19:06 PM
 */
public class DataSourceTest extends TestCase {

    public void testInit() {
        DataSource ds = new DataSource();
        try {
            ds.init();
        } catch (Exception e) {
            fail();
        }
    }

    public void testInit2() {
        System.setProperty(DataSource.KEY_DATA_SOURCE_PROPERTIES, "/Users/zxc/aaaa.properties");
        DataSource ds = new DataSource();
        try {
            ds.init();
        } catch (Exception e) {
            return;
        }
        fail();
    }
}
