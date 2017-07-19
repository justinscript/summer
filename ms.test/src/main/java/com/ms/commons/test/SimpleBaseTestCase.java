/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test;

import junit.framework.TestCase;

import org.junit.runner.RunWith;

import com.ms.commons.test.classloader.IntlTestURLClassPath;
import com.ms.commons.test.integration.jmockit.internal.JMockItUtil;
import com.ms.commons.test.integration.junit4.internal.IntlTestBlockJUnit4ClassRunner;

/**
 * @author zxc Apr 13, 2013 11:16:05 PM
 */
@RunWith(IntlTestBlockJUnit4ClassRunner.class)
public class SimpleBaseTestCase extends TestCase {

    static {
        IntlTestURLClassPath.initIntlTestURLClassLoader();
        JMockItUtil.startUpJMockItIfPossible();
    }
}
