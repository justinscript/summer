/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.mina.server;

import org.apache.mina.core.session.IoSession;

import com.ms.commons.nisa.info.ClientInfo;

/**
 * @author zxc Apr 12, 2013 6:47:17 PM
 */
public class IoSessionInfo {

    private ClientInfo clientInfo;
    private long       registTime;
    private long       lastHeartbeatTime; // 最近一次心跳检查时间
    private IoSession  registSession;    // 长连接

    public IoSessionInfo(ClientInfo clientInfo, IoSession registSession, long registTime, long lastHeartbeatTime) {
        if (clientInfo == null) {
            throw new RuntimeException("The MinaMessage is Error! ClientKey is null !");
        }
        this.clientInfo = clientInfo;
        this.registSession = registSession;
        this.registTime = registTime;
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
        System.out.println("registTime=" + registTime);
        System.out.println("lastHeartbeatTime=" + lastHeartbeatTime);
    }

    /**
     * 返回最近一次接收Server端发送回来的心态时间
     * 
     * @return
     */
    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public long getRegistTime() {
        return registTime;
    }

    public void setRegistTime(long registTime) {
        this.registTime = registTime;
    }

    public IoSession getRegistSession() {
        return registSession;
    }
}
