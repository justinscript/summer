/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader.exception;

/**
 * @author zxc Apr 13, 2013 11:37:27 PM
 */
public class DataReaderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5489321777815331245L;

    public DataReaderNotFoundException() {
        super();
    }

    public DataReaderNotFoundException(String message) {
        super(message);
    }

    public DataReaderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataReaderNotFoundException(Throwable cause) {
        super(cause);
    }
}
