/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author zxc Apr 12, 2013 11:22:14 PM
 */
public class FormValidatorTest {

    /**
     * 测试required的条件
     */
    @Test
    public void requiredTest() {
        Form form = new MockForm();
        ValidationResult valid = FormValidator.isValid(form);
        System.out.println(valid);
        assertFalse(valid.isValid());
        assertTrue(valid.isFieldError("email"));
    }

    /**
     * 长度测试
     */
    @Test
    public void LengthTest() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 129; i++) {
            sb.append("a");
        }
        assertTrue(sb.length() == 129);
        MockForm form = new MockForm();
        form.setEmail(sb.toString());
        ValidationResult valid = FormValidator.isValid(form);
        System.out.println(valid);
        assertFalse(valid.isValid());
        assertTrue(valid.isFieldError("email"));
    }

    /**
     * email格式
     */
    @Test
    public void email() {
        MockForm form = new MockForm();
        form.setEmail("zxc");
        ValidationResult valid = FormValidator.isValid(form);
        System.out.println(valid);
        assertFalse(valid.isValid());
        assertTrue(valid.isFieldError("email"));
    }

    /**
     * password 正则表示式
     */
    @Test
    public void passwordregx() {
        MockForm form = new MockForm();
        form.setEmail("zxc@msun.com");
        form.setPassword("123456.");
        ValidationResult valid = FormValidator.isValid(form);
        System.out.println("只能数字字母" + valid);
        assertFalse(valid.isValid());
        assertTrue(valid.isFieldError("password"));
        // tc2
        form.setPassword("12345");
        valid = FormValidator.isValid(form);
        System.out.println("长度小了" + valid);
        assertFalse(valid.isValid());
        // tc 3
        assertTrue(valid.isFieldError("password"));
        form.setPassword("123456789012345678901");
        valid = FormValidator.isValid(form);
        System.out.println("长度大了" + valid);
        assertFalse(valid.isValid());
        assertTrue(valid.isFieldError("password"));
        // tc4正常了
        form.setPassword("hello1234");
        valid = FormValidator.isValid(form);
        System.out.println("正常" + valid);
        assertTrue(valid.isValid());
        assertFalse(valid.isFieldError("password"));
    }

    /**
     * 正常格式
     */
    @Test
    public void normal() {
        MockForm form = new MockForm();
        form.setEmail("zxc@msun.com");
        form.setPassword("hello1234");
        ValidationResult valid = FormValidator.isValid(form);
        System.out.println(valid);
        assertTrue(valid.isValid());
        assertFalse(valid.isFieldError("email"));
    }
}
