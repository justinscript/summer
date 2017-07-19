/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl;

import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.impl.handler.BdbHandler;
import com.ms.commons.udas.impl.handler.InnerMemHandler;
import com.ms.commons.udas.impl.handler.MemCachedHandler;

/**
 * @author zxc Apr 12, 2013 5:33:24 PM
 */
public enum CacheEnum {
    remote_memcached, local_bdb, inner_mem;

    public boolean isRemoteMemcached() {
        return this == remote_memcached;
    }

    public boolean isLocalBDB() {
        return this == local_bdb;
    }

    public boolean isInnerMem() {
        return this == inner_mem;
    }

    public static boolean isRemoteMemcached(CacheEnum cacheEnum) {
        return cacheEnum != null && cacheEnum.isRemoteMemcached();
    }

    public static boolean isLocalBDB(CacheEnum cacheEnum) {
        return cacheEnum != null && cacheEnum.isLocalBDB();
    }

    public static boolean isInnerMem(CacheEnum cacheEnum) {
        return cacheEnum != null && cacheEnum.isInnerMem();
    }

    public static CacheEnum get(String name) {
        for (CacheEnum value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据CacheEnum和Config来创建KVHandler
     */
    public AbstractKVHandler createHandler(String nameSpace, String config) {
        AbstractKVHandler handler = null;
        switch (this) {
            case inner_mem:
                handler = new InnerMemHandler();
                handler.setConfig(config);
                break;

            case local_bdb:
                handler = new BdbHandler(config);
                break;

            case remote_memcached:
                handler = new MemCachedHandler(nameSpace, config);
                break;
        }
        return handler;
    }
}
