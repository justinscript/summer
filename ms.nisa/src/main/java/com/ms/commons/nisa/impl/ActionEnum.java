/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.impl;

/**
 * @author zxc Apr 12, 2013 6:52:51 PM
 */
public enum ActionEnum {
    CLIENT_REGIST, // Client注册服务
    CLIENT_UNREGIST, // Client取消注册服务
    CLIENT_RETURN_RESULT, // Client处理完后返回结果
    SERVER_SEND_MESSAGE, // Server发送消息
    CACHE_SERVER_BROADCAST, // Cache更新通知。 服务器端广播通知
    CACHE_CLIENT_REGIST, // Cache的客户端向Nisa注册
    CACHE_CLIENT_NOTIFY, // Cache更新通知,Client处理完后返回结果
    CACHE_CLIENT_RETURN_RESULT, // Cache更新通知,Client处理完后返回结果
    HEARTBEAT, // Client端心跳检测
}
