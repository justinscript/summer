/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.interfaces;

import com.ms.commons.message.impl.MessageSerivceException;

/**
 * @author zxc Apr 13, 2014 10:41:02 PM
 */
public interface MessageService {

    /**
     * Email和短信对外接口
     * 
     * @param message 如果是发送Email，调用着需要构造MsunEmail，MsunEmail实现了Message接口，如果是发送短信，则调用者需要构造MobileMesage对象
     * @throws MessageSerivceException 如果发送失败或传入的参数不正确，在绝大多数情况下会抛出MessageServiceException异常
     */
    void send(Message message) throws MessageSerivceException;
}
