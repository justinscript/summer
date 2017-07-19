/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.sender;

import junit.framework.Assert;

import org.junit.Test;

import com.ms.commons.message.impl.email.MsunMail;
import com.ms.commons.message.interfaces.Message;

/**
 * @author zxc Apr 13, 2014 10:49:13 PM
 */
public class EmailSenderTest {

    class InnerEmailSender extends DefaultEmailSender {

        private boolean success;

        public boolean isSuccess() {
            return success;
        }

        protected InnerEmailSender() {
            super("S_commons.message.hostName", "SA_ms.commons.message.userPwd");
            hostName = "192.168.1.190";
            auth = true;
            user = "support@msun.com";
            password = "123456";
        }

        public void send(Message message) {
            try {
                super.send(message);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
    }

    @Test
    public void testSend() {
        InnerEmailSender emailSender = new InnerEmailSender();
        String from = "support@msun.com";
        String[] to = new String[] { "support@msun.com" };
        String subject = "==MSUN内部测试邮件==" + Math.random();
        String htmlMessage = "<html><head></head><body><h1>Congratuation!</h1></body></html>";

        MsunMail message = new MsunMail(from, to, subject, htmlMessage);
        emailSender.send(message);
        Assert.assertTrue(emailSender.isSuccess());
    }
}
