/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.velocity.runtime.RuntimeServices;

/**
 * 用于清除Velocity中vm的缓存
 * 
 * @author zxc Apr 12, 2013 10:41:58 PM
 */
public class MsunResourceManagerImpl extends org.apache.velocity.runtime.resource.ResourceManagerImpl {

    public static MsunResourceManagerImpl instance;

    public synchronized void initialize(final RuntimeServices rsvc) {
        try {
            super.initialize(rsvc);
            System.out.println("zxc ! Velocity initialize globalCache =" + globalCache);
            instance = this;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        @SuppressWarnings("rawtypes")
        Iterator ir = globalCache.enumerateKeys();
        List<Object> keyList = new ArrayList<Object>();
        while (ir.hasNext()) {
            Object key = ir.next();
            if (key != null) {
                keyList.add(key);
            }
        }
        for (Object key : keyList) {
            System.out.println("Clear velocity cache . key=" + key);
            globalCache.remove(key);
        }
    }
}
