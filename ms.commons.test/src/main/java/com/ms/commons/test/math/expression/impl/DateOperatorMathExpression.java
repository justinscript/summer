/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.math.expression.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.ms.commons.test.math.expression.MathExpression;

/**
 * @author zxc Apr 14, 2013 12:24:16 AM
 */
public class DateOperatorMathExpression implements MathExpression {

    private char           operator;
    private MathExpression left;
    private MathExpression right;

    public DateOperatorMathExpression(char operator, MathExpression left, MathExpression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Object evaluate(Object param) {
        Object leftValue = left.evaluate(param);
        Object rightValue = right.evaluate(param);
        if ((leftValue instanceof Date) && (rightValue instanceof Double)) {
            long rv = (long) (DateUtils.MILLIS_PER_DAY * ((Double) rightValue).doubleValue());
            Date lv = (Date) leftValue;
            switch (operator) {
                case '+':
                    return new Date(lv.getTime() + rv);
                case '-':
                    return new Date(lv.getTime() - rv);
                default:
                    throw new RuntimeException("Unsupported operator between Date and Double: " + operator);
            }
        } else if ((leftValue instanceof Double) && (rightValue instanceof Double)) {
            double l = ((Double) leftValue).doubleValue();
            double r = ((Double) rightValue).doubleValue();
            switch (operator) {
                case '+':
                    return Double.valueOf(l + r);
                case '-':
                    return Double.valueOf(l - r);
                case '*':
                    return Double.valueOf(l * r);
                case '/':
                    return Double.valueOf(l / r);
                default:
                    throw new RuntimeException("Unknow operator: " + operator);
            }
        } else {
            throw new RuntimeException("Error type of: " + leftValue.getClass() + " and " + rightValue.getClass());
        }
    }

    @Override
    public String toString() {
        return "DateOperatorMathExpression [operator=" + operator + ", left=" + left + ", right=" + right + "]";
    }
}
