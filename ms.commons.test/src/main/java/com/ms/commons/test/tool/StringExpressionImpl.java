/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.tool;

import java.util.regex.Pattern;

import com.ms.commons.test.runner.filter.expression.internal.AbstractSimpleExpression;

/**
 * @author zxc Apr 13, 2013 11:42:16 PM
 */
public class StringExpressionImpl extends AbstractSimpleExpression {

    private Pattern expressionPattern;

    public StringExpressionImpl(String value) {
        super(value);

        String re = value.toLowerCase();
        re = re.replace(".", "\\.");
        re = re.replace("*", ".*");
        re = "^" + re + "$";
        expressionPattern = Pattern.compile(re);
    }

    public Object evaluate(Object param) {

        return expressionPattern.matcher(((String) param).toLowerCase()).matches();
    }
}
