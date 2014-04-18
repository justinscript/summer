/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zxc Apr 12, 2013 4:13:31 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidationToken {

    /**
     * 检查类型
     * 
     * @see ValidationTokenEnum
     */
    ValidationTokenEnum type() default ValidationTokenEnum.WEB;

    /**
     * 执行的方法名
     */
    String methodName() default "";

    /**
     * 如果是web请求(正常的同步请求) content为要跳转的url<br>
     * 如果是ajax请求 content为返回的json内容
     */
    String content();

    /**
     * 定位方式
     * 
     * @see ViewEnum
     */
    ViewEnum viewType() default ViewEnum.VIEW;
}
