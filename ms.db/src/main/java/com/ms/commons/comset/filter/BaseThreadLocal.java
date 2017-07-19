/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.comset.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 5:23:52 PM
 */
public class BaseThreadLocal {

    private static ExpandLogger                             log       = LoggerFactoryWrapper.getLogger(ThreadContextCache.class);

    protected static final ThreadLocal<Map<String, Object>> resources = new ThreadLocal<Map<String, Object>>() {

                                                                          protected Map<String, Object> initialValue() {
                                                                              // 解决子线程和父线程并发产生的问题和ConcurrentModificationException
                                                                              return new ConcurrentHashMap<String, Object>();
                                                                          }
                                                                      };

    public static void clean() {
        if (log.isDebugEnabled()) {
            log.debug("Clear BaseThreadLocal  [" + Thread.currentThread().getName() + "]");
            log.debug("Size : " + size());
        }

        resources.get().clear();
    }

    private static int size() {
        return resources.get().size();
    }

    public static enum CacheType {

        DAILY_PROMOTION("dailypromotion");

        private String nameSpace;

        private CacheType(String nameSpace) {
            this.nameSpace = nameSpace;
        }

        public String getNameSpace() {
            return this.nameSpace;
        }

        public Object get(String key) {
            String cacheKey = buildKey(key);
            Map<String, Object> map = resources.get();
            Object result = map.get(cacheKey);

            if (log.isDebugEnabled()) {
                log.debug("BaseThreadLocal  Get[" + cacheKey + ":" + result + "]");
            }
            return result;
        }

        public void put(String key, Object value) {
            String cacheKey = buildKey(key);
            Map<String, Object> map = resources.get();
            map.put(cacheKey, value);

            if (log.isDebugEnabled()) {
                log.debug("BaseThreadLocal  Put[" + cacheKey + ":" + value + "]");
            }
        }

        private String buildKey(String key) {
            String cacheKey = this.nameSpace + "_" + key;
            return cacheKey;
        }
    }
}
