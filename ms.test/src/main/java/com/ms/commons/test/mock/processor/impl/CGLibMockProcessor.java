/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock.processor.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.log4j.Logger;

import com.ms.commons.test.mock.processor.AbstractCallerProcessor;

/**
 * <pre>
 * Title: CGLibMockProcessor.java
 * Description:
 * Company: Msun.com
 * </pre>
 * 
 * @author zxc Apr 14, 2013 12:12:28 AM
 */
public class CGLibMockProcessor extends AbstractCallerProcessor implements MethodInterceptor {

    private static final Logger log      = Logger.getLogger(CGLibMockProcessor.class);

    private Enhancer            enhancer = new Enhancer();

    public CGLibMockProcessor(Object object, Class<?> clazz) {
        super(object, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createEnhancedObject() {
        try {
            enhancer.setSuperclass(tryGetObjectClass());
            enhancer.setCallback(this);
            return (T) enhancer.create();
        } catch (Exception e) {
            if ((clazz.getConstructors() == null) || (clazz.getConstructors().length == 0)) {
                throw new RuntimeException(e);
                // return (T) object;
            }

            try {
                Constructor<?> constructor = clazz.getConstructors()[0];
                Class<?>[] argumentTypes = constructor.getParameterTypes();
                Object[] arguments = new Object[argumentTypes.length];
                return (T) enhancer.create(argumentTypes, arguments);
            } catch (Exception e1) {
                log.error("Create cglib proxy for class`" + clazz.getName() + "` failed.", e1);
                return (T) object;
            }
        }
    }

    public Object intercept(Object object, Method method, Object[] params, MethodProxy proxy) throws Throwable {
        return processCall(object, method, params, proxy);
    }

    protected Object callMethod(Method method, Object[] args, Object additionParam) throws Throwable {
        return ((MethodProxy) additionParam).invoke(object, args);
    }

    private Class<?> tryGetObjectClass() {
        Class<?> clz = clazz;
        try {
            clz = object.getClass();
        } catch (Exception e) {
            log.error("Try get object's class failed.", e);
        }
        return clz;
    }
}
