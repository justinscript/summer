/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.service;

import org.junit.Test;

import com.ms.commons.message.impl.email.MsunMail;
import com.ms.commons.message.interfaces.Message;
import com.ms.commons.message.interfaces.MessageService;
import com.ms.commons.message.utils.MessageUtil;

/**
 * @author zxc Apr 13, 2014 10:48:24 PM
 */
// @TestCaseInfo(contextKey = "messageService2", classSuffix = "Data", defaultRollBack = true)
public class MessageServiceTest /** extends BaseTestCase **/
{

    private MessageService messageService2;

    public void setMessageService2(MessageService messageService2) {
        this.messageService2 = messageService2;
    }

    @Test
    public void testSendEmail() {
        // MessageService messageService = new MessageServiceImpl();
        // messageService2 = MessageServiceLocator.getMessageService();
        String htmlMessage = "test";
        Message message = new MsunMail("support@musn.com"/* "msun_01@163.com" */, new String[] { "zxc@msun.com" },
                                       "MSUN内部测试邮件", htmlMessage);
        messageService2.send(message);
    }

    @SuppressWarnings("unused")
    @Test
    public void testBuildReceiver() {
        String[] to = new String[] { "13621603604", "18606232372", "18606232435" };
        String receiver = MessageUtil.buildReceiver(to);

        String expectedResult = "13621603604;18606232372;18606232435";
        // assertEquals(expectedResult, receiver);
    }
}
