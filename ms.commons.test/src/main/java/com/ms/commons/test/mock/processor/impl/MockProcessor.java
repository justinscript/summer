/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.processor.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.ms.commons.test.common.ReflectUtil;
import com.ms.commons.test.mock.processor.AbstractCallerProcessor;

/**
 * @author zxc Apr 14, 2013 12:12:20 AM
 */
public class MockProcessor extends AbstractCallerProcessor implements InvocationHandler {

    public MockProcessor(Object object, Class<?> clazz) {
        super(object, clazz);
    }

    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return processCall(proxy, method, args, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createEnhancedObject() {
        ClassLoader classLoader = (object != null) ? object.getClass().getClassLoader() : clazz.getClassLoader();
        Class<?>[] interfacesOfClass = (object != null) ? ReflectUtil.getInterfaces(object.getClass()) : new Class<?>[] { clazz };
        return (T) Proxy.newProxyInstance(classLoader, interfacesOfClass, this);
    }
}
