/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.security;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 12, 2013 5:28:47 PM
 */
public class SecurityUtilsTest extends TestCase {

    public void testMD5() {
        String sourcePassword = "abc123...@&*(_)(&^%$!~..../";
        for (int i = 0; i < 1000; i++) {
            String a1 = SecurityUtils.createMD5(sourcePassword);
            String a2 = SecurityUtils.createMD5(sourcePassword);
            assertTrue(StringUtils.equals(a1, a2));
        }
    }
}
