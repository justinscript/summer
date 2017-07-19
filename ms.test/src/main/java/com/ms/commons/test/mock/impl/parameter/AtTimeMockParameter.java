/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock.impl.parameter;

/**
 * @author zxc Apr 14, 2013 12:15:29 AM
 */
public class AtTimeMockParameter extends AbstractMockParameter {

    private int time;

    public AtTimeMockParameter(Object returnValue, int time) {
        super(returnValue);
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
