/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas;

import java.io.Serializable;
import java.util.Map;

import junit.framework.TestCase;

import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.interfaces.UdasService;
import com.ms.commons.udas.service.UdasServiceLocator;

/**
 * @author zxc Apr 12, 2013 6:35:46 PM
 */
public class UdasServiceImplTest extends TestCase {

    public void testgetBulkKV() {
        UdasService udasService = UdasServiceLocator.getUdasService("testtestDataSource");
        udasService.put("test1", 60, new UdasObj("value1"));
        udasService.put("test2", 60, new UdasObj("value2"));
        udasService.put("test3", 60, new UdasObj("value3"));

        Map<String, Serializable> bulkKV = udasService.getBulkKV("test1", "test2", "test5");
        assertEquals("value1", (String) bulkKV.get("test1"));
        assertEquals("value2", (String) bulkKV.get("test2"));
        assertEquals(null, (String) bulkKV.get("test5"));

        udasService.put("test4", 3, new UdasObj("value4"));
        bulkKV = udasService.getBulkKV("test1", "test2", "test4");
        assertEquals("value1", (String) bulkKV.get("test1"));
        assertEquals("value2", (String) bulkKV.get("test2"));
        assertEquals("value4", (String) bulkKV.get("test4"));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            fail();
        }
        bulkKV = udasService.getBulkKV("test1", "test2", "test4");
        assertEquals("value1", (String) bulkKV.get("test1"));
        assertEquals("value2", (String) bulkKV.get("test2"));
        assertEquals(null, (String) bulkKV.get("test4"));
    }
}
