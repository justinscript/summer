/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.ms.commons.comset.filter.RecordEnum;
import com.ms.commons.comset.filter.ResourceTools;
import com.ms.commons.config.listener.ConfigListener;
import com.ms.commons.config.service.ConfigServiceLocator;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.interfaces.UdasService;
import com.ms.commons.udas.retry.RetryObject;
import com.ms.commons.udas.retry.RetryService;
import com.ms.commons.udas.retry.Retryable;

/**
 * @author zxc Apr 12, 2013 5:32:08 PM
 */
public class UdasServiceImpl implements UdasService, ConfigListener, Retryable {

    private static Logger       log         = LoggerFactoryWrapper.getLogger(UdasServiceImpl.class);
    private CacheEnum           cacheEnum;                                                          // cahce的类型
    private String              namespace;                                                          // 命名空间,例如:userCache,producrCache
    private String              configKey;                                                          // 去配置中心取数据的Key
    private String              configValues[];                                                     // 配置中心返回的value值
    // private DealHandler realDealHander; // 真正取数据的处理器（例如从数据库取)，如果不需要可以为null

    private AtomicLong          success     = new AtomicLong(0);                                    // 名字次数
    private AtomicLong          failure     = new AtomicLong(0);                                    // 失败次数
    private int                 curretIndex = -1;                                                   // 用于多数据源的轮询使用

    private AbstractKVHandler[] handlers;                                                           // 保存的cache处理器
    private RetryService        retryService;                                                       // 出错重试

    public UdasServiceImpl(String cacheEnumName, String namespace, String configKey) {
        this(CacheEnum.get(cacheEnumName), namespace, configKey);
    }

    public UdasServiceImpl(CacheEnum cacheEnum, String namespace, String configKey) {
        this.cacheEnum = cacheEnum;
        this.namespace = namespace;
        this.configKey = configKey;
    }

    public void init() {
        updateConfig();
        ConfigServiceLocator.getCongfigService().addConfigListener(this);
        retryService = new RetryService(this, namespace);
    }

    /**
     * 当JVM关闭或者Spring关闭时调用，目的处理一些后续事务。例如：BDB关闭时，把缓存的东西刷入内存。 目前在Spring的destroy-method=close()中调用
     * 
     * @return
     */
    public boolean close() {
        log.debug("Close UdasServiceImpl. namespace=" + namespace);
        try {
            retryService.close();
        } catch (Exception e) {
            log.error("Close retryService Fail . " + e.getMessage(), e);
        }
        try {
            if (handlers != null) {
                for (AbstractKVHandler han : handlers) {
                    han.closeCache();
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Close UdasServiceImpl Fail . " + e.getMessage(), e);
            return false;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public Serializable getKV(String key) {
        return getKV(key, null);
    }

    /**
     * @param key
     * @param expireTimeSecond 过期时间，单位秒。 如果为null就不检查,如果是0表示永不过期，也不检查
     * @return
     */
    public Serializable getKV(String key, Integer expireTimeSecond) {
        long s = System.currentTimeMillis();
        String keyWithNameSpace = createKey(key, namespace);
        UdasObj obj = checkUdasObjExpire(key, getFromKVHandler(keyWithNameSpace), expireTimeSecond);
        if (obj == null) {
            failure.incrementAndGet();
            // 之前设计时这个realDealHander是有值的。实际没有用。考虑去掉。
            // if (realDealHander != null) {
            // Serializable realObj = realDealHander.get(keyWithNameSpace);
            // if (realObj != null) {
            // obj = new UdasObj(System.currentTimeMillis(), realObj);
            // put(key, obj);
            // }
            // }
        } else {
            success.incrementAndGet();
        }
        long e = System.currentTimeMillis();
        ResourceTools.recordRunTime(RecordEnum.CACHE, namespace + ".get()", e - s);
        return obj == null ? null : obj.getValue();
    }

    /**
     * 检查UdasObj对象是否已经过期。理论上是不会的。实际上我们多次放入Memcache的东西没有过期。所以我们在此检查一下
     * 
     * @param obj
     * @param expireTimeSecond 单位S
     * @return
     */
    private UdasObj checkUdasObjExpire(String key, UdasObj obj, Integer expireTimeSecond) {
        if (obj == null) {
            return null;
        }
        if (expireTimeSecond == null || expireTimeSecond.intValue() == 0) {
            return obj;
        }
        long time = System.currentTimeMillis() - obj.getCreatTime();
        time = time / 1000;
        if (time >= expireTimeSecond) {
            log.error("---Udas_error---  key=" + key + "  create time=" + (new Date(obj.getCreatTime()))
                      + " expireTimeSecond=" + expireTimeSecond + "S, now=" + time + "S");
        }
        return time >= expireTimeSecond ? null : obj;
    }

    /**
     * 通过批量的key来获取批量的value
     * 
     * @param keys
     * @return
     */
    public Map<String, Serializable> getBulkKV(String... keys) {
        if (keys == null || keys.length == 0) {
            Collections.emptyMap();
        }
        long s = System.currentTimeMillis();
        int length = keys.length;
        String[] keysWithNameSpace = new String[length];
        for (int i = 0; i < length; i++) {
            keysWithNameSpace[i] = createKey(keys[i], namespace);
        }
        Map<String, UdasObj> map = getBulkFromKVHandler(keysWithNameSpace);
        if (map == null || map.isEmpty()) {
            failure.incrementAndGet();
            ResourceTools.recordRunTime(RecordEnum.CACHE, namespace + ".get()", System.currentTimeMillis() - s);
            return Collections.emptyMap();
        } else {
            success.incrementAndGet();
            Map<String, Serializable> serMap = new HashMap<String, Serializable>();
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String keyWithNamespace = (String) iterator.next();
                UdasObj udasObj = map.get(keyWithNamespace);
                String key = removeNamespace(keyWithNamespace, namespace);
                if (udasObj == null) {
                    serMap.put(key, null);
                } else {
                    serMap.put(key, udasObj.getValue());
                }
            }
            ResourceTools.recordRunTime(RecordEnum.CACHE, namespace + ".get()", System.currentTimeMillis() - s);
            return serMap;
        }
    }

    public Map<String, UdasObj> getAllValues(String key) {
        Map<String, UdasObj> map = new HashMap<String, UdasObj>();
        if (handlers != null) {
            int length = handlers.length;
            String keyWithNameSpace = createKey(key, namespace);
            for (int i = 0; i < length; i++) {
                AbstractKVHandler hand = handlers[i];
                String config = hand.getConfig();
                UdasObj udasObj = hand.get(keyWithNameSpace);
                map.put(config, udasObj);
            }
        }
        return map;
    }

    /**
     * 从KV数据源中查询数据
     */
    private UdasObj getFromKVHandler(String keyWithNameSpace) {
        curretIndex++;
        int mod = handlers.length;
        int arrayIndex = curretIndex % mod;
        curretIndex = arrayIndex;
        for (int i = 0; i < mod; i++) {
            UdasObj obj = handlers[(arrayIndex + i) % mod].get(keyWithNameSpace);
            if (obj != null) // 取到数据
            {
                if (i != 0) // 本数据源没有取到，取下一个数据源，这时需要把上一个数据源的数据补上
                {
                    for (int n = 0; n < mod; n++) {
                        if (n != (arrayIndex + i) % mod) // 自己那份就不设置了
                        {
                            handlers[n].put(keyWithNameSpace, obj);
                        }
                    }
                }
                return obj;
            }
        }
        return null;
    }

    /**
     * @param keysWithNameSpace
     * @return
     */
    private Map<String, UdasObj> getBulkFromKVHandler(String[] keysWithNameSpace) {
        curretIndex++;
        int mod = handlers.length;
        int arrayIndex = curretIndex % mod;
        curretIndex = arrayIndex;
        for (int i = 0; i < mod; i++) {
            Map<String, UdasObj> bulk = handlers[(arrayIndex + i) % mod].getBulk(keysWithNameSpace);
            if (bulk != null && !bulk.isEmpty()) // 取到数据
            {
                if (i != 0) // 本数据源没有取到，取下一个数据源，这时需要把上一个数据源的数据补上
                {
                    for (int n = 0; n < mod; n++) {
                        if (n != (arrayIndex + i) % mod) // 自己那份就不设置了
                        {
                            Iterator<String> iterator = bulk.keySet().iterator();
                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                handlers[n].put(key, bulk.get(key));
                            }
                        }
                    }
                }
                return bulk;
            }
        }
        return null;
    }

    public void del(String key) {
        String keyWithNameSpace = createKey(key, namespace);
        if (handlers != null) {
            for (AbstractKVHandler han : handlers) {
                han.del(keyWithNameSpace);
            }
        }
    }

    public void delAll() {
        if (handlers != null) {
            for (AbstractKVHandler han : handlers) {
                han.delAll();
            }
        }
    }

    public void put(String key, int expireTimeInSecondes, UdasObj value) {
        if (handlers == null) {
            return;
        }
        if (expireTimeInSecondes < 0) {
            throw new RuntimeException("过期时间不可以设置位负数");
        }
        long s = System.currentTimeMillis();
        boolean success = true;
        String keyWithNameSpace = createKey(key, getNamespace());
        for (AbstractKVHandler han : handlers) {
            if (!han.put(keyWithNameSpace, expireTimeInSecondes, value)) {
                success = false;
            }
        }
        // 如果需要重试，则被本地信息记录下来
        if (!success && needRetry()) {
            RetryObject retryObject = new RetryObject(value.getCreatTime(), value.getValue());
            retryService.addRetryTask(keyWithNameSpace, retryObject);
        }
        long e = System.currentTimeMillis();
        ResourceTools.recordRunTime(RecordEnum.CACHE, namespace + ".put(time)", e - s);
    }

    public static String createKey(String key, String namespace) {
        return namespace + "#" + key;
    }

    public static String removeNamespace(String keyWithNamespace, String namespace) {
        if (StringUtils.isEmpty(keyWithNamespace)) {
            return keyWithNamespace;
        }
        String prefix = namespace + "#";
        if (keyWithNamespace.startsWith(prefix)) {
            return keyWithNamespace.substring(prefix.length());
        }
        return keyWithNamespace;
    }

    public void put(String key, UdasObj value) {
        if (handlers == null) {
            return;
        }
        boolean success = true;
        String keyWithNameSpace = createKey(key, namespace);
        for (AbstractKVHandler han : handlers) {
            if (!han.put(keyWithNameSpace, value)) {
                success = false;
            }
        }
        long s = System.currentTimeMillis();
        if (!success && needRetry()) {
            RetryObject retryObject = new RetryObject(value.getCreatTime(), value.getValue());
            retryService.addRetryTask(keyWithNameSpace, retryObject);
        }
        long e = System.currentTimeMillis();
        ResourceTools.recordRunTime(RecordEnum.CACHE, namespace + ".put()", e - s);
    }

    /**
     * 是否需要重试，只有在数据源是RemoteMemcached时，才需要。
     */
    private boolean needRetry() {
        return CacheEnum.isRemoteMemcached(this.getCacheEnum());
    }

    /**
     * 更新配置项
     */
    public void updateConfig() {
        updateConfig(ConfigServiceLocator.getCongfigService().getKVStringArray(configKey));
    }

    /**
     * 更新配置项
     */
    public void updateConfig(String newConfigValues[]) {
        // 配置项相同，不修改
        if (newConfigValues == null || isSameStringArray(configValues, newConfigValues)) {
            return;
        }
        // 配置项不同，修改KVHandler列表
        AbstractKVHandler[] newHandlers = new AbstractKVHandler[newConfigValues.length];
        for (int i = 0; i < newConfigValues.length; i++) {
            String config = newConfigValues[i];
            // 首先从原有Handler找一样配置的handler，没有找到则创建
            AbstractKVHandler newTmpHandler = findHandlerByConfig(config, handlers);
            if (newTmpHandler == null) {
                newTmpHandler = createKVHandler(config, cacheEnum);
            }

            newHandlers[i] = newTmpHandler;
        }
        handlers = newHandlers;
        configValues = newConfigValues;
    }

    /**
     * 根据配置项目，从<code>AbstractKVHandler</code>查找具有相同配置第一个的Handler
     * 
     * @param config 查找的配置
     * @param targetHandlers 查找的对象
     * @return 若不存在则返回<code>null</code>
     */
    private AbstractKVHandler findHandlerByConfig(String config, AbstractKVHandler[] targetHandlers) {
        if (targetHandlers == null) {
            return null;
        }
        for (AbstractKVHandler tmp : targetHandlers) {
            // 如果配置相同就认为两个Handler相同
            boolean isSameHandler = config.equals(tmp.getConfig());
            if (isSameHandler) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * 判断两个数组是否相等
     * 
     * @param a
     * @param b
     * @return
     */
    private boolean isSameStringArray(String[] a, String b[]) {
        if (a == null && b == null) {
            return true;
        }
        if ((a == null && b != null) || (a != null && b == null)) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!a[i].equals(b[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据CacheEnum来创建KVHandler
     */
    private AbstractKVHandler createKVHandler(String config, CacheEnum cacheEnum) {
        return cacheEnum.createHandler(namespace, config);
    }

    public AbstractKVHandler[] getHandlers() {
        return handlers;
    }

    public void setHandlers(AbstractKVHandler[] handlers) {
        this.handlers = handlers;
    }

    // public void setRealDealHander(DealHandler realDealHander) {
    // this.realDealHander = realDealHander;
    // }

    public CacheEnum getCacheEnum() {
        return cacheEnum;
    }

    public String getConfigKey() {
        return configKey;
    }

    // public DealHandler getRealDealHander() {
    // return realDealHander;
    // }

    public AtomicLong getSuccess() {
        return success;
    }

    public AtomicLong getFailure() {
        return failure;
    }

    public int getCurretIndex() {
        return curretIndex;
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append("cahce的类型=").append(cacheEnum.name());
        sb.append(" 命名空间=").append(namespace);
        sb.append(" configKey=").append(configKey);
        return sb.toString();
    }

    public String toString() {
        return getName();
    }

    public void retry(String key, RetryObject retryObject) {
        if (retryObject == null) {
            return;
        }
        UdasObj value = new UdasObj(retryObject.getCreatTime(), retryObject.getValue());
        boolean failed = false;
        for (AbstractKVHandler han : handlers) {
            if (!han.put(key, value)) failed = true;
        }
        // 又失败了
        if (failed) {
            retryObject.incrRetryCount();
            retryService.addRetryTask(key, retryObject);
        }
    }

    public boolean needRetry(String key, RetryObject retryObject) {
        UdasObj udasObj = (UdasObj) getKV(key);
        // 当前UDAS没有值，或者值较旧时需要重试。
        if (udasObj == null || udasObj.getCreatTime() < retryObject.getCreatTime()) {
            return true;
        }
        return false;
    }
}
