/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.utilities;

import java.io.InputStream;

/**
 * @author zxc Apr 12, 2013 1:35:44 PM
 */
public class ResourcesUtilities {

    public static InputStream getResourceAsStream(String name, Class<?> clzz) {
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (inStream != null) {
            return inStream;
        }
        ClassLoader c = clzz.getClassLoader();
        if (c != null) {
            inStream = c.getResourceAsStream(name);
            if (inStream != null) {
                return inStream;
            }
        }
        return ClassLoader.getSystemResourceAsStream(name);
    }
}
