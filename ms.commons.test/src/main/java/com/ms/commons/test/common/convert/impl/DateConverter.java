/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert.impl;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import com.ms.commons.test.common.convert.AbstractConverter;
import com.ms.commons.test.math.expression.MathExpression;
import com.ms.commons.test.math.expression.builder.impl.SimpleDateMathExpressionBuilder;
import com.ms.commons.test.math.expression.exception.MathParseException;
import com.ms.commons.test.math.expression.util.MathExpressionParseUtil;

/**
 * @author zxc Apr 13, 2013 11:25:04 PM
 */
public class DateConverter extends AbstractConverter<Date> {

    protected static String[] parsePatterns = new String[] { "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd" };

    @Override
    public Date internalConvert(Object value) {
        if ((value.getClass() == double.class) || (value.getClass() == Double.class)) {
            return HSSFDateUtil.getJavaDate((Double) value);
        }
        if (value.getClass() == String.class) {
            String sv = value.toString().trim();
            if (sv.length() == 0) {
                return null;
            }

            if (sv.startsWith("=")) {
                try {
                    SimpleDateMathExpressionBuilder sme = new SimpleDateMathExpressionBuilder();
                    MathExpression me = MathExpressionParseUtil.parse(sv.substring(1), sme);
                    return (Date) me.evaluate(null);
                } catch (MathParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    return DateUtils.parseDate(sv, parsePatterns);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try {
            return DateUtils.parseDate(String.valueOf(value), parsePatterns);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
