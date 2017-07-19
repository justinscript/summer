/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zxc Apr 13, 2013 11:29:55 PM
 */
public class DefaultMethodHolder implements MethodHolder {

    private Object object;

    private Method method;

    public DefaultMethodHolder(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public Object invoke(Object... args) {
        try {
            return method.invoke(object, args);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("error occurred while invoke method [" + method.getName() + "]", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("error occurred while invoke method [" + method.getName() + "]", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("error occurred while invoke method [" + method.getName() + "]", e);
        }
    }
}
