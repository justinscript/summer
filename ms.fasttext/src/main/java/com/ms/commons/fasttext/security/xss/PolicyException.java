/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.fasttext.security.xss;

/**
 * @author zxc Apr 12, 2013 3:28:57 PM
 */
public class PolicyException extends Exception {

    private static final long serialVersionUID = -2045960001387814125L;

    public PolicyException(Exception e) {
        super(e);
    }

    public PolicyException(String string) {
        super(string);
    }
}
