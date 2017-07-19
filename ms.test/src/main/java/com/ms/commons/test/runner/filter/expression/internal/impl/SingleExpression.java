/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runner.filter.expression.internal.impl;

import com.ms.commons.test.runner.filter.expression.internal.Expression;

/**
 * @author zxc Apr 14, 2013 12:20:36 AM
 */
public class SingleExpression implements Expression {

    public Expression expression;

    public SingleExpression(Expression expression) {
        this.expression = expression;
    }

    public Object evaluate(Object param) {
        return expression.evaluate(param);
    }
}
