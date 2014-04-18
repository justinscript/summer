/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert.impl;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:24:22 PM
 */
public class IntegerConverter extends AbstractConverter<Integer> {

    public Integer internalConvert(Object value) {
        String strValue = String.valueOf(value).trim();
        if (!isNumeric(strValue)) {
            return null;
        }
        return Double.valueOf(strValue).intValue();
    }

    private static boolean isNumeric(String value) {
        try {
            Double.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
