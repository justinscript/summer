/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.app.web.commons.valve;

import java.io.Serializable;

/**
 * @author zxc Apr 12, 2013 11:12:46 PM
 */
public class BaseWebUser implements Serializable {

    private static final long serialVersionUID = 2651609333764931368L;
    private boolean           hasLogin;

    public boolean hasLogin() {
        return hasLogin;
    }

    public void setHasLogin(boolean hasLogin) {
        this.hasLogin = hasLogin;
    }

    public boolean isHasLogin() {
        return hasLogin;
    }

    public static BaseWebUser getCurrentUser() {
        return null;
    }
}
