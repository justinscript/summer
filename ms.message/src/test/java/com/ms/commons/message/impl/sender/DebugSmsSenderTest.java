/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.sender;

import junit.framework.Assert;

import org.junit.Test;

import com.ms.commons.message.impl.sms.MobileMessage;

/**
 * @author zxc Apr 13, 2014 10:50:05 PM
 */
@SuppressWarnings("static-access")
public class DebugSmsSenderTest {

    @Test
    public void testSender_1() {
        String from = "13621603604";
        String[] to = { "13621603604" };
        String content = "##this is a test##";
        MobileMessage message = new MobileMessage(from, to, content);
        DebugSmsSender sender = new DebugSmsSender();
        sender.IS_UNIT_TEST = true;
        try {
            sender.send(message);
            Assert.assertEquals(content, sender.getMsgHistory(to[0]));
        } catch (Exception e) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSender_2() {
        String from = "13621603604";
        String[] to = { "13621603604" };
        String content = null;
        MobileMessage message = new MobileMessage(from, to, content);
        DebugSmsSender sender = new DebugSmsSender();
        sender.IS_UNIT_TEST = true;
        sender.send(message);
        Assert.assertEquals(content, sender.getMsgHistory(to[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSender_3() {
        String from = "13621603604";
        String[] to = { "1362160360x" };
        String content = null;
        MobileMessage message = new MobileMessage(from, to, content);
        DebugSmsSender sender = new DebugSmsSender();
        sender.send(message);
        Assert.assertNull(sender.getMsgHistory(to[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSender_4() {
        String from = "13621603604";
        String[] to = { "13621603604" };
        String content = null;
        MobileMessage message = new MobileMessage(from, to, content);
        DebugSmsSender sender = new DebugSmsSender();
        sender.IS_UNIT_TEST = true;
        sender.send(message);
        Assert.assertNull(sender.getMsgHistory(to[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSender_5() {
        String from = "13621603604";
        String[] to = { "13621603604" };
        String content = "A journey of a thousand miles begins with the first step! A journey of a thousand miles begins with the first step! A journey of a thousand miles begins with the first step! A journey of a thousand miles begins with the first step! A journey of a thousand miles begins with the first step! A journey of a thousand miles begins with the first step! ";
        MobileMessage message = new MobileMessage(from, to, content);
        // AbstractSmsSender sender = (AbstractSmsSender) AbstractSmsSender.getInstance();
        DebugSmsSender sender = new DebugSmsSender();
        sender.IS_UNIT_TEST = true;
        try {
            sender.send(message);
            Assert.assertEquals(content, sender.getMsgHistory(to[0]));
        } catch (Exception e) {
        }
    }
}
