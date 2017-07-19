/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author zxc Apr 12, 2013 6:53:00 PM
 */
public class ConfigMap {

    private HashMap<String, Serializable> paramMap = new HashMap<String, Serializable>();

    /**
     * 复制一份配置项
     * 
     * @return
     */
    public ConfigMap copy() {
        ConfigMap newConfigMap = new ConfigMap();
        if (paramMap != null) {
            Iterator<String> ir = paramMap.keySet().iterator();
            while (ir.hasNext()) {
                String key = ir.next();
                Serializable value = paramMap.get(key);
                if (value != null) {
                    newConfigMap.putKV(key, value);
                }
            }
        }
        System.out.println("Copy size =" + newConfigMap.size());
        return newConfigMap;
    }

    public int size() {
        return paramMap.size();
    }

    public boolean containsKey(String key) {
        return paramMap.containsKey(key);
    }

    public void putKV(String key, Serializable value) {
        paramMap.put(key, value);
    }

    public String getKV(String key, String defaultValue) {
        Serializable value = paramMap.get(key);
        return value == null ? defaultValue : (String) value;
    }

    public int getKV(String key, int defaultValue) {
        Serializable value = paramMap.get(key);
        return value == null ? defaultValue : (Integer) value;
    }

    public float getKV(String key, float defaultValue) {
        Serializable value = paramMap.get(key);
        return value == null ? defaultValue : (Float) value;
    }
}
