/*
 * Copyright 2011-2016 ZXC.com All riimport java.lang.reflect.Method; import java.util.Map; import
 * java.util.concurrent.ConcurrentHashMap; import com.ms.commons.test.annotation.Prepare; import
 * com.ms.commons.test.annotation.TestCaseInfo; ordance with the terms of the license agreement you entered into with
 * ZXC.com.
 */
package com.ms.commons.test.context;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ms.commons.test.annotation.Prepare;
import com.ms.commons.test.annotation.TestCaseInfo;

/**
 * @author zxc Apr 13, 2013 11:39:44 PM
 */
public class TestCaseRuntimeInfo {

    private static TestCaseRuntimeInfo testCaseRuntimeInfo = new TestCaseRuntimeInfo();

    private Class<?>                   clazz;
    private TestCaseInfo               testCaseInfo;
    private Method                     method;
    private Prepare                    prepare;
    private Map<String, Object>        context             = new ConcurrentHashMap<String, Object>();

    public static TestCaseRuntimeInfo current() {
        return testCaseRuntimeInfo;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public TestCaseInfo getTestCaseInfo() {
        return testCaseInfo;
    }

    public void setTestCaseInfo(TestCaseInfo testCaseInfo) {
        this.testCaseInfo = testCaseInfo;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Prepare getPrepare() {
        return prepare;
    }

    public void setPrepare(Prepare prepare) {
        this.prepare = prepare;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
