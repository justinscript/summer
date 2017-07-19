/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.fasttext.security.xss;

/**
 * @author zxc Apr 12, 2013 3:28:48 PM
 */
public class ScanException extends RuntimeException {

    private static final long serialVersionUID = -8204412394195926823L;

    public ScanException(Exception e) {
        super(e);
    }

    public ScanException(String string) {
        super(string);
    }
}
