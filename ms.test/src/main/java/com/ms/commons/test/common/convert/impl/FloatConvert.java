/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.convert.impl;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:24:45 PM
 */
public class FloatConvert extends AbstractConverter<Float> {

    @Override
    protected Float internalConvert(Object value) {
        String strValue = String.valueOf(value).trim();
        if (strValue.length() == 0) {
            return null;
        }
        return Float.valueOf(strValue);
    }
}
