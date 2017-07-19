/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock.impl;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.ms.commons.test.mock.AbstractMock;
import com.ms.commons.test.mock.MockContext;
import com.ms.commons.test.mock.MockResult;
import com.ms.commons.test.mock.impl.parameter.AbstractMockParameter;
import com.ms.commons.test.mock.impl.parameter.OnMockParameter;

/**
 * @author zxc Apr 14, 2013 12:14:21 AM
 */
public class OnMock extends AbstractMock {

    protected MockResult internalMock(MockContext mockContext, AbstractMockParameter mockParameter, Object object,
                                      Method method, Object[] params) throws Throwable {
        OnMockParameter parameter = asType(mockParameter);
        if (Arrays.equals(parameter.getParams(), params)) {
            return MockResult.createFinal(parameter.getReturnValue());
        } else {
            return MockResult.CALL_NEXT;
        }
    }
}
