/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.common.convert.impl;

import java.util.Calendar;

import com.ms.commons.test.common.convert.AbstractConverter;

/**
 * @author zxc Apr 13, 2013 11:25:21 PM
 */
public class CalendarConverter extends AbstractConverter<Calendar> {

    protected static DateConverter dateConverter = new DateConverter();

    public Calendar internalConvert(Object value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateConverter.convert(value).getTime());
        return calendar;
    }
}
