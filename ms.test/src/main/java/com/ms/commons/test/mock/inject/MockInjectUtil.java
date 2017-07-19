/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ms.commons.test.common.ReflectUtil;
import com.ms.commons.test.mock.processor.AbstractCallerProcessor;
import com.ms.commons.test.mock.processor.AliMockType;

/**
 * @author zxc Apr 14, 2013 12:13:03 AM
 */
public class MockInjectUtil {

    static final Logger                          log     = Logger.getLogger(MockInjectUtil.class);

    private static MockFilter                    mockFilter;
    private static final Map<ObjectKey, Boolean> mockMap = new HashMap<ObjectKey, Boolean>();

    public static void setMockFilter(MockFilter filter) {
        synchronized (mockMap) {
            mockFilter = filter;
        }
    }

    public static void mocked(Object object) {
        synchronized (mockMap) {
            if (mockMap.get((new ObjectKey(object))) != null) {
                throw new RuntimeException("Object `" + object + "` is already mocked.");
            }
            mockMap.put(new ObjectKey(object), Boolean.TRUE);
        }
    }

    public static boolean isMocked(Object object) {
        synchronized (mockMap) {
            return (mockMap.get(new ObjectKey(object)) != null);
        }
    }

    public static boolean isMockableObject(Class<?> clazz) {
        if (mockFilter != null) {
            if (!(mockFilter.canMock(clazz))) {
                return false;
            }
        }
        return true;
    }

    public static void mockObject(Object object) {
        if (object == null) {
            return;
        }

        boolean isClazz = (object.getClass() == Class.class);
        Class<?> clazz = isClazz ? (Class<?>) object : object.getClass();
        if (!isMockableObject(clazz)) {
            return;
        }

        if (MockInjectUtil.isMocked(object)) {
            return;
        }

        MockInjectUtil.mocked(object);

        Field[] fields = ReflectUtil.getDeclaredFields(clazz);

        if (isClazz) {
            // ����class����ʱֻ�õ�static�Ķ�Ӧ
            fields = ReflectUtil.getStaticFields(fields);
        }

        for (Field field : fields) {
            synchronized (mockMap) {
                if (!isMockableObject(field.getType())) {
                    continue;
                }
            }

            if (Modifier.isFinal(field.getModifiers())) {
                log.warn("Field `" + clazz.getName() + "#" + field.getName() + "` cannot be mocked for it is final.");
                continue;
            }

            // if is interface and default mockable
            field.setAccessible(true);

            Object value = null;
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    value = field.get(null);
                } else {
                    value = field.get(object);
                }
            } catch (IllegalArgumentException e) {
                log.error("Mock failed for exception: IllegalArgumentException", e);
                throw new RuntimeException("Mock faied.", e);
            } catch (IllegalAccessException e) {
                log.error("Mock failed for exception: IllegalAccessException", e);
                throw new RuntimeException("Mock faied.", e);
            }

            // mock cycle
            if (value instanceof Proxy) {
                Object targetValue = getTargetObject((Proxy) value);
                if (targetValue != null) {
                    mockObject(targetValue);
                }
            } else {
                mockObject(value);
            }

            if (!isObjectMocked(value)) {
                Object mockedObject = null;
                try {
                    mockedObject = AbstractCallerProcessor.createCallerObject(value, field.getType(), AliMockType.AUTO);
                } catch (Throwable e) {
                    log.error("Mock failed for throwable", e);
                    throw new RuntimeException("Mock faied.", e);
                }
                try {
                    if (Modifier.isStatic(field.getModifiers())) {
                        field.set(null, mockedObject);
                    } else {
                        field.set(object, mockedObject);
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Mock failed for exception: IllegalArgumentException", e);
                    throw new RuntimeException("Mock faied.", e);
                } catch (IllegalAccessException e) {
                    log.error("Mock failed for exception: IllegalAccessException", e);
                    throw new RuntimeException("Mock faied.", e);
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T createMockObject(Class<T> clazz) {
        if (clazz.isInterface()) {
            return createMockObject(clazz, null, AliMockType.AUTO);
        } else {
            try {
                return (T) createMockObject(clazz.newInstance(), AliMockType.AUTO);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create mock object.", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create mock object.", e);
            }
        }
    }

    public static Object createMockObject(Object object, AliMockType mockType) {
        if (object == null) {
            throw new RuntimeException("Cannot create null mock object.");
        }
        return createMockObject(null, object, mockType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createMockObject(Class<T> interfaceClazz, Object value, AliMockType mockType) {
        Object mockedObject = null;

        if (interfaceClazz == null) {
            if (value == null) {
                throw new RuntimeException("Cannot create mock object without class or object.");
            }
            if (value.getClass().getInterfaces() == null || value.getClass().getInterfaces().length == 0) {
                interfaceClazz = (Class<T>) value.getClass();
            } else {
                interfaceClazz = (Class<T>) value.getClass().getInterfaces()[0];
            }
        }

        if (!isMockableObject(interfaceClazz)) {
            return (T) mockedObject;
        }

        try {
            mockedObject = AbstractCallerProcessor.createCallerObject(value, interfaceClazz, mockType);
        } catch (Throwable e) {
            throw new RuntimeException("Mock failed.", e);
        }
        return (T) mockedObject;
    }

    protected static Object getTargetObject(Proxy proxy) {
        InvocationHandler h = Proxy.getInvocationHandler(proxy);
        Class<?> clazz = h.getClass();
        String className = clazz.getName();
        if ("org.springframework.aop.framework.JdkDynamicAopProxy".equals(className)) {
            try {
                Field advisedFiled = clazz.getDeclaredField("advised");
                advisedFiled.setAccessible(true);
                Object advised = advisedFiled.get(h);
                Method getTargetSourceMethod = advised.getClass().getMethod("getTargetSource");
                Object targetSource = getTargetSourceMethod.invoke(advised);
                return targetSource.getClass().getMethod("getTarget").invoke(targetSource);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if ("com.ms.commons.test.mock.processor.impl.MockProcessor".equals(className)) {
            try {
                Field objectField = clazz.getSuperclass().getDeclaredField("object");
                objectField.setAccessible(true);
                return objectField.get(h);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Unknow proxy `" + className + "` detected.");
    }

    protected static boolean isObjectMocked(Object object) {
        if (object instanceof Proxy) {
            InvocationHandler h = Proxy.getInvocationHandler((Proxy) object);
            Class<?> clazz = h.getClass();
            String className = clazz.getName();
            if ("com.ms.commons.test.mock.processor.impl.MockProcessor".equals(className)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
