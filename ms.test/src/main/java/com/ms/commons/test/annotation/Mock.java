/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.test.mock.impl.DefaultMockFilter;
import com.ms.commons.test.mock.inject.MockFilter;

/**
 * @author zxc Apr 13, 2013 11:03:37 PM
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mock {

    Class<? extends MockFilter> value() default DefaultMockFilter.class;

    String locators() default StringUtils.EMPTY;
}
