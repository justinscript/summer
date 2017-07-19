/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.validation.validator;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zxc Apr 12, 2013 11:18:31 PM
 */
public abstract class AbstarctValidator implements Validator {

    protected String      errorMessage;
    private static Logger logger = LoggerFactory.getLogger(AbstarctValidator.class);

    public String getErrorMessage(String displayName) {
        // errorMessage = paser(errorMessage, displayName);
        return errorMessage;
    }

    String paser(String errorMessage, String displayName) {
        int beginIndex = errorMessage.indexOf("${");
        int endIndex = errorMessage.indexOf("}");
        if (beginIndex != -1 && endIndex != -1) {
            String name = errorMessage.substring(beginIndex + 2, endIndex);
            if (name.equalsIgnoreCase("displayName")) {
                errorMessage = StringUtils.replace(errorMessage, "${" + name + "}", displayName);
            } else {
                try {
                    Field f = this.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    Object value = f.get(this);
                    errorMessage = StringUtils.replace(errorMessage, "${" + name + "}", value.toString());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return errorMessage;
                }
            }
            return paser(errorMessage, displayName);
        } else {
            return errorMessage;
        }
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isValid(Object value) {
        if (filterBlankValue()) {
            if (value == null) {
                return true;
            }
            if (value instanceof String && StringUtils.isBlank((String) value)) {// 将""串过滤掉
                return true;
            }
        }
        return validate(value);

    }

    /**
     * 过滤掉空值
     */
    protected boolean filterBlankValue() {
        return true;
    }

    /**
     * 验证非空的值
     */
    protected abstract boolean validate(Object value);
}
