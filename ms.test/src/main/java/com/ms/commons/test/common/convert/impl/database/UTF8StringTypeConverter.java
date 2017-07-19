/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert.impl.database;

import java.io.UnsupportedEncodingException;

import com.ms.commons.test.common.convert.Converter;
import com.ms.commons.test.common.convert.impl.StringConverter;

/**
 * @author zxc Apr 13, 2013 11:25:46 PM
 */
public class UTF8StringTypeConverter implements Converter<String> {

    static private final StringConverter stringConverter = new StringConverter();

    private static final String          STORE_CHARSET   = "UTF-8";
    private static final String          SYSTEMEN_CODING = "iso-8859-1";

    public String convert(Object value) {
        String string = stringConverter.convert(value);
        if (string == null) {
            return null;
        }
        try {
            return new String(string.getBytes(STORE_CHARSET), SYSTEMEN_CODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Oh, we do not support UTF-8 or iso-8859-1 ?", e);
        }
    }
}
