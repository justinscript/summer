/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock;

import java.util.Arrays;

import com.ms.commons.test.assertion.exception.AssertException;
import com.ms.commons.test.mock.exception.MockNeverCalledException;
import com.ms.commons.test.mock.impl.handle.MockHandle;
import com.ms.commons.test.mock.inject.MockFilter;
import com.ms.commons.test.mock.inject.MockInjectUtil;
import com.ms.commons.test.mock.inject.register.MockPairCreater;
import com.ms.commons.test.mock.inject.register.MockRegister;
import com.ms.commons.test.mock.processor.AliMockType;

/**
 * @author zxc Apr 14, 2013 12:11:42 AM
 */
public class AliMock {

    public static void clearMock() {
        clearMockRegister();
        clearMockClassCalled();
    }

    public static void clearMockRegister() {
        MockRegister.clear();
    }

    public static void clearMockClassCalled() {
        MockClassCalledUtil.clear();
    }

    public static void setMockFilter(MockFilter mockFilter) {
        MockInjectUtil.setMockFilter(mockFilter);
    }

    public static void mockObject(Object object) {
        MockInjectUtil.mockObject(object);
    }

    public static void mockClass(Class<?> clazz) {
        mockObject(clazz);
    }

    public static <T> T createMockObject(Class<T> clazz) {
        return MockInjectUtil.createMockObject(clazz);
    }

    public static Object createMockObject(Object object) {
        return MockInjectUtil.createMockObject(object, AliMockType.AUTO);
    }

    public static Object createProxyMockObject(Object object) {
        return MockInjectUtil.createMockObject(object, AliMockType.PROXY);
    }

    public static Object createCgLibMockObject(Object object) {
        return MockInjectUtil.createMockObject(object, AliMockType.CGLIB);
    }

    public static <T> T createMockObject(Class<T> interfaceClazz, Object value) {
        return MockInjectUtil.createMockObject(interfaceClazz, value, AliMockType.AUTO);
    }

    public static <T> T createProxyMockObject(Class<T> interfaceClazz, Object value) {
        return MockInjectUtil.createMockObject(interfaceClazz, value, AliMockType.PROXY);
    }

    public static <T> T createCgLibMockObject(Class<T> interfaceClazz, Object value) {
        return MockInjectUtil.createMockObject(interfaceClazz, value, AliMockType.CGLIB);
    }

    // ////////////////////////////////////////////////////////////////////

    public static void addReturnValue(String clazz, String method) {
        addReturnValue(clazz, method, null);
    }

    public static void addReturnNull(String clazz, String method) {
        addReturnValue(clazz, method, null);
    }

    public static void addReturnValue(String clazz, String method, Object value) {
        MockPair pair = MockPairCreater.createAddReturn(value);
        MockRegister.register(clazz, method, pair);
    }

    public static void setReturnValueAt(String clazz, String method, int time) {
        setReturnValueAt(clazz, method, time, null);
    }

    public static void setReturnNullAt(String clazz, String method, int time) {
        setReturnValueAt(clazz, method, time, null);
    }

    public static void setReturnValueAt(String clazz, String method, int time, Object value) {
        MockPair pair = MockPairCreater.createAtTime(value, time);
        MockRegister.register(clazz, method, pair);
    }

    public static void setReturnValueAtAllTimes(String clazz, String method) {
        setReturnValueAtAllTimes(clazz, method, null);
    }

    public static void setReturnNullAtAllTimes(String clazz, String method) {
        setReturnValueAtAllTimes(clazz, method, null);
    }

    public static void setReturnValueAtAllTimes(String clazz, String method, Object value) {
        MockPair pair = MockPairCreater.createAllTimes(value);
        MockRegister.register(clazz, method, pair);
    }

    /**
     * @see AliMock#setReturnValueOn(String, String, Object[])
     */
    public static void setReturnValueOn(String clazz, String mehtod, Object param) {
        setReturnValueOn(clazz, mehtod, new Object[] { param });
    }

    /**
     * @see AliMock#setReturnValueOn(String, String, Object[])
     */
    public static void setReturnNullOn(String clazz, String mehtod, Object param) {
        setReturnNullOn(clazz, mehtod, new Object[] { param });
    }

    /**
     * @param clazz
     * @param mehtod
     * @param params
     */
    public static void setReturnValueOn(String clazz, String mehtod, Object[] params) {
        setReturnValueOn(clazz, mehtod, params, null);
    }

    /**
     * @param clazz
     * @param mehtod
     * @param params
     */
    public static void setReturnNullOn(String clazz, String mehtod, Object[] params) {
        setReturnValueOn(clazz, mehtod, params, null);
    }

    /**
     * @see AliMock#setReturnValueOn(String, String, Object[], Object)
     * @param clazz
     * @param mehtod
     * @param param
     * @param value
     */
    public static void setReturnValueOn(String clazz, String mehtod, Object param, Object value) {
        setReturnValueOn(clazz, mehtod, new Object[] { param }, value);
    }

    /**
     * @param clazz
     * @param method
     * @param params
     * @param value
     */
    public static void setReturnValueOn(String clazz, String method, Object[] params, Object value) {
        MockPair pair = MockPairCreater.createOn(value, params);
        MockRegister.register(clazz, method, pair);
    }

    /**
     * @param clazz
     * @param method
     * @param handle
     */
    public static void setReturnValueHandle(String clazz, String method, MockHandle handle) {
        MockPair pair = MockPairCreater.createHandle(null, handle);
        MockRegister.register(clazz, method, pair);
    }

    // ////////////////////////////////////////////////////////////////////

    /**
     * @param clazz
     * @param method
     * @param calledTime
     * @return
     */
    public static Object[] getArgument(String clazz, String method, int calledTime) {
        Class<?> contextClazz = MockClassCalledUtil.getMatchMockClass(clazz);
        if (contextClazz == null) {
            throw new MockNeverCalledException("This mock `" + clazz + "#" + method + "` never been called.");
        }
        MockMethodCalledContext methodContext = MockClassCalledUtil.getMockMethodCalledContextAt(contextClazz, method,
                                                                                                 calledTime);
        return methodContext.getParams();
    }

    public static Object getArgument(String clazz, String method, int calledTime, int argumentIndex) {
        Class<?> contextClazz = MockClassCalledUtil.getMatchMockClass(clazz);
        if (contextClazz == null) {
            throw new MockNeverCalledException("This mock `" + clazz + "#" + method + "` never been called.");
        }
        MockMethodCalledContext methodContext = MockClassCalledUtil.getMockMethodCalledContextAt(contextClazz, method,
                                                                                                 calledTime);
        return methodContext.getParams()[argumentIndex];
    }

    public static Object getReturnValue(String clazz, String method, int calledTime) {
        Class<?> contextClazz = MockClassCalledUtil.getMatchMockClass(clazz);
        if (contextClazz == null) {
            throw new MockNeverCalledException("This mock `" + clazz + "#" + method + "` never been called.");
        }
        MockMethodCalledContext methodContext = MockClassCalledUtil.getMockMethodCalledContextAt(contextClazz, method,
                                                                                                 calledTime);
        return methodContext.getReturnValue();
    }

    public static int getCallCount(String clazz, String method) {
        Class<?> contextClazz = MockClassCalledUtil.getMatchMockClass(clazz);
        if (contextClazz == null) {
            return 0;
        }
        return MockClassCalledUtil.getMockMethodCalledTimes(contextClazz, method);
    }

    // ////////////////////////////////////////////////////////////////////

    public static void assertCalled(String clazz, String method) {
        if (getCallCount(clazz, method) <= 0) {
            throw new AssertException("Method `" + clazz + "#" + method + "` never been called.");
        }
    }

    public static void assertNotCalled(String clazz, String method) {
        if (getCallCount(clazz, method) > 0) {
            throw new AssertException("Method `" + clazz + "#" + method + "` had been called.");
        }
    }

    public static void assertArgumentPassed(String clazz, String method, int calledTime, Object... params) {
        Object[] calledParams = getArgument(clazz, method, calledTime);
        if (!Arrays.equals(calledParams, params)) {
            throw new AssertException("Aspect `" + arrayAsString(params) + "` but called with `"
                                      + arrayAsString(calledParams) + "`.");
        }
    }

    // ////////////////////////////////////////////////////////////////////

    static String arrayAsString(Object[] a) {
        if (a == null) {
            return String.valueOf(null);
        } else {
            return Arrays.asList(a).toString();
        }
    }
}
