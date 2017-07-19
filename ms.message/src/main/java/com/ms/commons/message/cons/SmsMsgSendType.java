/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.cons;

/**
 * 信息发送类型
 * 
 * <pre>
 * 1)验证码通道
 * 2)普通群发
 * </pre>
 * 
 * @author zxc Apr 13, 2014 10:47:11 PM
 */
public enum SmsMsgSendType {

    /**
     * 验证码短信
     */
    ONTIME("ontime"),
    /**
     * 普通群发短信
     */
    NORMAL("normal");

    private String name;

    SmsMsgSendType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SmsMsgSendType getDefaultType() {
        return NORMAL;
    }
}
