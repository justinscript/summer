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
import com.ms.commons.test.mock.impl.parameter.AbstractMockParameter;
import com.ms.commons.test.mock.impl.parameter.AllTimesMockParameter;

/**
 * @author zxc Apr 14, 2013 12:14:55 AM
 */
public class AllTimesMock extends AbstractMock {

    protected MockResult internalMock(MockContext mockContext, AbstractMockParameter mockParameter, Object object,
                                      Method method, Object[] params) throws Throwable {
        AllTimesMockParameter parameter = asType(mockParameter);
        return MockResult.createFinal(parameter.getReturnValue());
    }
}
