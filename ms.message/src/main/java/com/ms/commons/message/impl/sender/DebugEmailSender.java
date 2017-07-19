/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.sender;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.impl.MessageSerivceException;
import com.ms.commons.message.impl.email.MsunMail;

/**
 * @author zxc Apr 13, 2014 10:43:32 PM
 */
public class DebugEmailSender extends DefaultEmailSender {

    // 日志记录器
    private static final ExpandLogger logger = LoggerFactoryWrapper.getLogger(DebugEmailSender.class);

    /**
     * 缺省构建器，从配置中心读数据
     * 
     * @param hostNameKey
     * @param userPwdKey
     */
    public DebugEmailSender(String hostNameKey, String userPwdKey) {
        super(hostNameKey, userPwdKey);
    }

    // Email的具体发送方法， 写到日志文件
    public void doSend(MsunMail msunEmail) throws MessageSerivceException {
        try {
            logger.info(msunEmail.dumpInfo());
            if (logger.isDebugEnabled()) {
                logger.debug("Email发送成功! " + msunEmail.dumpInfo());
            }
        } catch (Exception ex) {
            logger.error("Email发送失败! " + msunEmail.dumpInfo(), ex);
            throw new MessageSerivceException("Email发送失败!", ex);
        }
    }

    @Override
    public boolean is4Debug() {
        return true;
    }
}
