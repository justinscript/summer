/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.service;

import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.message.interfaces.MessageService;

/**
 * @author zxc Apr 13, 2014 10:40:38 PM
 */
public class MessageServiceLocator extends CommonServiceLocator {

    public static MessageService getMessageService() {
        return (MessageService) getBean("messageService");
    }
}
