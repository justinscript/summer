/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.interfaces;

import java.io.Serializable;
import java.util.List;

import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.impl.filter.MsgKey;

/**
 * Message 标记接口
 * 
 * @author zxc Apr 13, 2014 10:41:12 PM
 */
public interface Message extends Serializable {

    /**
     * 消息的主题
     * 
     * @return
     */
    String getSubject();

    /**
     * 待发送消息到内容
     * 
     * @param msgType 待发送消息到类型
     * @return
     */
    String getMessage();

    /**
     * 消息最终接收着
     * 
     * @return
     */
    String[] getTo();

    /**
     * 邮件的发送者
     * 
     * @return
     */
    String getFrom();

    /**
     * 要发送到Message的类型，目前支持Email和SMS
     * 
     * @return
     */
    MessageTypeEnum getMessageType();

    /**
     * 打印出与调试相关的信息
     * 
     * @return
     */
    String dumpInfo();

    /**
     * 消息的标志符号,用来排除发送多次相同信息 如果是SMS，可以是手机号与Content的组合键，如果是Email，则可以是To[Cc，Bcc]，Subject和Content
     * 
     * @return
     */
    MsgKey[] identity();

    /**
     * 信息的所有接收人，如果信息是Email,则包括To，Cc，以及Bcc
     * 
     * @return
     */
    String[] getAllReceiver();

    /**
     * 用来存储未通过验证的信息接收着
     * 
     * @param list
     */
    void setUnqualifiedReceiver(List<String> list);

    /**
     * 返回未通过验证的信息接收着
     * 
     * @return
     */
    List<String> getUnqualifiedReceiver();

    /**
     * 辅助测试Key值，不能为空，请确保Key值的唯一行，目前做如下约定
     * 
     * <pre>
     * 1)Email，客服端IP+邮件模版 
     * 2)Message,客户端IP+接收人
     * </pre>
     * 
     * @param key
     */
    void setTestKey(String key);

    /**
     * 返回测试的Key值
     * 
     * @return
     */
    String getTestKey();

    /**
     * 设置Sms短信发送类型
     * 
     * @param smsMsgSendType
     */
    void setSmsMsgSendType(SmsMsgSendType smsMsgSendType);

    /**
     * 查询smsMsg短信发送类型
     * 
     * @return
     */
    SmsMsgSendType getSmsMsgSendType();
}
