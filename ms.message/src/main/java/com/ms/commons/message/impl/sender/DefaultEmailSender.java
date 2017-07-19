/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.sender;

import org.apache.commons.mail.Email;

import com.ms.commons.nisa.interfaces.ConfigService;
import com.ms.commons.nisa.listener.ConfigListener;
import com.ms.commons.nisa.service.ConfigServiceLocator;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.message.impl.MessageSerivceException;
import com.ms.commons.message.impl.email.MsunMail;

/**
 * @author zxc Apr 13, 2014 10:42:46 PM
 */
public class DefaultEmailSender extends AbstractEmailSender {

    /**
     * 日志记录器
     */
    private static final ExpandLogger logger = LoggerFactoryWrapper.getLogger(DefaultEmailSender.class);

    /**
     * 缺省构建器，从配置中心读数据
     * 
     * @param hostNameKey
     * @param userPwdKey
     */
    public DefaultEmailSender(String hostNameKey, String userPwdKey) {
        hostName = ConfigServiceLocator.getCongfigService().getKV(hostNameKey, "");
        String[] userPwd = ConfigServiceLocator.getCongfigService().getKVStringArray(userPwdKey);
        user = userPwd[0];
        password = userPwd[1];
    }

    /*
     * 真正发送Mail
     */
    public void doSend(MsunMail msunEmail) throws MessageSerivceException {
        try {
            Email email = getEmail(msunEmail);
            email.send();
            // 输出日志信息
            logger.info("Email发送成功! " + msunEmail.dumpInfo());
        } catch (Exception ex) {
            logger.error("Email发送失败! " + msunEmail.dumpInfo(), ex);
            ex.printStackTrace();
            throw new MessageSerivceException("Email发送失败!", ex);
        }
    }

    @Override
    public boolean is4Debug() {
        return false;
    }
}
