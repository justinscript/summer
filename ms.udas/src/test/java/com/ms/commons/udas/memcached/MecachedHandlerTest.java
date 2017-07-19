/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.memcached;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.impl.handler.MemCachedHandler;

/**
 * 此类用户测试SpyMemcachedClient的基本功能
 * 
 * @author zxc Apr 12, 2013 6:39:38 PM
 */
public class MecachedHandlerTest {

    AbstractKVHandler handler;

    @Before
    public void before() {
        handler = new MemCachedHandler("html", "192.168.1.182:11211");
    }

    /**
     * 测试基本的Put和Get
     */
    @Test
    public void handlerPutAndGet() {
        AbstractKVHandler handler = new MemCachedHandler("html", "192.168.1.182:11211");
        HTMLTestObj obj = new HTMLTestObj("<html><body>Page Not Found!</body></html>");
        // put
        UdasObj value = new UdasObj(System.currentTimeMillis(), obj);
        assertTrue(handler.put("key2", value));
        // get
        HTMLTestObj result = (HTMLTestObj) handler.get("key2").getValue();
        assertTrue(result.equals(obj));
        System.out.println(result);

        // key 不存在
        assertNull(handler.get("keynotexisted"));
        // key的值已经存在了
        obj.setContent("<html><body>new content!</body></html>");
        assertTrue(handler.put("key2", new UdasObj(System.currentTimeMillis(), obj)));
        result = (HTMLTestObj) handler.get("key2").getValue();
        assertTrue(result.equals(obj));
        System.out.println(result);
    }

    /**
     * 测试过期时间
     */
    public void testExpireTime() {

    }

    /**
     * 测试请求被挂住的情况
     */
    public void testConnectHangOn() {

    }

    /**
     * 测试连接被拒绝
     */
    public void testConnectRefused() {

    }

    /**
     * 测试Value超过1M
     */
    @Test
    public void testValueOverFlow() {

        String[] values = new String[1024 * 1024 * 5];
        for (int i = 0, j = values.length; i < j; i++) {
            values[i] = "abcdefghi";
        }
        handler.put("key3", new UdasObj(System.currentTimeMillis(), values));
    }
}
