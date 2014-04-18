/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import com.ms.commons.config.interfaces.ConfigService;
import com.ms.commons.config.service.ConfigServiceLocator;

/**
 * 这个类是代理到com.ms.commons.security.EncryptUtils类上的
 * 
 * @author zxc Apr 12, 2013 10:53:52 PM
 */
public class EncryptUtils {

    private static final String SECRET_KEY;
    static {
        ConfigService configService = ConfigServiceLocator.getCongfigService();
        SECRET_KEY = configService.getKV(com.ms.commons.security.EncryptUtils.NISA_KEY, null);
        if (SECRET_KEY == null) {
            throw new RuntimeException("从NISA获取加密Key失败了！");
        }
    }

    public static String decrypt(String secretString) {
        return com.ms.commons.security.EncryptUtils.decrypt(secretString, SECRET_KEY);
    }

    public static String encrypt(String source) {
        return com.ms.commons.security.EncryptUtils.encrypt(source, SECRET_KEY);
    }
}
