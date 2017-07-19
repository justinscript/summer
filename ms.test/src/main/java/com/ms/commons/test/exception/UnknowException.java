/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.exception;

/**
 * @author zxc Apr 13, 2013 11:32:25 PM
 */
public class UnknowException extends RuntimeException {

    private static final long serialVersionUID = -8558696849200278991L;

    public UnknowException() {
        super();
    }

    public UnknowException(String message) {
        super(message);
    }

    public UnknowException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknowException(Throwable cause) {
        super(cause);
    }
}
