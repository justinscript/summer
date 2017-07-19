/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.handler;

import java.util.Map;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 4:06:27 PM
 */
public class DataBinderUtilTest extends TestCase {

    public void testgetPathValues() {
        String[] patterns = { "/item/{value}_{id}", "/item/{id}" };
        String path = "/item/123";
        Map<String, String> pathValues = DataBinderUtil.getPathValues(patterns, path);
        assertEquals("123", pathValues.get("id"));
        assertEquals(null, pathValues.get("value"));
    }

    public void testgetPathValues2() {
        String[] patterns = { "/item/{value}_{id}", "/item/{id}" };
        String path = "/item/OGL001_123";
        Map<String, String> pathValues = DataBinderUtil.getPathValues(patterns, path);
        assertEquals("123", pathValues.get("id"));
        assertEquals("OGL001", pathValues.get("value"));
    }

    public void testgetPathValues3() {
        String[] patterns = { "/item/{value}_{id}", "/item/{id}" };
        String path = "/item/OGL001,OGL002_123";
        Map<String, String> pathValues = DataBinderUtil.getPathValues(patterns, path);
        assertEquals("123", pathValues.get("id"));
        assertEquals("OGL001,OGL002", pathValues.get("value"));
    }

    public void testgetPathValues4() {
        String[] patterns = { "/*/{value}_{id}", "/*/{id}" };
        String path = "/item/OGL001,OGL002_123";
        Map<String, String> pathValues = DataBinderUtil.getPathValues(patterns, path);
        assertEquals("123", pathValues.get("id"));
        assertEquals("OGL001,OGL002", pathValues.get("value"));
    }

    public void testgetPathValues5() {
        String[] patterns = { "/item/{value}_{id}", "/item/{id}" };
        String path = "/item/OGL001_123_321";
        Map<String, String> pathValues = DataBinderUtil.getPathValues(patterns, path);
        assertEquals("321", pathValues.get("id"));
        assertEquals("OGL001_123", pathValues.get("value"));
    }

    public void testgetPathValues6() {
        String[] patterns = { "/item/{value}_{id}", "/item/{id}" };
        String path = "/item/_321";
        Map<String, String> pathValues = DataBinderUtil.getPathValues(patterns, path);
        assertEquals("321", pathValues.get("id"));
        assertEquals("", pathValues.get("value"));
    }
}
