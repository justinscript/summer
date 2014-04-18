/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.ms.commons.test.mock.inject.proxy.impl.AliMockProcessorProxyDepackage;
import com.ms.commons.test.mock.inject.proxy.impl.SpringDynamicAopProxyDepackage;

/**
 * @author zxc Apr 14, 2013 12:13:41 AM
 */
public class ProxyDepackageUtil {

    protected static Map<String, ProxyDepackage> map = new HashMap<String, ProxyDepackage>();

    static {
        register(new AliMockProcessorProxyDepackage());
        register(new SpringDynamicAopProxyDepackage());
    }

    public static void register(ProxyDepackage depackage) {
        synchronized (map) {
            map.put(depackage.proxyName(), depackage);
        }
    }

    public static Object getDepackageObject(Object object) {
        if (object == null) {
            return null;
        }
        if (!(object instanceof Proxy)) {
            return object;
        }
        InvocationHandler h = Proxy.getInvocationHandler((Proxy) object);
        ProxyDepackage depackage;
        synchronized (map) {
            depackage = map.get(h.getClass().getName());
        }
        if (depackage == null) {
            throw new RuntimeException("Cannot proxy depackager for `" + h.getClass().getName() + "`.");
        }
        return depackage.depackage((Proxy) object);
    }

    public static Object getLastDepackageObject(Object object) {
        Object o = getDepackageObject(object);
        while ((o != null) && (o instanceof Proxy)) {
            o = getDepackageObject(o);
        }
        return o;
    }
}
