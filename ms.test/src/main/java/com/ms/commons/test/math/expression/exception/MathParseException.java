/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.math.expression.exception;

/**
 * @author zxc Apr 14, 2013 12:24:23 AM
 */
public class MathParseException extends Exception {

    private static final long serialVersionUID = -6533420557435094996L;

    public MathParseException() {
        super();
    }

    public MathParseException(String message) {
        super(message);
    }

    public MathParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MathParseException(Throwable cause) {
        super(cause);
    }
}
