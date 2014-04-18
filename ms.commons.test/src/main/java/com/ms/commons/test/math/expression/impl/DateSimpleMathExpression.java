/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.math.expression.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ms.commons.test.math.expression.MathExpression;

/**
 * @author zxc Apr 14, 2013 12:24:09 AM
 */
public class DateSimpleMathExpression implements MathExpression {

    private static final String SYSDATE = "SYSDATE";
    private String              value;

    public DateSimpleMathExpression(String value) {
        this.value = value;
    }

    public Object evaluate(Object param) {
        String upperValue = value.trim().toUpperCase();
        if (SYSDATE.equals(upperValue)) {
            return new Date();
        }
        if (upperValue.startsWith(SYSDATE)) { // if starts with 'SYSDATE'
            String ymd = upperValue.substring(SYSDATE.length());

            Pattern p = Pattern.compile("(\\d\\d)(\\d\\d)(\\d\\d)(\\d\\d\\d)?");
            Matcher m = p.matcher(ymd);
            if (!m.matches()) {
                throw new RuntimeException("Error format '" + value + "'");
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(1)));
            cal.set(Calendar.MINUTE, Integer.parseInt(m.group(2)));
            cal.set(Calendar.SECOND, Integer.parseInt(m.group(3)));
            if ((m.group(4) != null) && (m.group(4).length() > 0)) {
                cal.set(Calendar.MILLISECOND, Integer.parseInt(m.group(4)));
            } else {
                cal.set(Calendar.MILLISECOND, 0);
            }
            return cal.getTime();
        }
        return Double.parseDouble(value);
    }

    @Override
    public String toString() {
        return "DateSimpleMathExpression [value=" + value + "]";
    }
}
