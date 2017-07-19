/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.utils;

/**
 * @author zxc Apr 12, 2013 10:51:29 PM
 */
public enum ResultCode {
    SUCCESS(0), ERROR(1), NEED_LOGIN(2), SUBMITED(3), FORBIDDEN(4);

    private int value;

    private ResultCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isError() {
        return this == ERROR;
    }

    public boolean isNeedLogin() {
        return this == NEED_LOGIN;
    }

    public boolean isSubmited() {
        return this == SUBMITED;
    }

    public static boolean isSuccess(int value) {
        return SUCCESS.getValue() == value;
    }

    public static boolean isError(int value) {
        return ERROR.getValue() == value;
    }

    public static boolean isNeedLogin(int value) {
        return NEED_LOGIN.getValue() == value;
    }

    public static boolean isSubmited(int value) {
        return SUBMITED.getValue() == value;
    }

    public static ResultCode getEnum(int value) {
        for (ResultCode code : values()) {
            if (value == code.getValue()) return code;
        }
        return null;
    }
}
