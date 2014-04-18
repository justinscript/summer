/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.reflect;

import java.lang.reflect.Method;

/**
 * @author zxc Apr 13, 2013 11:30:11 PM
 */
public class ClassHolder {

    private Class<?> clazz;

    /**
     * @param clazz
     */
    public ClassHolder(Class<?> clazz) {
        this.clazz = clazz;
    }

    public MethodHolder method(String methodName, Class<?>... argsClass) {
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, argsClass);
            method.setAccessible(true);
            return new DefaultMethodHolder(null, method);
        } catch (SecurityException e) {
            throw new RuntimeException("error occurred while invoke method [" + methodName + "]", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("error occurred while invoke method [" + methodName + "]", e);
        }
    }
}
