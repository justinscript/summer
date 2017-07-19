/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ms.commons.udas.impl.CacheEnum;
import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.impl.UdasServiceImpl;
import com.ms.commons.udas.impl.handler.AbstractKVHandler;

/**
 * @author zxc Apr 12, 2013 6:37:17 PM
 */
public abstract class AbstartUdasServiceTest {

    static {
        System.setProperty("nisa.mina.client.start", "false");
    }

    private static final String key = "existed";
    protected UdasServiceImpl   udasService;

    @Before
    public void before() {
        udasService = new UdasServiceImpl(CacheEnum.remote_memcached, "test", "test");
        udasService.setHandlers(gethandles());
    }

    protected abstract AbstractKVHandler[] gethandles();

    @Test
    public void testdel() {
        // 插入
        UdasObj udasObj = new UdasObj(System.currentTimeMillis(), new String("testdel"));
        udasService.put(key, udasObj);
        assertNotNull(udasService.getKV(key));
        // 删除
        udasService.del(key);
        assertNull(udasService.getKV(key));
    }

    @Test
    public void testput() {
        // 清理
        udasService.del(key);
        assertNull(udasService.getKV(key));
        // 插入
        UdasObj udasObj = new UdasObj(System.currentTimeMillis(), new String("testput"));
        udasService.put(key, udasObj);
        assertNotNull(udasService.getKV(key));
    }

    @Test
    @Ignore
    public void testputwithExp() throws Exception {
        int expireTimeinSecondes = 3;// 设置为3秒过期
        UdasObj udasObj = new UdasObj(System.currentTimeMillis(), new String("testputwithExp"));
        udasService.put(key, expireTimeinSecondes, udasObj);
        assertNotNull(udasService.getKV(key));
        // 过一会
        Thread.sleep(expireTimeinSecondes * 1000);
        assertNull(udasService.getKV(key));
    }

    @Test
    public void testgetKV() {
        // 插入
        String value = new String("testdel");
        UdasObj udasObj = new UdasObj(System.currentTimeMillis(), value);
        udasService.put(key, udasObj);
        // 获取对象
        String result = (String) udasService.getKV(key);
        assertTrue(value.equals(result));
    }

    @After
    public void after() {
        udasService.del(key);
    }
}
