/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.validation;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zxc Apr 12, 2013 11:22:04 PM
 */
public class MobileTest {

    private String fieldKey     = "mobile";
    private String displayName  = "手机号码";
    private String validatorIds = "mobile";

    @Test
    public void lengthTest() {
        ValidationResult valid = FormValidator.isValid(fieldKey, displayName, "1234567890", validatorIds);
        Assert.assertFalse(valid.isValid());
        valid = FormValidator.isValid(fieldKey, displayName, "123456789012", validatorIds);
        Assert.assertFalse(valid.isValid());
    }

    /**
     * 手机号码必须时1开头
     */
    @Test
    public void testStart() {
        ValidationResult valid = FormValidator.isValid(fieldKey, displayName, "02345678901", validatorIds);
        Assert.assertFalse(valid.isValid());
    }

    /**
     * 手机号码必须时1开头
     */
    @Test
    public void normal() {
        ValidationResult valid = FormValidator.isValid(fieldKey, displayName, "12345678901", validatorIds);
        Assert.assertTrue(valid.isValid());
    }
}
