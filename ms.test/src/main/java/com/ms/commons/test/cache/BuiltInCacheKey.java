/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.cache;

/**
 * @author zxc Apr 13, 2013 11:12:35 PM
 */
public enum BuiltInCacheKey {

    Prepare("prepare"),

    Result("result"),

    Method("_method_"),

    Finally("_finally_"),

    SecondarySetting("_secondary_setting_");

    private BuiltInCacheKey(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return getValue();
    }

    public static BuiltInCacheKey make(String value) {
        for (BuiltInCacheKey status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
