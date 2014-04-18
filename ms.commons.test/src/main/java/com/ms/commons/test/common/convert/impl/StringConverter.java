/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert.impl;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:24:07 PM
 */
public class StringConverter extends AbstractConverter<String> {

    public String internalConvert(Object value) {
        if ((value.getClass() == double.class) || (value.getClass() == Double.class)) {
            Double doubleValue = (Double) value;
            if (Math.ceil(doubleValue) == Math.floor(doubleValue)) {
                return String.valueOf(doubleValue.longValue());
            }
        }
        if ((value.getClass() == float.class) || (value.getClass() == Float.class)) {
            Float floatValue = (Float) value;
            if (Math.ceil(floatValue) == Math.floor(floatValue)) {
                return String.valueOf(floatValue.longValue());
            }
        }
        return String.valueOf(value);
    }
}
