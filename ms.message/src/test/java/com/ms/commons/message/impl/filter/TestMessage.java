/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.interfaces.Message;

/**
 * @author zxc Apr 13, 2014 10:50:59 PM
 */
public class TestMessage implements Message {

    private static final long serialVersionUID = 7443633311476062029L;

    @Test
    public void testmessage() {
        Assert.assertTrue(true);
    }

    private List<String>   unqualifiedReceiver = new ArrayList<String>();
    private SmsMsgSendType smsMsgSendType;

    public String getSubject() {
        return "##MSUN内部短信测试信息##";
    }

    @Override
    public String getMessage() {
        return "--祝您工作愉快！--";
    }

    @Override
    public String[] getTo() {
        return new String[] { "support@msun.com" };
    }

    @Override
    public String getFrom() {
        return "support@msun.com";
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.email;
    }

    @Override
    public MsgKey[] identity() {
        MsgKey[] msgKeys = new MsgKey[1];
        msgKeys[0] = new MsgKey(getTo()[0], getMessage(), getMessage().length());
        return msgKeys;
    }

    @Override
    public String dumpInfo() {
        return "**email storm filer testing**";
    }

    @Override
    public String[] getAllReceiver() {
        return new String[] { "" };
    }

    @Override
    public void setUnqualifiedReceiver(List<String> list) {
        unqualifiedReceiver = list;
    }

    @Override
    public List<String> getUnqualifiedReceiver() {
        return unqualifiedReceiver;
    }

    @Override
    public void setTestKey(String key) {
    }

    @Override
    public String getTestKey() {
        return null;
    }

    @Override
    public void setSmsMsgSendType(SmsMsgSendType smsMsgSendType) {
        this.smsMsgSendType = smsMsgSendType;
    }

    @Override
    public SmsMsgSendType getSmsMsgSendType() {
        if (smsMsgSendType == null) {
            return SmsMsgSendType.getDefaultType();
        }
        return smsMsgSendType;
    }
}
