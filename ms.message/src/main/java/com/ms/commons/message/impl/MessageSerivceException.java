/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl;

/**
 * @author zxc Apr 13, 2014 10:42:00 PM
 */
public class MessageSerivceException extends RuntimeException {

    private static final long serialVersionUID = -1346595758384052573L;

    public MessageSerivceException() {
        super();
    }

    public MessageSerivceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public MessageSerivceException(String arg0) {
        super(arg0);
    }

    public MessageSerivceException(Throwable arg0) {
        super(arg0);
    }
}
