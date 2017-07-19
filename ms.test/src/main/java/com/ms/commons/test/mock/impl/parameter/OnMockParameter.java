/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock.impl.parameter;

/**
 * s * @author zxc Apr 14, 2013 12:15:11 AM
 */
public class OnMockParameter extends AbstractMockParameter {

    private Object[] params;

    public OnMockParameter(Object returnValue, Object[] params) {
        super(returnValue);
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
