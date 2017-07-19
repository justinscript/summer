/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock;

import java.io.Serializable;

/**
 * @author zxc Apr 14, 2013 12:10:16 AM
 */
public class MockResult implements Serializable {

    private static final long      serialVersionUID = 1L;

    public static final MockResult CALL_NEXT        = new MockResult(false, null);

    private boolean                isFinal;

    private Object                 returnValue;

    public static MockResult createFinal(Object returnValue) {
        return new MockResult(true, returnValue);
    }

    public MockResult(boolean isFinal, Object returnValue) {
        this.isFinal = isFinal;
        this.returnValue = returnValue;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}
