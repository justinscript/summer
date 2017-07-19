/*
 * Copyright 2017-2025 msun.com All rigimport java.util.HashMap; import java.util.Map; import java.util.Set; import
 * org.apache.mina.util.ConcurrentHashSet; import com.ms.commons.comset.filter.info.LeafInfo; import
 * com.ms.commons.log.ExpandLogger; import com.ms.commons.log.LoggerFactoryWrapper; ered into with msun.com.
 */
package com.ms.commons.comset.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ms.commons.comset.filter.info.LeafInfo;
import com.ms.commons.concurrent.ConcurrentHashSet;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 5:24:12 PM
 */
public class ThreadContextCache {

    private static ExpandLogger                                   log          = LoggerFactoryWrapper.getLogger(ThreadContextCache.class);
    protected static final ThreadLocal<Map<RecordEnum, LeafInfo>> resources    = new ThreadLocal<Map<RecordEnum, LeafInfo>>() {

                                                                                   protected Map<RecordEnum, LeafInfo> initialValue() {
                                                                                       // 解决子线程和父线程并发产生的问题和ConcurrentModificationException
                                                                                       return new HashMap<RecordEnum, LeafInfo>();
                                                                                   }
                                                                               };

    private static final Set<ThreadLocalCallback>                 callbackList = new ConcurrentHashSet<ThreadLocalCallback>();

    /**
     * 清除全部
     */
    public static void clean() {
        if (log.isDebugEnabled()) {
            log.debug("Clear all elements from  thread [" + Thread.currentThread().getName() + "]");
            log.debug("Size : " + size());
        }
        currentMap().clear();
        for (ThreadLocalCallback callback : callbackList) {
            try {
                callback.cleanAll();
            } catch (Exception e) {
                log.error("Clean up ThreadLocal failed:" + e.getMessage(), e);
            }
        }
    }

    /**
     * 获得一个模块的线程上下文空间
     * 
     * @param module - 模块
     * @param autoCreate - 是否自动创建
     */
    protected static LeafInfo getModuleContext(RecordEnum module, boolean autoCreate) {
        Map<RecordEnum, LeafInfo> threadContext = currentMap();
        LeafInfo moduleContext = threadContext.get(module);
        if (moduleContext == null && autoCreate) {
            moduleContext = new LeafInfo();
            threadContext.put(module, moduleContext);
        }

        return moduleContext;
    }

    /**
     * 加入一个变量
     * 
     * @param module
     * @param key
     * @param value
     */
    public static void put(RecordEnum key, LeafInfo value) {
        Map<RecordEnum, LeafInfo> threadContext = currentMap();
        threadContext.put(key, value);
    }

    /**
     * 方法的描述.
     * 
     * @param key
     * @return
     */
    public static Object get(RecordEnum key) {
        return getModuleContext(key, false);
    }

    /**
     * 方法的描述.
     * 
     * @return
     */
    protected static Map<RecordEnum, LeafInfo> currentMap() {
        return resources.get();
    }

    /**
     * 方法的描述.
     * 
     * @return
     */
    public static int size() {
        Map<RecordEnum, LeafInfo> threadContext = currentMap();
        return (threadContext != null) ? threadContext.size() : 0;
    }

    /**
     * 方法的描述.
     */
    public static void destroy() {
        if (log.isDebugEnabled()) {
            log.debug("ThreadContextCache resource destroy. Thread = " + Thread.currentThread().getName(),
                      new Exception());
        }
        // 清除所有的cache
        currentMap().clear();

        resources.set(null);
    }

    public static void regist(ThreadLocalCallback callback) {
        callbackList.add(callback);
    }

    public static interface ThreadLocalCallback {

        public void cleanAll();
    }
}
