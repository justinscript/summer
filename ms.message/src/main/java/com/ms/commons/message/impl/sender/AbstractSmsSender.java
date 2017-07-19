/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.sender;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.cons.MessageConstants;
import com.ms.commons.message.cons.MessageTypeEnum;
import com.ms.commons.message.cons.SmsMsgSendType;
import com.ms.commons.message.impl.MessageSerivceException;
import com.ms.commons.message.interfaces.Message;
import com.ms.commons.message.interfaces.Sender;
import com.ms.commons.message.utils.MessageUtil;

/**
 * 短信发送实现类
 * 
 * @author zxc Apr 13, 2014 10:43:49 PM
 */
public abstract class AbstractSmsSender implements Sender {

    /**
     * Logger for this class
     */
    private static final ExpandLogger logger  = LoggerFactoryWrapper.getLogger(AbstractSmsSender.class);

    // 短信发送是否准备就绪，有第三方接口确定
    private boolean                   isReady = true;

    /**
     * constructor
     */
    public AbstractSmsSender() {
    }

    /*
     * 如果超过短信的长度，则分成几条发
     */
    public void send(Message message) {
        String content = message.getMessage();
        String[] to = message.getTo();

        if (StringUtils.isEmpty(content)) {
            logger.error("短信内容为空!");
            throw new MessageSerivceException("短信内容为空!");
        }

        // 如果服务未准备好，先初始化
        if (!isReady()) {
            try {
                // 初始化后，服务仍未准备好
                if (!isReady()) {
                    logger.error("邮件服务初始化异常!");
                    throw new MessageSerivceException("邮件服务初始化异常!");
                }
            } catch (Exception e) {
                logger.error("发送短信失败", e);
                throw new MessageSerivceException("邮件服务初始化异常");
            }
        }

        // 过滤掉未通过验证的信息接收着
        List<String> unqualifiedReceiver = message.getUnqualifiedReceiver();
        if (unqualifiedReceiver != null && unqualifiedReceiver.size() > 0) {
            for (int i = 0; i < to.length; i++) {
                if (unqualifiedReceiver.contains(to[i])) {
                    to[i] = null;
                }
            }
            // 剔除空元素
            to = MessageUtil.removeEmptyElement(to);
        }

        if (to == null || to.length <= 0) {
            // throw new IllegalArgumentException("信息接收人不能为空！");
            logger.error("发送短信给取消,短信接收人为空, 短信内容为<" + content + ">");
        }

        // 如果超过最大长度，则分成几条发送
        String[] msgs = MessageUtil.split(content, MessageConstants.MAX_LENGTH_PER_MSG);
        // 群发
        String receiver = MessageUtil.buildReceiver(to);
        try {
            for (String msg : msgs) {
                doSend(msg, receiver, message.getSmsMsgSendType());
            }
        } catch (Exception e) {
            logger.error("发送短信给<" + receiver + ">失败,发送内容[" + content + "].", e);
        }
        // 单发
        // for (int k = 0; k < to.length; k++) {
        // try {
        // for (String msg : msgs) {
        // doSend(msg, to[k]);
        // }
        // } catch (Exception e) {
        // logger.error("发送短信给<" + to[k] + ">失败,发送内容[" + content + "].", e);
        // }
        // }
    }

    /**
     * 判断各个参数是否正确初始化完毕
     * 
     * @return
     */
    private boolean isReady() {
        return isReady;
    }

    /**
     * 该方法仅仅为增加测试易用性所用
     * 
     * @param isReady
     */
    protected void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean support(MessageTypeEnum messageType) {
        return MessageTypeEnum.sms.equals(messageType);
    }

    /**
     * @param content
     * @param phoneNo
     * @return
     * @throws CheckException
     */
    public abstract String doSend(String content, String phoneNo, SmsMsgSendType smsMsgSendType)
                                                                                                throws MessageSerivceException;
}
