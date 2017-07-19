/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas.memcached;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ms.commons.udas.impl.commons.MemcachedKeyStore;
import com.ms.commons.udas.memcached.client.MockMemcachedClient;

/**
 * @author zxc Apr 12, 2013 6:39:17 PM
 */
public class MemcachedKeyStoreTest {

    MemcachedKeyStore   keyStore;
    MockMemcachedClient memcachedClient;

    @Before
    public void before() throws IOException {
        boolean isFake = true;// 不用写到远端Memcached中，只写到本地JVM内存
        memcachedClient = new MockMemcachedClient("192.168.1.182:11211", isFake);
        keyStore = new MemcachedKeyStore("html", memcachedClient);
        System.setProperty("memcached.key.flush.time", "1");
    }

    @Test
    public void testAdd() throws InterruptedException {
        // add
        for (int i = 1; i <= 200; i++) {
            keyStore.append("value" + i);
        }
        Thread.sleep(1000);
        System.out.println(keyStore.getAllStoredKey().size());
        assertTrue(keyStore.getAllStoredKey().size() == 200);
    }

    @Test
    public void testDel() throws InterruptedException {
        for (int i = 1; i <= 500; i++) {
            keyStore.append("value" + i);
        }
        System.out.println(keyStore.getAllStoredKey().size());
        Thread.sleep(1100);
        assertTrue(keyStore.getAllStoredKey().size() == 500);
        // then delete
        for (int i = 1; i <= 200; i++) {
            keyStore.del("value" + i);
        }
        Thread.sleep(5000);
        System.out.println(keyStore.getAllStoredKey().size());
        assertTrue(keyStore.getAllStoredKey().size() == 300);

    }

    @After
    public void clear() {
        for (int i = 1; i <= 10000; i++) {
            keyStore.del("value" + i);
        }
    }
}
