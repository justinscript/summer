/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个数据项的配置信息
 * 
 * @author zxc Apr 12, 2013 11:18:53 PM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidationInfo {

    /**
     * 预先配置好的校验器ID，注意先后顺序,第一个出错，整个校验就会返回
     */
    String[] validators();

    /**
     * 该配置项的Key，这个Key很关键，页面中是通过这个key来判断是否出错的
     */
    String key();

    /**
     * 配置象的名字，现在显示出错信息时可能有用。
     */
    String displayName() default "";
}
