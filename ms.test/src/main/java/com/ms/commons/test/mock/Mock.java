/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock;

import java.lang.reflect.Method;

import com.ms.commons.test.mock.impl.parameter.AbstractMockParameter;

/**
 * @author zxc Apr 14, 2013 12:11:34 AM
 */
public interface Mock {

    MockResult mock(MockContext mockContext, AbstractMockParameter mockParameter, Object object, Method method,
                    Object[] params) throws Throwable;
}
