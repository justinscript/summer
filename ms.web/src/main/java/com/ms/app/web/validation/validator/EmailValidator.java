/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.validation.validator;

import org.apache.commons.lang.StringUtils;

import com.ms.app.web.validation.annotation.ValidationInfo;

/**
 * @author zxc Apr 12, 2013 11:18:22 PM
 */
public class EmailValidator extends AbstarctValidator {

    public boolean isAppeared(ValidationInfo validationInfo) {
        return false;
    }

    public boolean validate(Object value) {
        if (value instanceof String) {
            String email = StringUtils.trimToNull((String) value);
            return org.apache.commons.validator.EmailValidator.getInstance().isValid(email);
        } else {
            throw new RuntimeException("EmailValidator 只能验证String类型的值");
        }
    }
}
