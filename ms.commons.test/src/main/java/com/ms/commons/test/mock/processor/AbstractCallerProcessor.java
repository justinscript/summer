/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ms.commons.test.mock.MockClassCalledUtil;
import com.ms.commons.test.mock.MockResult;
import com.ms.commons.test.mock.MockUtil;
import com.ms.commons.test.mock.processor.impl.CGLibMockProcessor;
import com.ms.commons.test.mock.processor.impl.MockProcessor;

/**
 * @author zxc Apr 14, 2013 12:12:11 AM
 */
public abstract class AbstractCallerProcessor {

    // object for the object to be called
    protected Object   object;
    protected Class<?> clazz;

    public AbstractCallerProcessor(Object object, Class<?> clazz) {
        this.object = object;
        this.clazz = clazz;
    }

    protected Object processCall(Object object, Method method, Object[] params, Object additionParam) throws Throwable {
        try {
            MockResult mockResult = MockUtil.callMethod(object, method, params);
            if (mockResult.isFinal()) {
                Object returnValue = mockResult.getReturnValue();
                MockClassCalledUtil.addMockClassCalled(object, method, returnValue, params, null);
                return returnValue;
            } else {
                if (this.object == null) {
                    throw new RuntimeException("No orientied object.");
                }
                Object returnValue = callMethod(method, params, additionParam);
                MockClassCalledUtil.addMockClassCalled(object, method, returnValue, params, null);
                return returnValue;
            }
        } catch (Throwable t) {
            MockClassCalledUtil.addMockClassCalled(object, method, null, params, t);
            throw t;
        }
    }

    protected Object callMethod(Method method, Object[] args, Object additionParam) throws Throwable {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(object, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public abstract <T> T createEnhancedObject();

    // ====================================================================== //

    /**
     * ����Mock���?CGLIB�����JDK��̬���� d
     */
    @SuppressWarnings("unchecked")
    public static final <T> T createCallerObject(T object, Class<?> interfaceClass, AliMockType mockType)
                                                                                                         throws Throwable {
        AbstractCallerProcessor processor = null;
        if ((mockType == null) || (mockType == AliMockType.AUTO)) {
            if (interfaceClass.isInterface()) {
                processor = new MockProcessor(object, interfaceClass);
            } else {
                processor = new CGLibMockProcessor(object, interfaceClass);
            }
        } else if (mockType == AliMockType.PROXY) {
            processor = new MockProcessor(object, interfaceClass);
        } else if (mockType == AliMockType.CGLIB) {
            processor = new CGLibMockProcessor(object, interfaceClass);
        } else {
            throw new RuntimeException("Unknow mock type: " + mockType);
        }
        return (T) processor.createEnhancedObject();
    }
}
