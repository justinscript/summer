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

import com.ms.commons.test.datareader.DataReaderType;

/**
 * @author zxc Apr 13, 2013 11:02:12 PM
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrepareInfo {

    /**
     * @see Prepare#type()
     * @return
     */
    DataReaderType type() default DataReaderType.None;

    /**
     * @see Prepare#cnStringEncoding()
     * @return
     */
    String cnStringEncoding() default "";

    /**
     * @see Prepare#utf8StringEncoding()
     * @return
     */
    String utf8StringEncoding() default "";

    /**
     * @see Prepare#primaryKey()
     * @return
     */
    String primaryKey() default "";
}
