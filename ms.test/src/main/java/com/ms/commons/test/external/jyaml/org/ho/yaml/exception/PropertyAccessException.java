/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.exception;

/**
 * @author zxc Apr 14, 2013 12:33:23 AM
 */
public class PropertyAccessException extends YamlException {

    private static final long serialVersionUID = 2061226476851310884L;

    public PropertyAccessException() {
        super();
    }

    public PropertyAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyAccessException(String message) {
        super(message);
    }

    public PropertyAccessException(Throwable cause) {
        super(cause);
    }
}
