/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.impl;

import java.lang.reflect.Method;

import com.ms.commons.test.mock.AbstractMock;
import com.ms.commons.test.mock.MockContext;
import com.ms.commons.test.mock.MockResult;
import com.ms.commons.test.mock.MockUtil;
import com.ms.commons.test.mock.impl.parameter.AbstractMockParameter;
import com.ms.commons.test.mock.impl.parameter.AtTimeMockParameter;

/**
 * @author zxc Apr 14, 2013 12:14:48 AM
 */
public class AtTimeMock extends AbstractMock {

    protected MockResult internalMock(MockContext mockContext, AbstractMockParameter mockParameter, Object object,
                                      Method method, Object[] params) throws Throwable {
        AtTimeMockParameter parameter = asType(mockParameter);
        int calledTime = MockUtil.getMockCalledTime(method);
        if (parameter.getTime() == calledTime) {
            return MockResult.createFinal(parameter.getReturnValue());
        } else {
            return MockResult.CALL_NEXT;
        }
    }
}
