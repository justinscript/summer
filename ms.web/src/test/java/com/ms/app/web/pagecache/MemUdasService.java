/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.pagecache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.interfaces.UdasService;

/**
 * @author zxc Apr 12, 2013 11:22:38 PM
 */
public class MemUdasService implements UdasService {

    private Map<String, MemElement> map = new HashMap<String, MemUdasService.MemElement>();

    public Serializable getKV(String key) {
        MemElement memElement = map.get(key);
        if (memElement == null) {
            return null;
        }
        if (System.currentTimeMillis() - memElement.getCreateTime() > memElement.getExpireTime() * 1000) {
            map.remove(key);
            return null;
        }
        return memElement.getUdasObj().getValue();
    }

    public Map<String, Serializable> getBulkKV(String... keys) {
        return null;
    }

    public void put(String key, UdasObj udasObj) {
        MemElement element = new MemElement(Integer.MAX_VALUE, udasObj);
        map.put(key, element);
    }

    public void put(String key, int expireTimeinSecondes, UdasObj udasObj) {
        MemElement element = new MemElement(expireTimeinSecondes, udasObj);
        map.put(key, element);
    }

    public void del(String key) {
        map.remove(key);
    }

    public boolean close() {
        return false;
    }

    private static class MemElement {

        private long    createTime;
        private int     expireTime;
        private UdasObj udasObj;

        public MemElement(int expireTime, UdasObj udasObj) {
            super();
            this.createTime = System.currentTimeMillis();
            this.expireTime = expireTime;
            this.udasObj = udasObj;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public UdasObj getUdasObj() {
            return udasObj;
        }

        public long getCreateTime() {
            return createTime;
        }
    }

    @Override
    public Serializable getKV(String key, Integer expireTimeSecond) {
        return null;
    }
}
