/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock.impl.parameter;

import com.ms.commons.test.mock.impl.handle.MockHandle;

/**
 * @author zxc Apr 14, 2013 12:15:22 AM
 */
public class HandleMockParameter extends AbstractMockParameter {

    private MockHandle handle;

    public HandleMockParameter(Object returnValue, MockHandle handle) {
        super(returnValue);
        this.handle = handle;
    }

    public MockHandle getHandle() {
        return handle;
    }

    public void setHandle(MockHandle handle) {
        this.handle = handle;
    }
}
