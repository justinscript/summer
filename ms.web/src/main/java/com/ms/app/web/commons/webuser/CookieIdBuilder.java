/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.webuser;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ms.commons.cookie.CookieKeyEnum;
import com.ms.commons.cookie.manager.CookieManager;

/**
 * 用来生成CookieId
 * 
 * @author zxc Apr 12, 2013 11:05:52 PM
 */
class CookieIdBuilder {

    private static final Lock lock = new ReentrantLock();

    /**
     * 获取或者生成新的CookieId
     * 
     * <pre>
     * 如果当前Cookie中CookieID存在，直接返回
     * 如果没有则创建一个，同时保存到Cookie当中去
     * </pre>
     * 
     * @return 返回当前或者最新的CookieId
     */
    public static String createCookieId(CookieManager cookieManager) {
        try {
            lock.lock();
            String cookieId = getCookieId(cookieManager);
            if (cookieId == null) {
                cookieId = create();
                cookieManager.set(CookieKeyEnum.cookie_id, cookieId);
            }
            return cookieId;
        } finally {
            lock.unlock();
        }
    }

    public static String getCookieId(CookieManager cookieManager) {
        return cookieManager.get(CookieKeyEnum.cookie_id);
    }

    public static String create() {
        String s = UUID.randomUUID().toString();
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }
}
