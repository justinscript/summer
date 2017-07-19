/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import com.ms.commons.udas.impl.CacheEnum;
import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.impl.UdasServiceImpl;
import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.impl.handler.InnerMemHandler;
import com.ms.commons.udas.interfaces.DealHandler;

/**
 * @author zxc Apr 12, 2013 6:35:39 PM
 */
public class TestMuliDataSourceCache {

    private UdasServiceImpl mcache = null;

    public void tearDown1() {
        mcache = null;
    }

    @Before
    public void setUp1() {
        mcache = new UdasServiceImpl(CacheEnum.inner_mem, "test", "config.key");
        AbstractKVHandler h1 = new InnerMemHandler();
        String key1 = UdasServiceImpl.createKey("1", "test");
        String key2 = UdasServiceImpl.createKey("2", "test");
        String key3 = UdasServiceImpl.createKey("3", "test");
        String key4 = UdasServiceImpl.createKey("4", "test");

        h1.put(key1, new UdasObj(System.currentTimeMillis(), "h1_1"));
        AbstractKVHandler h2 = new InnerMemHandler();
        h2.put(key2, new UdasObj(System.currentTimeMillis(), "h2_2"));
        AbstractKVHandler h3 = new InnerMemHandler();
        h3.put(key3, new UdasObj(System.currentTimeMillis(), "h3_3"));
        AbstractKVHandler h4 = new InnerMemHandler();
        h4.put(key4, new UdasObj(System.currentTimeMillis(), "h4_4"));
        AbstractKVHandler[] hs = new AbstractKVHandler[4];
        hs[0] = h1;
        hs[1] = h2;
        hs[2] = h3;
        hs[3] = h4;
        mcache.setHandlers(hs);
    }

    @Test
    public void test1() {
        // 经过此方法后应该所有的KVHandler中都保留了这个值
        Serializable value = mcache.getKV("4");
        value = mcache.getKV("4");
        value = mcache.getKV("4");
        value = mcache.getKV("4");
        AbstractKVHandler hs[] = mcache.getHandlers();
        assertEquals(value, "h4_4");
        String key = UdasServiceImpl.createKey("4", "test");
        assertEquals(hs[0].get(key).getValue(), "h4_4");
        assertEquals(hs[1].get(key).getValue(), "h4_4");
        assertEquals(hs[2].get(key).getValue(), "h4_4");
    }

    @Test
    public void test2() {
        setUp1();
        // 经过此方法后应该所有的KVHandler中都保留了这个值
        mcache.put("A5", new UdasObj(System.currentTimeMillis(), "A5"));
        Serializable value = mcache.getKV("A5");
        value = mcache.getKV("A5");
        AbstractKVHandler hs[] = mcache.getHandlers();
        assertEquals(value, "A5");
        String key = UdasServiceImpl.createKey("A5", "test");
        assertEquals(hs[0].get(key).getValue(), "A5");
        assertEquals(hs[1].get(key).getValue(), "A5");
        assertEquals(hs[2].get(key).getValue(), "A5");
        assertEquals(hs[3].get(key).getValue(), "A5");
    }

    @Test
    public void test3() {
        setUp1();
        // 经过此方法后应该所有的KVHandler中都保留了这个值
        Serializable value = mcache.getKV("2");
        AbstractKVHandler hs[] = mcache.getHandlers();
        assertEquals(value, "h2_2");
        String key = UdasServiceImpl.createKey("2", "test");

        assertEquals(hs[0].get(key).getValue(), "h2_2");
        assertEquals(hs[1].get(key).getValue(), "h2_2");
        assertEquals(hs[2].get(key).getValue(), "h2_2");
        assertEquals(hs[3].get(key).getValue(), "h2_2");
    }

    /**
     * 测试单数据源的
     */
    @Test
    public void test4() {
        mcache = new UdasServiceImpl(CacheEnum.inner_mem, "test", "config.key");
        String configs[] = { "2" };
        mcache.updateConfig(configs);
        mcache.put("abc", new UdasObj(System.currentTimeMillis(), "abc_value"));
        assertEquals(1, mcache.getHandlers().length);
        assertEquals("abc_value", mcache.getKV("abc"));
        assertNull(mcache.getKV("nullkey"));
    }

    /**
     * 测试双数据源的
     */
    @Test
    public void test5() {
        mcache = new UdasServiceImpl(CacheEnum.inner_mem, "test", "config.key");
        AbstractKVHandler h1 = new InnerMemHandler();
        String key1 = UdasServiceImpl.createKey("1", "test");
        String key2 = UdasServiceImpl.createKey("2", "test");

        h1.put(key1, new UdasObj(System.currentTimeMillis(), "h1_1"));
        AbstractKVHandler h2 = new InnerMemHandler();
        h2.put(key2, new UdasObj(System.currentTimeMillis(), "h2_2"));
        AbstractKVHandler[] hs = new AbstractKVHandler[2];
        hs[0] = h1;
        hs[1] = h2;
        mcache.setHandlers(hs);
        assertEquals("h1_1", mcache.getKV("1"));
        assertEquals("h2_2", mcache.getKV("2"));
        assertEquals("h2_2", mcache.getKV("2"));
        assertEquals("h1_1", mcache.getKV("1"));
    }

    /**
     * 测试修改配置项后handler是否修改
     */
    @Test
    public void test6() {
        mcache = new UdasServiceImpl(CacheEnum.inner_mem, "test", "config.key");
        String configs[] = { "2", "3", "4" };
        mcache.updateConfig(configs);
        assertEquals(configs.length, mcache.getHandlers().length);
        AbstractKVHandler hs[] = mcache.getHandlers();
        // 判断两次相同配置后没有更新Handler
        mcache.updateConfig(configs);
        assertArrayEquals(hs, mcache.getHandlers());

        String configs2[] = { "3", "4", "5", "6" };
        mcache.updateConfig(configs2);
        assertEquals(configs2.length, mcache.getHandlers().length);
        hs = mcache.getHandlers();
        assertEquals("6", hs[3].getConfig()); // 最后一个Handler应该是6
    }

    /**
     * 测试DealHandler
     */
    @Test
    public void testDealHandler1() {
        mcache = new UdasServiceImpl(CacheEnum.inner_mem, "test", "config.key");
        // mcache.setRealDealHander(new TestDealHandlerImpl());
        String configs[] = { "2", "3", "4" };
        mcache.updateConfig(configs);

        AbstractKVHandler[] hs = mcache.getHandlers();
        assertEquals("valuetest#1", mcache.getKV("1"));
        assertEquals("valuetest#2", mcache.getKV("2"));
        String key = UdasServiceImpl.createKey("2", "test");

        assertEquals("valuetest#2", hs[0].get(key).getValue());
        assertEquals("valuetest#2", hs[1].get(key).getValue());
        assertEquals("valuetest#2", hs[2].get(key).getValue());
    }

    class TestDealHandlerImpl implements DealHandler {

        public Serializable get(String key) {
            return "value" + key;
        }

    }

}
