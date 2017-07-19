/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.handler;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * 在数据转换时，转换日期对象
 * 
 * @author zxc Apr 12, 2013 4:11:11 PM
 */
public class DateEditor extends PropertyEditorSupport {

    private List<DateFormat> dateFormatList;

    public DateEditor(DateFormat... dateFormats) {
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

    /**
     * Parse the Date from the given text, using the specified DateFormat.
     */
    public void setAsText(String text) throws IllegalArgumentException {
        if (!StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else {
            for (DateFormat dateFormat : dateFormatList) {
                try {
                    setValue(dateFormat.parse(text));
                    return;
                } catch (ParseException ex) {

                }
            }
        }
    }

    /**
     * Format the Date as String, using the specified DateFormat.
     */
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? this.dateFormatList.get(0).format(value) : "");
    }
}
