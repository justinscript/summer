/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.impl.sms;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.cons.MessageConstants;
import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.impl.filter.MsgKey;
import com.ms.commons.message.interfaces.Message;
import com.ms.commons.message.utils.MessageUtil;
import com.ms.commons.utilities.CoreUtilities;

/**
 * @author zxc Apr 13, 2014 10:40:29 PM
 */
public class MobileMessage implements Message {

    /**
     * 序列化ID
     */
    private static final long         serialVersionUID = -6198947274426776937L;

    /**
     * Logger for this class
     */
    private static final ExpandLogger logger           = LoggerFactoryWrapper.getLogger(MobileMessage.class);

    /**
     * 信息发送人，值为手机号码
     */
    private String                    from;
    /**
     * 信息接收着，值为手机号码
     */
    private String[]                  to;
    /**
     * 信息内容
     */
    private String                    message;

    /**
     * 未能通过验证的信息接收着
     */
    private List<String>              unqualifiedReceiver;

    /**
     * 辅助自动化测试的字段，对业务逻辑没有影响
     */
    private String                    testKey;

    /**
     * 缺省为普通短信
     */
    private SmsMsgSendType            smsMsgSendType   = SmsMsgSendType.getDefaultType();

    /**
     * @param from
     * @param to
     * @param message
     */
    public MobileMessage(String from, String[] to, String message) {
        this(from, to, message, CoreUtilities.getIPAddress() + "_sms_key_", SmsMsgSendType.getDefaultType());
    }

    public MobileMessage(String from, String[] to, String message, SmsMsgSendType smsMsgSendType) {
        this(from, to, message, CoreUtilities.getIPAddress() + "_sms_key_", smsMsgSendType);
    }

    public MobileMessage(String from, String[] to, String message, String testKey) {
        this(from, to, message, testKey, SmsMsgSendType.getDefaultType());
    }

    /**
     * 缺省构建器
     * 
     * @param from 信息发送人
     * @param to 信息接收着
     * @param message 信息内容, 信息的长度不能超过210
     * @param testKey 可以传入客户端IP
     */
    public MobileMessage(String from, String[] to, String message, String testKey, SmsMsgSendType smsMsgSendType) {
        this.from = from;
        if (to == null || to.length < 1) {
            throw new IllegalArgumentException("信息接收人不能为空！");
        }

        /**
         * 过滤不争取的手机号码
         */
        for (int i = 0; i < to.length; i++) {
            if (!MessageUtil.isValidateMobileNumber(to[i]) || StringUtils.isEmpty(to[i])) {
                logger.warn("手机号码<" + to[i] + "<输入有误，将会忽略!");
                to[i] = null;
            }
        }

        this.message = StringUtils.trimToEmpty(message);
        if (message == null || message.length() > MessageConstants.MAX_TEXT_MSG_LENGTH) {
            throw new IllegalArgumentException("Message不能为空，单条信息的长度不能超过" + MessageConstants.MAX_TEXT_MSG_LENGTH + "个字符");
        }

        this.to = MessageUtil.removeEmptyElement(to);
        this.testKey = testKey;
        this.smsMsgSendType = smsMsgSendType;
    }

    public MsgKey[] identity() {
        int count = to.length;
        MsgKey[] msgKeys = new MsgKey[count];
        for (int i = 0; i < msgKeys.length; i++) {
            msgKeys[i] = new MsgKey(to[i], getMessage(), getMessage().length());
        }
        return msgKeys;
    }

    public String getSubject() {
        // 短信通常没有主题
        return "";
    }

    public String getMessage() {
        return message;
    }

    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.sms;
    }

    public String[] getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String[] getAllReceiver() {
        return getTo();
    }

    public String dumpInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageType=" + getMessageType());
        sb.append(";from=");
        sb.append(this.getFrom());
        sb.append(";to=");
        sb.append(StringUtils.join(this.getTo(), ","));
        sb.append(";message=" + getMessage());
        return sb.toString();
    }

    @Override
    public void setUnqualifiedReceiver(List<String> list) {
        unqualifiedReceiver = list;
    }

    @Override
    public List<String> getUnqualifiedReceiver() {
        return unqualifiedReceiver;
    }

    // //////////////////////////////////////////////////////////////////////////////
    //
    // 辅助Web自动化测试方法
    //
    // //////////////////////////////////////////////////////////////////////////////
    @Override
    public void setTestKey(String key) {
        this.testKey = key;

    }

    @Override
    public String getTestKey() {
        return testKey;
    }

    @Override
    public void setSmsMsgSendType(SmsMsgSendType smsMsgSendType) {
        this.smsMsgSendType = smsMsgSendType;
    }

    @Override
    public SmsMsgSendType getSmsMsgSendType() {
        if (smsMsgSendType == null) {
            return SmsMsgSendType.getDefaultType();
        } else {
            return smsMsgSendType;
        }
    }
}
