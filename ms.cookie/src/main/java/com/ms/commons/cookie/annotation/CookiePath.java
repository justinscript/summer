/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.cookie.annotation;

/**
 * @author zxc Apr 12, 2014 7:38:37 PM
 */
public enum CookiePath {
    /**
     * 泛子目录
     */
    ROOT("/"),

    ADMIN("/admin");

    private String path;

    private CookiePath(String path) {
        this.setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static CookiePath getEnum(String path) {
        for (CookiePath cookiePath : values()) {
            if (cookiePath.getPath().equals(path)) return cookiePath;
        }
        return null;
    }
}
