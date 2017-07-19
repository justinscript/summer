/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.ms.commons.udas.interfaces.UdasService;
import com.ms.commons.udas.service.UdasServiceLocator;

/**
 * @author zxc Apr 12, 2013 6:35:21 PM
 */
public class UdasServiceLocatorTest {

    @Before
    public void before() {
        System.setProperty("nisa.mina.client.start", "false");
    }

    @Test
    public void test() {
        UdasService udasService = UdasServiceLocator.getUdasService("htmlDataSource");
        assertNotNull(udasService);
    }

    @Test
    public void testGetAll() {
        assertNotNull(UdasServiceLocator.getAllUdasServiceMap());
    }
}
