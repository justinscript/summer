/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock;

import java.lang.reflect.Method;
import java.util.List;

import com.ms.commons.test.mock.impl.parameter.AbstractMockParameter;
import com.ms.commons.test.mock.inject.register.MockRegister;

/**
 * @author zxc Apr 14, 2013 12:10:05 AM
 */
public class MockUtil {

    public static final MockResult callMethod(Object object, Method method, Object[] params) throws Throwable {
        MockContext mockContext = getMockContext(method);
        List<MockPair> mockPairList = mockContext.getMockPairList();

        for (int i = 0; i < mockPairList.size(); i++) {
            MockPair mockPair = mockPairList.get(i);
            Mock mock = mockPair.getMock();
            AbstractMockParameter mockParameter = mockPair.getParameter();

            MockResult result = mock.mock(mockContext, mockParameter, object, method, params);
            if (result.isFinal()) {
                return result;
            }
        }
        // invoke actual method
        return MockResult.CALL_NEXT;
    }

    public static MockContext getMockContext(Method method) {
        return MockRegister.getMockContext(method);
    }

    public static int getMockCalledTime(Method method) {
        return MockClassCalledUtil.getMockMethodCalledTimes(method.getDeclaringClass(), method.getName());
    }
}
