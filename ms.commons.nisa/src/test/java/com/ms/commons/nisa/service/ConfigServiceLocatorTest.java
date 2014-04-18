/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.service;

import junit.framework.TestCase;

import com.ms.commons.nisa.interfaces.ConfigService;

/**
 * @author zxc Apr 12, 2013 6:53:44 PM
 */
public class ConfigServiceLocatorTest extends TestCase {

    public void testgetCongfigService() {
        System.setProperty("nisa.mina.client.start", "false");
        ConfigService congfigService = ConfigServiceLocator.getCongfigService();
        assertNotNull(congfigService);
    }
}
