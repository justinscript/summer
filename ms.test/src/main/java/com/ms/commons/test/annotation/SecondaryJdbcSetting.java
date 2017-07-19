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

import com.ms.commons.test.database.JdbcManagementToolBuilder;
import com.ms.commons.test.database.NonSecondaryPreareFilterImpl;
import com.ms.commons.test.database.SecondaryPreareFilter;

/**
 * @author zxc Apr 13, 2013 11:01:52 PM
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecondaryJdbcSetting {

    String driver() default "";

    String url() default "";

    String username() default "";

    String password() default "";

    Class<? extends SecondaryPreareFilter> secondaryPrepareFilter() default NonSecondaryPreareFilterImpl.class;

    Class<? extends JdbcManagementToolBuilder> jdbcManagementToolBuilder() default JdbcManagementToolBuilder.class;
}
