/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.external.jyaml.org.ho.yaml.wrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import com.ms.commons.test.external.jyaml.org.ho.yaml.exception.YamlException;

/**
 * @author zxc Apr 14, 2013 12:31:38 AM
 */
@SuppressWarnings("rawtypes")
public class DateWrapper extends DefaultSimpleTypeWrapper implements WrapperFactory {

    public static final String DATEFORMAT_YAML    = "yyyy-MM-dd hh:mm:ss";
    public static final String DATEFORMAT_ISO8601 = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'";

    public DateWrapper() {
        super(Date.class);
    }

    @Override
    public Class expectedArgType() {
        return String.class;
    }

    @Override
    public ObjectWrapper makeWrapper() {
        ObjectWrapper ret = new DateWrapper();
        ret.setYamlConfig(config);
        return ret;
    }

    @Override
    public void setObject(Object obj) {
        if (obj == null) super.setObject(null);
        else if (obj instanceof Date) {
            super.setObject(obj);
        } else {
            String arg = (String) obj;
            super.setObject(parseDate(arg));
            if (!objectInitialized) throw new YamlException("Can't instantiate " + getType() + " with literal " + arg);
        }
    }

    @SuppressWarnings("deprecation")
    Date parseDate(String s) {
        DateFormat fmt = config.getDateFormatter();
        if (fmt != null) {
            try {
                return fmt.parse(s);
            } catch (ParseException e) {
                throw new YamlException("Error parsing date: '" + s + "'", e);
            }
        } else {
            // Original method
            try {
                return new Date(Long.parseLong(s));
            } catch (NumberFormatException e) {
            }
            return new Date(s);
        }
    }

    @Override
    public Object getOutputValue() {
        return formateDate((Date) getObject());
    }

    /**
     * Writes a date into the output, using the preferred format
     * 
     * @param date - the date to write
     */
    String formateDate(Date date) {
        DateFormat fmt = config.getDateFormatter();
        if (fmt == null) {
            return "" + date.getTime();
        } else {
            return "\"" + fmt.format(date) + "\"";
        }
    }
}
