/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject.proxy.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.ms.commons.test.mock.inject.proxy.AbstractProxyDepackage;

/**
 * @author zxc Apr 14, 2013 12:14:11 AM
 */
public class AliMockProcessorProxyDepackage extends AbstractProxyDepackage {

    public String proxyName() {
        return "com.ms.commons.test.mock.processor.impl.MockProcessor";
    }

    public Object internalDepackage(Proxy proxy, InvocationHandler h, Class<?> clazz) {
        try {
            Field objectField = clazz.getSuperclass().getDeclaredField("object");
            objectField.setAccessible(true);
            return objectField.get(h);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
