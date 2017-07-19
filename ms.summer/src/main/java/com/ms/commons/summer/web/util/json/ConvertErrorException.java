/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.util.json;

/**
 * @author zxc Apr 12, 2013 4:25:40 PM
 */
public class ConvertErrorException extends RuntimeException {

    private static final long serialVersionUID = -3348772457721309660L;

    private Object            value;
    private Class<?>          type;

    public ConvertErrorException(Object value, Class<?> type) {
        super("can't convert '" + value + "' to " + type.getName());
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
