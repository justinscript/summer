/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.common.convert.impl;

import java.sql.Timestamp;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:23:59 PM
 */
public class TimestampConverter extends AbstractConverter<Timestamp> {

    protected static final DateConverter DATE_CONVERTER = new DateConverter();

    @Override
    public Timestamp internalConvert(Object value) {
        return new Timestamp(DATE_CONVERTER.convert(value).getTime());
    }
}
