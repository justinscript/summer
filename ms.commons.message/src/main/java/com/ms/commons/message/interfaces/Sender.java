/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.interfaces;

import com.ms.commons.message.cons.MessageTypeEnum;

/**
 * @author zxc Apr 13, 2014 10:40:51 PM
 */
public interface Sender {

    /**
     * Email和短信发送实现类
     * 
     * @param message
     */
    void send(Message message);

    /**
     * 是否能处理指定类型的MessageType
     * 
     * @return
     */
    boolean support(MessageTypeEnum messageType);

    /**
     * 具体实现着为调试Sender,如果为调试类，则可以将Message写入文件或其它的实现方式
     * 
     * @return
     */
    boolean is4Debug();
}
