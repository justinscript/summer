/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.utils;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author zxc Apr 12, 2013 11:23:08 PM
 */
public class HttpUtilTest extends TestCase {

    @Test
    public void testisIntranetIp() {
        assertEquals(true, HttpUtil.isIntranetIp("127.0.0.1"));
        assertEquals(true, HttpUtil.isIntranetIp("192.168.1.30"));
        assertEquals(false, HttpUtil.isIntranetIp("122.193.111.154"));
    }
}
