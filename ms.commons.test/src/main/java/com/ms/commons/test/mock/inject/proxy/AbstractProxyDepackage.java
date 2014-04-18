/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author zxc Apr 14, 2013 12:13:56 AM
 */
public abstract class AbstractProxyDepackage implements ProxyDepackage {

    public Object depackage(Proxy proxy) {
        InvocationHandler h = Proxy.getInvocationHandler(proxy);
        if (!h.getClass().getName().equals(proxyName())) {
            throw new RuntimeException("Cannot call this depackage `" + proxyName() + "` for `"
                                       + h.getClass().getName() + "`.");
        }
        return internalDepackage(proxy, h, h.getClass());
    }

    abstract protected Object internalDepackage(Proxy proxy, InvocationHandler h, Class<?> clazz);
}
