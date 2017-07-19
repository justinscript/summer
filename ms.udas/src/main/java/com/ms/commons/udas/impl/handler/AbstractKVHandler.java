/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ms.commons.udas.impl.UdasObj;

/**
 * @author zxc Apr 12, 2013 5:37:02 PM
 */
public abstract class AbstractKVHandler {

    private Logger  log     = LoggerFactory.getLogger(AbstractKVHandler.class);

    private String  config;                                                    // 配置项
    private boolean enabled = true;                                            // 设置可用状态
    private int     exceptionCount;                                            // 联系出现异常超过20次，就认为是有问题的

    public int getExceptionCount() {
        return exceptionCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public boolean put(String keyWithNameSpace, UdasObj value) {
        if (!enabled) {
            return false;
        }
        try {
            boolean flag = putKV(keyWithNameSpace, value);
            exceptionCount = 0;
            return flag;
        } catch (Exception e) {
            dealException(e);
            return false;
        }
    }

    public boolean put(String key, int expireTimeInSeconds, UdasObj value) {
        if (!enabled) {
            return false;
        }
        try {
            boolean flag = putKV(key, expireTimeInSeconds, value);
            exceptionCount = 0;
            return flag;
        } catch (Exception e) {
            dealException(e);
            return false;
        }
    }

    public UdasObj get(String keyWithNameSpace) {
        if (!enabled) {
            return null;
        }
        try {
            UdasObj obj = getKV(keyWithNameSpace);
            exceptionCount = 0;
            return obj;
        } catch (Exception e) {
            dealException(e);
            return null;
        }
    }

    public Map<String, UdasObj> getBulk(String... keyWithNameSpace) {
        if (!enabled) {
            return null;
        }
        try {
            Map<String, UdasObj> bulkKV = getBulkKV(keyWithNameSpace);
            exceptionCount = 0;
            return bulkKV;
        } catch (Exception e) {
            dealException(e);
            return null;
        }
    }

    public void del(String keyWithNameSpace) {
        if (!enabled) {
            return;
        }
        try {
            delKV(keyWithNameSpace);
            exceptionCount = 0;
            return;
        } catch (Exception e) {
            dealException(e);
            return;
        }
    }

    public void delAll() {
        if (!enabled) {
            return;
        }
        try {
            delAllKV();
            exceptionCount = 0;
            return;
        } catch (Exception e) {
            dealException(e);
            return;
        }
    }

    private void dealException(Exception e) {
        log.error("Find CacheHander Exception !", e);
        exceptionCount++;
        if (exceptionCount > 20) {
            log.error("有个UDASHander[" + config + "]失效了");
            enabled = false;
        }
    }

    protected abstract boolean putKV(String key, UdasObj value) throws Exception;

    protected abstract boolean putKV(String key, int expireTimeInSeconds, UdasObj value) throws Exception;

    protected abstract UdasObj getKV(String key) throws Exception;

    protected abstract Map<String, UdasObj> getBulkKV(String... keys) throws Exception;

    protected abstract boolean delKV(String key) throws Exception;

    protected abstract boolean delAllKV() throws Exception;

    public abstract boolean closeCache() throws Exception;
}
