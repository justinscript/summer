/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.math.expression.builder;

import com.ms.commons.test.math.expression.MathExpression;

/**
 * @author zxc Apr 14, 2013 12:24:30 AM
 */
public interface MathExpressionBuilder {

    MathExpression buildSimpleMathExpression(String value);

    MathExpression buildSimpleMathExpressionByOp(char op, MathExpression left, MathExpression right);
}
