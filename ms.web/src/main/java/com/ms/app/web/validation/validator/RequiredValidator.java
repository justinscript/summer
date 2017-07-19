/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation.validator;

import org.apache.commons.lang.StringUtils;

/**
 * @author zxc Apr 12, 2013 11:17:59 PM
 */
public class RequiredValidator extends AbstarctValidator {

    protected boolean filterBlankValue() {
        // 不需要过滤空值
        return false;
    }

    public boolean validate(Object value) {
        if (value instanceof String) {
            String newValue = StringUtils.trimToNull((String) value);
            return newValue != null;
        } else {
            return value != null;
        }
    }

    public String getErrorMessage(String display) {
        return display + errorMessage;
    }
}
