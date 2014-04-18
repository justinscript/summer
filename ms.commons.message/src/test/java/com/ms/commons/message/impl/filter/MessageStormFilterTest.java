/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.filter;

import org.junit.Assert;
import org.junit.Test;

import com.ms.commons.message.impl.email.MsunMail;
import com.ms.commons.message.interfaces.Message;

/**
 * @author zxc Apr 13, 2014 10:50:15 PM
 */
public class MessageStormFilterTest {

    @Test
    public void testDoFilterMessage() {
        MessageStormFilter filter = new MessageStormFilter();
        Message message = new TestMessage();
        for (int i = 0, size = filter.getMaxCount(); i < size; i++) {
            Assert.assertNull(filter.doFilter(message));
        }
        // 第5次失败
        Assert.assertNotNull(filter.doFilter(message));
    }

    @Test
    public void testDoFilterMail() {
        MessageStormFilter filter = new MessageStormFilter();
        MsunMail email = new MsunMail("support@msun.com", new String[] { "support@msun.com"/* "msun_01@163.com" */},
                                        "**MSUN内部测试邮件**", "--祝你工作愉快--");
        for (int i = 0, size = filter.getMaxCount(); i < size; i++) {
            Assert.assertNull(filter.doFilter(email));
        }
        // 第5次失败
        Assert.assertNotNull(filter.doFilter(email));
    }
}
