/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author zxc Apr 14, 2013 12:11:12 AM
 */
public class MockClassCalledUtil {

    static Logger                                        log                       = Logger.getLogger(MockClassCalledUtil.class);

    private static Map<Class<?>, MockClassCalledContext> mockClassCalledContextMap = new HashMap<Class<?>, MockClassCalledContext>();

    public static void clear() {
        mockClassCalledContextMap.clear();
    }

    public static Map<Class<?>, MockClassCalledContext> getMockClassCalledContextMap() {
        return mockClassCalledContextMap;
    }

    public static void addMockClassCalled(Object object, Method method, Object returnValue, Object[] params,
                                          Throwable throwable) {
        try {
            synchronized (mockClassCalledContextMap) {
                MockClassCalledContext mockClassCalledContext = mockClassCalledContextMap.get(method.getDeclaringClass());
                if (mockClassCalledContext == null) {
                    mockClassCalledContext = new MockClassCalledContext(method.getDeclaringClass());
                    mockClassCalledContextMap.put(method.getDeclaringClass(), mockClassCalledContext);
                }
                MockMethodCalledContext mockMethodCalledContext = new MockMethodCalledContext(
                                                                                              object,
                                                                                              method.getDeclaringClass(),
                                                                                              method, returnValue,
                                                                                              params, throwable);
                mockClassCalledContext.addMockMethodCalledContext(mockMethodCalledContext);
            }
        } catch (Throwable t) {
            // eat exception here
            log.error("Error occured in add mock class called record.", t);
        }
    }

    public static int getMockClassCalledTimes(Class<?> clazz) {
        synchronized (mockClassCalledContextMap) {
            MockClassCalledContext mockClassCalledContext = mockClassCalledContextMap.get(clazz);
            if (mockClassCalledContext == null) {
                return 0;
            }
            int totalCalledTimes = 0;
            for (List<MockMethodCalledContext> list : mockClassCalledContext.getMockMethodCalledContextMap().values()) {
                if (list != null) {
                    totalCalledTimes += list.size();
                }
            }
            return totalCalledTimes;
        }
    }

    public static int getMockMethodCalledTimes(Class<?> clazz, String method) {
        synchronized (mockClassCalledContextMap) {
            MockClassCalledContext mockClassCalledContext = mockClassCalledContextMap.get(clazz);
            if (mockClassCalledContext == null) {
                return 0;
            }
            List<MockMethodCalledContext> list = mockClassCalledContext.getMockMethodCalledContextList(method);
            if (list == null) {
                return 0;
            }
            return list.size();
        }
    }

    public static Class<?> getMatchMockClass(String clazz) {
        synchronized (mockClassCalledContextMap) {
            List<Class<?>> classes = new ArrayList<Class<?>>();
            boolean isSimpleName = (!clazz.contains("."));
            if (isSimpleName) {
                for (Class<?> c : mockClassCalledContextMap.keySet()) {
                    if (c.getSimpleName().equals(clazz)) {
                        classes.add(c);
                    }
                }
            } else {
                for (Class<?> c : mockClassCalledContextMap.keySet()) {
                    if (c.getName().equals(clazz)) {
                        classes.add(c);
                        break;
                    }
                }
            }

            if (classes.size() == 0) {
                return null;
            }
            if (classes.size() == 1) {
                return classes.get(0);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, classes.size()); i++) {
                sb.append((i > 0) ? ", " : "");
                sb.append(classes.get(i).getName());
            }
            if (classes.size() > 3) {
                sb.append(" ... ");
            }
            throw new RuntimeException("There a more than one class's  simple named called `" + clazz
                                       + "` deteced of `" + sb.toString() + "`.");
        }
    }

    public static MockMethodCalledContext getMockMethodCalledContextAt(Class<?> clazz, String method, int time) {
        synchronized (mockClassCalledContextMap) {
            MockClassCalledContext mockClassCalledContext = mockClassCalledContextMap.get(clazz);
            if (mockClassCalledContext == null) {
                throw new IndexOutOfBoundsException("Method `" + clazz.getName() + "." + method
                                                    + "` has never be called.");
            }
            List<MockMethodCalledContext> list = mockClassCalledContext.getMockMethodCalledContextList(method);
            if ((list == null) || (list.size() == 0)) {
                throw new IndexOutOfBoundsException("Method `" + clazz.getName() + "." + method
                                                    + "` has never be called.");
            }
            if (time >= list.size()) {
                throw new IndexOutOfBoundsException("Method `" + clazz.getName() + "." + method + "` only called "
                                                    + list.size() + " times, but you want get the " + time + " time.");
            }
            return list.get(time);
        }
    }
}
