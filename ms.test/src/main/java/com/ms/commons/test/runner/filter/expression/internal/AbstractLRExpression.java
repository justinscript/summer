/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.runner.filter.expression.internal;

/**
 * @author zxc Apr 14, 2013 12:20:27 AM
 */
public abstract class AbstractLRExpression implements Expression {

    protected Expression left;
    protected Expression right;

    public AbstractLRExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    protected boolean eval(Expression expr, Object param) {
        return ((Boolean) expr.evaluate(param)).booleanValue();
    }

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "@" + left + "," + right + "]";
    }
}
