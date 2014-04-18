/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.security.web;

/**
 * @author zxc Apr 12, 2013 4:06:18 PM
 */
public class SummerSecurityRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -4370432135968889140L;

    public SummerSecurityRuntimeException() {
    }

    public SummerSecurityRuntimeException(final String message) {
        super(message);
    }

    public SummerSecurityRuntimeException(final Throwable cause) {
        super(cause);
    }

    public SummerSecurityRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
