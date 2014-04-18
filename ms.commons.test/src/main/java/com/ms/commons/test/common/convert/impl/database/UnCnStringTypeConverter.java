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
 * @author zxc Apr 13, 2013 11:26:02 PM
 */
public class UnCnStringTypeConverter implements Converter<String> {

    static private final StringConverter stringConverter = new StringConverter();

    private static final String          STORE_CHARSET   = "GBK";
    private static final String          SYSTEMEN_CODING = "iso-8859-1";

    public String convert(Object value) {
        String string = stringConverter.convert(value);
        if (string == null) {
            return null;
        }
        try {
            return new String(string.getBytes(SYSTEMEN_CODING), STORE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Oh, we do not support GBK or iso-8859-1 ?", e);
        }
    }
}
