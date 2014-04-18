/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runner.filter.expression.internal.impl;

import com.ms.commons.test.runner.filter.expression.internal.AbstractLRExpression;
import com.ms.commons.test.runner.filter.expression.internal.Expression;

/**
 * @author zxc Apr 14, 2013 12:20:44 AM
 */
public class OrExpression extends AbstractLRExpression {

    public OrExpression(Expression left, Expression right) {
        super(left, right);
    }

    public Object evaluate(Object param) {
        return Boolean.valueOf(eval(left, param) || eval(right, param));
    }
}
