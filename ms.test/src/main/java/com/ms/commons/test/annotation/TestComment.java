/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zxc Apr 13, 2013 11:01:23 PM
 */
@Target({ ElementType.LOCAL_VARIABLE, ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR,
         ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.SOURCE)
public @interface TestComment {

    String value();
}
