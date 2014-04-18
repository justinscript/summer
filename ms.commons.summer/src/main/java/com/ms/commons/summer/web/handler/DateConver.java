/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.handler;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.Converter;

/**
 * @author zxc Apr 12, 2013 4:11:24 PM
 */
public class DateConver implements Converter {

    private List<DateFormat> dateFormatList;

    public DateConver(DateFormat... dateFormats) {
        if (dateFormats == null || dateFormats.length == 0) {
            throw new IllegalArgumentException("dateFormat 不能为空");
        }
        dateFormatList = new ArrayList<DateFormat>();
        for (DateFormat df : dateFormats) {
            if (df != null) {
                dateFormatList.add(df);
            }
        }
        if (dateFormats.length == 0) {
            throw new IllegalArgumentException("dateFormat 不能为空");
        }
    }

    @SuppressWarnings("rawtypes")
    public Object convert(Class type, Object value) {
        if (value == null || !(value instanceof String)) {
            return null;
        }
        String str = (String) value;
        for (DateFormat dateFormat : dateFormatList) {
            try {
                return dateFormat.parse(str);
            } catch (ParseException ex) {
            }
        }
        return null;
    }
}
