/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.reflect;

import java.lang.reflect.Method;

/**
 * @author zxc Apr 13, 2013 11:28:51 PM
 */
public class ObjectHolder {

    private Object object;

    public ObjectHolder(Object object) {
        this.object = object;
    }

    public MethodHolder method(String methodName, Class<?>... argsClass) {
        Method method;
        try {
            method = object.getClass().getDeclaredMethod(methodName, argsClass);
            method.setAccessible(true);
            return new DefaultMethodHolder(object, method);
        } catch (SecurityException e) {
            throw new RuntimeException("error occurred while invoke method [" + methodName + "]", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("error occurred while invoke method [" + methodName + "]", e);
        }
    }
}
