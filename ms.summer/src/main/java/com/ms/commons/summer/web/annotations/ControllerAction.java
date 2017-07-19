/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller类中action方法的标记
 * 
 * @author zxc Apr 12, 2013 4:09:37 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ControllerAction {

    /**
     * 是否检查token，默认false
     * 
     * @return
     */
    boolean checkToken() default false;

    /**
     * 是否检查csrf，默认false
     * 
     * @return
     */
    boolean checkCSRF() default false;
}
