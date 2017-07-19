/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.math.expression.builder.impl;

import com.ms.commons.test.math.expression.MathExpression;
import com.ms.commons.test.math.expression.builder.MathExpressionBuilder;
import com.ms.commons.test.math.expression.impl.DateOperatorMathExpression;
import com.ms.commons.test.math.expression.impl.DateSimpleMathExpression;

/**
 * @author zxc Apr 14, 2013 12:24:39 AM
 */
public class SimpleDateMathExpressionBuilder implements MathExpressionBuilder {

    public MathExpression buildSimpleMathExpression(String value) {
        return new DateSimpleMathExpression(value);
    }

    public MathExpression buildSimpleMathExpressionByOp(char op, MathExpression left, MathExpression right) {
        return new DateOperatorMathExpression(op, left, right);
    }
}
