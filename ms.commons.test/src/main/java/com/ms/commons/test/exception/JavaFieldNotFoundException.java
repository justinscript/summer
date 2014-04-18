/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.exception;

/**
 * @author zxc Apr 13, 2013 11:32:35 PM
 */
public class JavaFieldNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7406827674440749935L;

    public JavaFieldNotFoundException(Class<?> clazz, String field) {
        super("Filed `" + field + "` cannot be found in class `" + clazz.getName() + "`.");
    }
}
