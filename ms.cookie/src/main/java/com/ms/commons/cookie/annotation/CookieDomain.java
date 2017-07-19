/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.cookie.annotation;

/**
 * @author zxc Apr 12, 2014 7:36:07 PM
 */
public enum CookieDomain {

    /**
     * musn官网的cookie .msun.com加版本号
     */
    DOT_MSUN_COM(".msun.com"),
    /**
     * musn官网的cookie；这个以后就是www.msun.com
     */
    WWW_MSUN_COM("www.msun.com");

    private String domain;

    private CookieDomain(String cookieDomain) {
        this.domain = cookieDomain;
    }

    public String getDomain() {
        return domain;
    }

    public static CookieDomain getEnum(String domain) {
        for (CookieDomain cookieDomain : values()) {
            if (cookieDomain.getDomain().equals(domain)) return cookieDomain;
        }
        return null;
    }
}
