/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.log;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 1:28:05 PM
 */
public class LoggerFactoryWrapperTest extends TestCase {

    public void testgetLogger() {
        ExpandLogger logger1 = LoggerFactoryWrapper.getLogger(LoggerFactoryWrapperTest.class);
        ExpandLogger logger2 = LoggerFactoryWrapper.getLogger(LoggerFactoryWrapperTest.class);
        assertEquals(true, logger1 == logger2);

        ExpandLogger logger3 = LoggerFactoryWrapper.getLogger(SqlLoggerHandler.class);
        ExpandLogger logger4 = LoggerFactoryWrapper.getLogger(SqlLoggerHandler.class);
        assertEquals(true, logger3 == logger4);

        assertEquals(false, logger1 == logger3);
    }
}
