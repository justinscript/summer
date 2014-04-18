/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert.impl;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:25:29 PM
 */
public class BooleanConverter extends AbstractConverter<Boolean> {

    StringConverter stringConverter = new StringConverter();

    public Boolean internalConvert(Object value) {
        return Boolean.valueOf(stringConverter.convert(value));
    }
}
