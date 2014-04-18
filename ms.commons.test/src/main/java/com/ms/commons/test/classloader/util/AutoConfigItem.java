/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.util;

import java.net.URL;

/**
 * @author zxc Apr 13, 2013 11:09:34 PM
 */
public class AutoConfigItem {

    URL    autoConfig;

    String template;

    String destfile;

    String charset;

    public AutoConfigItem(URL autoConfig, String template, String destfile, String charset) {
        this.autoConfig = autoConfig;
        this.template = template;
        this.destfile = destfile;
        this.charset = charset;
    }

    public URL getAutoConfig() {
        return autoConfig;
    }

    public String getTemplate() {
        return template;
    }

    public String getDestfile() {
        return destfile;
    }

    public String getCharset() {
        return charset;
    }
}
