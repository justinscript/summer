/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas;

import java.io.File;

import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.impl.handler.BdbHandler;

/**
 * @author zxc Apr 12, 2013 6:35:07 PM
 */
public class LocalBdbUdasServiceTest extends AbstartUdasServiceTest {

    static final String PATH = System.getProperty("user.home") + "/" + ".testbdb";
    static final String NAME = PATH + "#test";

    static {
        File file = new File(PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    protected AbstractKVHandler[] gethandles() {
        return new AbstractKVHandler[] { new BdbHandler(NAME) };
    }
}
