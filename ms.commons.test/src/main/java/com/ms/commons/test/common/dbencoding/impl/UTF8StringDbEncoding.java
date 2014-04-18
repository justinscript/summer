/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.dbencoding.impl;

import com.ms.commons.test.common.convert.impl.database.UTF8StringTypeConverter;
import com.ms.commons.test.common.convert.impl.database.UnUTF8StringTypeConverter;
import com.ms.commons.test.common.dbencoding.DbEncoding;

/**
 * @author zxc Apr 13, 2013 11:22:46 PM
 */
public class UTF8StringDbEncoding implements DbEncoding<String> {

    static private final UTF8StringTypeConverter   utf8StringTypeConverter   = new UTF8StringTypeConverter();
    static private final UnUTF8StringTypeConverter unUtf8StringTypeConverter = new UnUTF8StringTypeConverter();

    public String encode(Object value) {
        return utf8StringTypeConverter.convert(value);
    }

    public Object decode(String value) {
        return unUtf8StringTypeConverter.convert(value);
    }
}
