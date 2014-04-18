/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.inject.register;

import com.ms.commons.test.mock.MockPair;
import com.ms.commons.test.mock.impl.AddReturnMock;
import com.ms.commons.test.mock.impl.AllTimesMock;
import com.ms.commons.test.mock.impl.AtTimeMock;
import com.ms.commons.test.mock.impl.HandleMock;
import com.ms.commons.test.mock.impl.OnMock;
import com.ms.commons.test.mock.impl.handle.MockHandle;
import com.ms.commons.test.mock.impl.parameter.AddReturnMockParameter;
import com.ms.commons.test.mock.impl.parameter.AllTimesMockParameter;
import com.ms.commons.test.mock.impl.parameter.AtTimeMockParameter;
import com.ms.commons.test.mock.impl.parameter.HandleMockParameter;
import com.ms.commons.test.mock.impl.parameter.OnMockParameter;

/**
 * @author zxc Apr 14, 2013 12:13:25 AM
 */
public class MockPairCreater {

    public static MockPair createAddReturn(Object value) {
        return new MockPair(new AddReturnMock(), new AddReturnMockParameter(value));
    }

    public static MockPair createAtTime(Object value, int time) {
        return new MockPair(new AtTimeMock(), new AtTimeMockParameter(value, time));
    }

    public static MockPair createAllTimes(Object value) {
        return new MockPair(new AllTimesMock(), new AllTimesMockParameter(value));
    }

    public static MockPair createOn(Object value, Object[] params) {
        return new MockPair(new OnMock(), new OnMockParameter(value, params));
    }

    public static MockPair createHandle(Object value, MockHandle handle) {
        return new MockPair(new HandleMock(), new HandleMockParameter(value, handle));
    }
}
