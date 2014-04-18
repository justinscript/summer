/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.annotations;

/**
 * @author zxc Apr 12, 2013 4:13:20 PM
 */
public enum ValidationTokenEnum {

    /**
     * 普通的同步请求
     */
    WEB("webTokenCheck"),

    /**
     * 异步请求
     */
    AJAX("ajaxTokenCheck");

    private String methodName;

    private ValidationTokenEnum(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isWeb() {
        return this == WEB;
    }

    public boolean isAjax() {
        return this == AJAX;
    }
}
