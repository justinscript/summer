/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common;

/**
 * @author zxc Apr 13, 2013 11:20:02 PM
 */
public class ExceptionUtil {

    public static RuntimeException wrapToRuntimeException(Throwable throwable) {
        if (throwable == null) {
            return new NullPointerException("Oops, parameter throwable is null.");
        }
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }

    public static void thorwRuntimeException(Throwable throwable) {
        throw new RuntimeException(wrapToRuntimeException(throwable));
    }

    public static void thorwRuntimeException(String message) {
        throw new RuntimeException(message);
    }
}
