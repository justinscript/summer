/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.message.cons;

/**
 * 系统邮件发送者常量类
 * 
 * @author zxc Apr 13, 2014 10:47:39 PM
 */
public final class MessageConstants {

    public static final String FROM_SERVICE_EMAIL   = "service@msun.com";

    // 判断消息是否重复时所需计算的字符最大长度
    // 信息发送内容，如果是短信，则是消息内容，如果是Email，目前暂且判断主题和主题内容--如果内容过长，则取前80个字符
    public static final int    MAX_LENGTH           = 90;

    // 短信服务商运行每条短信内容的最大长度
    public static final int    MAX_LENGTH_PER_MSG   = 240;

    // 每条短信的最大字符长度
    public static final int    MAX_TEXT_MSG_LENGTH  = 240;

    public static final String SMS_URL_ID           = null;

    public static final String SMS_CPID_NAME_ID     = null;

    public static final String SMS_CPID_VALUE_ID    = null;

    public static final String SMS_PWD_NAME_ID      = null;

    public static final String SMS_PWD_VALUE_ID     = null;

    public static final String SMS_PID_NAME_ID      = null;

    public static final String SMS_PID_VALUE_ID     = null;

    public static final String SMS_PHONE_NAME_ID    = null;

    public static final String SMS_MSG_NAME_ID      = null;

    public static final String SMS_MSG_MAXLENGTH_ID = null;
}
