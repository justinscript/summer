/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock;

import java.lang.reflect.Method;

/**
 * @author zxc Apr 14, 2013 12:10:54 AM
 */
public class MockMethodCalledContext {

    private Object    object;
    private Class<?>  clazz;
    private Method    method;
    private Object    returnValue;
    private Object[]  params;
    private Throwable throwable;

    public MockMethodCalledContext(Object object, Class<?> clazz, Method method, Object returnValue, Object[] params,
                                   Throwable throwable) {
        this.object = object;
        this.clazz = clazz;
        this.method = method;
        this.returnValue = returnValue;
        this.params = params;
        this.throwable = throwable;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
