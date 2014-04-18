/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ms.commons.test.database.SecondaryPreareFilter;
import com.ms.commons.test.datareader.DataReaderType;

/**
 * @author zxc Apr 13, 2013 11:03:15 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Prepare {

    String methodName() default "";

    DataReaderType type() default DataReaderType.None;

    boolean autoImport() default false;

    String importTables() default "";

    boolean autoClear() default false;

    boolean forceClear() default false;

    boolean newThreadTransactionImport() default false;

    String cnStringEncoding() default "";

    String utf8StringEncoding() default "";

    boolean autoClearExistsData() default false;

    String primaryKey() default "";

    boolean autoClearImportDataOnFinish() default false;

    boolean autoFormatFieldName() default true;

    boolean autoImportCommonData() default true;

    Class<? extends SecondaryPreareFilter> secondaryPrepareFilter() default SecondaryPreareFilter.class;
}
