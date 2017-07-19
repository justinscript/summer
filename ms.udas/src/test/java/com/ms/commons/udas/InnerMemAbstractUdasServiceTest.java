/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas;

import com.ms.commons.udas.impl.handler.AbstractKVHandler;
import com.ms.commons.udas.impl.handler.InnerMemHandler;

/**
 * @author zxc Apr 12, 2013 6:37:03 PM
 */
public class InnerMemAbstractUdasServiceTest extends AbstartUdasServiceTest {

    @Override
    protected AbstractKVHandler[] gethandles() {
        return new AbstractKVHandler[] { new InnerMemHandler() };
    }
}
