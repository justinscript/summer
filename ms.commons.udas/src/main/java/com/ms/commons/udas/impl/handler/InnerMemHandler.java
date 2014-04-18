/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.impl.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ms.commons.udas.impl.UdasObj;

/**
 * 本JVM内部的java内存空间
 * 
 * @author zxc Apr 12, 2013 5:35:15 PM
 */
public class InnerMemHandler extends AbstractKVHandler {

    private Map<String, UdasObj> map = new ConcurrentHashMap<String, UdasObj>();

    public boolean putKV(String key, UdasObj value) throws Exception {
        map.put(key, value);
        return true;
    }

    public UdasObj getKV(String key) throws Exception {
        return map.get(key);
    }

    protected Map<String, UdasObj> getBulkKV(String... keys) throws Exception {
        if (keys == null || keys.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, UdasObj> bulkMap = new HashMap<String, UdasObj>();
        for (String key : keys) {
            UdasObj kv = getKV(key);
            bulkMap.put(key, kv);
        }
        return bulkMap;
    }

    protected boolean putKV(String key, int expireTimeInSeconds, UdasObj value) throws Exception {
        throw new UnsupportedOperationException("JVM本地内存数据源，不支持过期数据时间设置。");
    }

    protected boolean delKV(String key) throws Exception {
        map.remove(key);
        return true;
    }

    public boolean closeCache() throws Exception {
        return true;
    }

    protected boolean delAllKV() throws Exception {
        map.clear();
        return true;
    }
}
