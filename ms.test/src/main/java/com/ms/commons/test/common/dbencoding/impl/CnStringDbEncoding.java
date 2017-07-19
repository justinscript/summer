/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.dbencoding.impl;

import com.ms.commons.test.common.convert.impl.database.CnStringTypeConverter;
import com.ms.commons.test.common.convert.impl.database.UnCnStringTypeConverter;
import com.ms.commons.test.common.dbencoding.DbEncoding;

/**
 * @author zxc Apr 13, 2013 11:22:54 PM
 */
public class CnStringDbEncoding implements DbEncoding<String> {

    static private final CnStringTypeConverter   cnStringTypeConverter   = new CnStringTypeConverter();
    static private final UnCnStringTypeConverter unCnStringTypeConverter = new UnCnStringTypeConverter();

    public String encode(Object value) {
        return cnStringTypeConverter.convert(value);
    }

    public Object decode(String value) {
        return unCnStringTypeConverter.convert(value);
    }
}
