/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock;

import com.ms.commons.test.mock.impl.parameter.AbstractMockParameter;

/**
 * @author zxc Apr 14, 2013 12:10:45 AM
 */
public class MockPair {

    private Mock                  mock;
    private AbstractMockParameter parameter;

    public MockPair(Mock mock, AbstractMockParameter parameter) {
        this.mock = mock;
        this.parameter = parameter;
    }

    public Mock getMock() {
        return mock;
    }

    public void setMock(Mock mock) {
        this.mock = mock;
    }

    public AbstractMockParameter getParameter() {
        return parameter;
    }

    public void setParameter(AbstractMockParameter parameter) {
        this.parameter = parameter;
    }
}
