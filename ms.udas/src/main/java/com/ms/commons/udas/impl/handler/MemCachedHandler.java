/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.udas.impl.handler;

import static com.ms.commons.udas.impl.commons.UdasContants.never_expire;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.impl.commons.MemcachedKeyStore;

/**
 * @author zxc Apr 12, 2013 5:34:30 PM
 */
public class MemCachedHandler extends AbstractKVHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractKVHandler.class);
    private MemcachedClient     memcachedClient;
    private Exception           initException;
    private MemcachedKeyStore   keyStore;

    public MemCachedHandler(String nameSpace, String server) {
        setConfig(server);
        try {
            memcachedClient = new MemcachedClient(AddrUtil.getAddresses(server));
        } catch (IOException e) {
            logger.error("MemcachedClient 初始化失败了!!", e);
            initException = e;
            return;
        }
        // 用来保存所有的UDAS key
        keyStore = new MemcachedKeyStore(nameSpace, memcachedClient);
    }

    public boolean putKV(String key, UdasObj value) throws Exception {
        // 不管Cache中值是否存在，都设置新值
        boolean success = putKV(key, never_expire, value);
        // if (success) {
        // keyStore.append(key);// 对于没有设置缓存时间的可以将也存下来
        // }
        return success;
    }

    protected boolean putKV(String key, int expireTimeInSeconds, UdasObj value) throws Exception {
        if (initException != null) {
            throw initException;
        }
        Future<Boolean> result = memcachedClient.set(key, expireTimeInSeconds, value);
        try {
            // 2s，没有返回认为错误
            boolean success = result.get(2, TimeUnit.SECONDS);
            if (success) {
                keyStore.append(key);
            }
            return success;
        } catch (TimeoutException e) {
            logger.error("插入一条数据超时", e);
            return false;
        }
    }

    public UdasObj getKV(String key) throws Exception {
        if (initException != null) {
            throw initException;
        }
        return (UdasObj) memcachedClient.get(key);
    }

    public Map<String, UdasObj> getBulkKV(String... keys) throws Exception {
        if (initException != null) {
            throw initException;
        }
        Map<String, Object> bulk = memcachedClient.getBulk(keys);
        if (bulk.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, UdasObj> udasMap = new HashMap<String, UdasObj>();
        Iterator<String> iterator = bulk.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            UdasObj udasObj = (UdasObj) bulk.get(key);
            udasMap.put(key, udasObj);
        }
        return udasMap;
    }

    protected boolean delKV(String key) throws Exception {
        if (initException != null) {
            throw initException;
        }
        boolean flag = false;
        Future<Boolean> result = memcachedClient.delete(key);
        try {
            // 2S，没有返回认为错误
            flag = result.get(2, TimeUnit.SECONDS);
            if (flag) keyStore.del(key);// 保存的Key也要删除
        } catch (TimeoutException e) {
            logger.error("插入一条数据超时", e);
        }
        return flag;
    }

    protected boolean delAllKV() throws Exception {
        if (initException != null) {
            throw initException;
        }
        boolean flag = false;
        // 同步key
        keyStore.flushKey();
        Set<String> allStoredKey = keyStore.getAllStoredKey();
        for (String key : allStoredKey) {
            Future<Boolean> result = memcachedClient.delete(key);
            try {
                // 2S，没有返回认为错误
                flag = result.get(2, TimeUnit.SECONDS);
                if (flag) {
                    keyStore.del(key);// 保存的Key也要删除
                }
            } catch (TimeoutException e) {
                logger.error("删除一条数据超时", e);
            }
        }
        // 同步key
        keyStore.flushKey();
        return true;
    }

    public boolean closeCache() throws Exception {
        return true;
    }

    public MemcachedKeyStore getKeyStore() {
        return keyStore;
    }
}
