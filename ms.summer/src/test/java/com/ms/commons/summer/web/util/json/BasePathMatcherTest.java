/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.util.json;

import java.util.Map;

import junit.framework.TestCase;

import com.ms.commons.summer.web.util.BasePathMatcher;

/**
 * @author zxc Apr 12, 2013 4:07:32 PM
 */
public class BasePathMatcherTest extends TestCase {

    public void testextractUriTemplateVariables() {
        BasePathMatcher matcher = new BasePathMatcher();
        String pattern = "/bops/returned/{method}/{id}";
        String path = "/bops/returned/detail/123";
        Map<String, String> map = matcher.extractUriTemplateVariables(pattern, path);
        assertEquals("detail", map.get("method"));
        assertEquals("123", map.get("id"));
        //
        pattern = "/{method}/{id}.htm";
        path = "/item/123.htm";
        map = matcher.extractUriTemplateVariables(pattern, path);
        assertEquals("item", map.get("method"));
        assertEquals("123", map.get("id"));
        //
        pattern = "/{id}/{method}_{value}.htm";
        path = "/123/item_haha.htm";
        map = matcher.extractUriTemplateVariables(pattern, path);
        assertEquals("item", map.get("method"));
        assertEquals("123", map.get("id"));
        assertEquals("haha", map.get("value"));
        //
        pattern = "/item/{style}_{id}.htm";
        path = "/item/OGL001_123.htm";
        map = matcher.extractUriTemplateVariables(pattern, path);
        assertEquals("123", map.get("id"));
        assertEquals("OGL001", map.get("style"));

    }

    public void testextractUriTemplateVariables2() {
        BasePathMatcher matcher = new BasePathMatcher();
        String pattern = "/item/{value}_{id}";
        String path = "/item/OGL001_123";
        Map<String, String> map = matcher.extractUriTemplateVariables(pattern, path);
        assertEquals("OGL001", map.get("value"));
        assertEquals("123", map.get("id"));
    }

    public void testextractUriTemplateVariables3() {
        BasePathMatcher matcher = new BasePathMatcher();
        String pattern = "/coord/{value}_{id}";
        String path = "/coord/OGL001,OGL002_123";
        Map<String, String> map = matcher.extractUriTemplateVariables(pattern, path);
        assertEquals("OGL001,OGL002", map.get("value"));
        assertEquals("123", map.get("id"));
    }

}
