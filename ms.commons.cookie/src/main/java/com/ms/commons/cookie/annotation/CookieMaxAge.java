/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.cookie.annotation;

/**
 * @author zxc Apr 12, 2014 7:36:50 PM
 */
public final class CookieMaxAge {

    /**
     * 用于临时Cookie的MaxAge
     */
    public static final int TEMP        = -1;
    /**
     * 用于永久Cookie的MaxAge
     */
    public static final int FOREVER     = Integer.MAX_VALUE;

    /**
     * 用于删除Cookie
     */
    public static final int OUT_OF_DATE = 0;
}
