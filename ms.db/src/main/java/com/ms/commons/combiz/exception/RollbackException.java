/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.combiz.exception;

import org.springframework.transaction.TransactionException;

/**
 * @author zxc Apr 12, 2013 7:15:26 PM
 */
public class RollbackException extends TransactionException {

    private static final long serialVersionUID = -8139406723353941539L;

    public RollbackException(String msg) {
        super(msg);
    }

    public RollbackException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
