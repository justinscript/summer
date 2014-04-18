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

import junit.framework.TestCase;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * @author zxc Apr 13, 2013 11:01:33 PM
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestCaseInfo {

    Class<?> testFor() default TestCase.class;

    String basePath() default "";

    boolean defaultPath() default true;

    String pathPrefix() default "";

    String classSuffix() default "";

    boolean autoWire() default true;

    int autoWireMode() default AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    String contextKey() default "";

    boolean useDataSourceContextKey() default false;

    boolean useJdbcExtractor() default true;

    boolean defaultRollBack() default true;

    boolean dumpMessage() default false;
}
