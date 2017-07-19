/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.convert.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:25:38 PM
 */
public class BigDecimalConverter extends AbstractConverter<BigDecimal> {

    public BigDecimal internalConvert(Object value) {

        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }

        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }

        String strValue = value.toString().trim();
        return new BigDecimal(strValue);
    }
}
