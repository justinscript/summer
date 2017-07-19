/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.annotations;

/**
 * @author zxc Apr 12, 2013 4:13:08 PM
 */
public enum ViewEnum {
    /**
     * 直接定位到vm
     */
    VIEW,
    /**
     * forward跳转
     */
    FORWARD,
    /**
     * 重定向
     */
    REDIRECT;

    public boolean isView() {
        return this == VIEW;
    }

    public boolean isForward() {
        return this == FORWARD;
    }

    public boolean isRedirect() {
        return this == REDIRECT;
    }
}
