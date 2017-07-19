/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import com.ms.commons.nisa.impl.MinaMessage;

/**
 * @author zxc Apr 12, 2013 6:53:10 PM
 */
public class ConfigHandler {

    private ConfigMap configMap = new ConfigMap();

    public void deal(MinaMessage minaMessage) {
        // 复制一份老数据
        ConfigMap newConfigMap = configMap.copy();
        // 把Server端传送过来数据进行覆盖
        HashMap<String, Serializable> paramMap = minaMessage.getParamMap();
        if (paramMap != null) {
            Iterator<String> ir = paramMap.keySet().iterator();
            while (ir.hasNext()) {
                String key = ir.next();
                Serializable value = paramMap.get(key);
                newConfigMap.putKV(key, value);
            }
        }
        configMap = newConfigMap;
    }
}
