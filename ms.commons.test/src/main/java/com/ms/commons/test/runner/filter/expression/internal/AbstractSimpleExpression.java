/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runner.filter.expression.internal;

/**
 * @author zxc Apr 14, 2013 12:20:20 AM
 */
public abstract class AbstractSimpleExpression implements Expression {

    protected String value;

    public AbstractSimpleExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "@" + value + "]";
    }
}
