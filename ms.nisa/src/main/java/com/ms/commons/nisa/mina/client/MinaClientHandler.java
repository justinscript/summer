/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.mina.client;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.nisa.impl.ActionEnum;
import com.ms.commons.nisa.impl.CacheRegistServiceImpl;
import com.ms.commons.nisa.impl.ConfigServiceImpl;
import com.ms.commons.nisa.impl.MinaMessage;
import com.ms.commons.nisa.info.NotifyInfo;
import com.ms.commons.nisa.interfaces.CacheRegistService;
import com.ms.commons.nisa.interfaces.ConfigService;
import com.ms.commons.nisa.listener.CacheUpdateListener;
import com.ms.commons.nisa.service.CacheRegistServiceLocator;
import com.ms.commons.nisa.service.ConfigServiceLocator;

/**
 * 接收MinaServer端返回的消息
 * 
 * @author zxc Apr 12, 2013 6:47:32 PM
 */
public class MinaClientHandler extends IoHandlerAdapter {

    private ExpandLogger log = LoggerFactoryWrapper.getLogger(MinaClientHandler.class);

    private MinaMessage  msg;
    private IoSession    ioSession;
    // 上次心跳检查时间
    private long         lastHeartbeatTime;
    // 判断是否接收了Nisa的数据
    private boolean      hasReceiver;

    public MinaClientHandler(MinaMessage message) {
        this.msg = message;
        this.lastHeartbeatTime = System.currentTimeMillis();
        hasReceiver = false;
    }

    public boolean isConnected() {
        if (ioSession != null) {
            if (ioSession.isConnected() && !hasReceiver) {
                log.error("pan--#--- Nisa Client has connected ! but not receiver message !");
            }
            return hasReceiver && ioSession.isConnected();
        }
        return false;
    }

    public ConfigService getCongfigService() {
        return ConfigServiceLocator.getCongfigService();
    }

    public CacheRegistService getCacheRegistService() {
        return CacheRegistServiceLocator.getCacheRegistService();

    }

    public void sessionOpened(IoSession session) throws Exception {
        // System.out.println("我来了..");
        msg.setAction(ActionEnum.CLIENT_REGIST);
        session.write(msg);
        ioSession = session;
    }

    public void sessionClosed(IoSession session) throws Exception {
        msg.setAction(ActionEnum.CLIENT_UNREGIST);
        session.write(msg);
        // System.out.println("我走喽");
    }

    /**
     * 接收Server端发来的请求
     */
    public void messageReceived(IoSession session, Object message) throws Exception {
        lastHeartbeatTime = System.currentTimeMillis();
        MinaMessage recevierMessage = (MinaMessage) message;
        if (recevierMessage != null && recevierMessage.getAction() == ActionEnum.SERVER_SEND_MESSAGE) {
            if (ConfigServiceImpl.instance != null) {
                long t1 = System.currentTimeMillis();
                ConfigServiceImpl.instance.deal(recevierMessage);
                hasReceiver = true;
                System.out.println("处理完的结果为" + recevierMessage.getRemark());
                long t2 = System.currentTimeMillis();
                MinaMessage newMsg = new MinaMessage(msg.getClientInfo());
                newMsg.setAction(ActionEnum.CLIENT_RETURN_RESULT);
                newMsg.setRemark("收到消息，处理耗时" + (t2 - t1) + "ms");
                session.write(newMsg);
            }
        } else if (recevierMessage != null && recevierMessage.getAction() == ActionEnum.CACHE_SERVER_BROADCAST) {
            if (getCacheRegistService() != null) {
                long t1 = System.currentTimeMillis();
                ((CacheRegistServiceImpl) getCacheRegistService()).deal(recevierMessage);
                System.out.println("处理完的结果为" + recevierMessage.getRemark());
                long t2 = System.currentTimeMillis();
                MinaMessage newMsg = new MinaMessage(msg.getClientInfo());
                newMsg.setAction(ActionEnum.CACHE_CLIENT_RETURN_RESULT);
                newMsg.setRemark("Cache更新已经结束，处理耗时" + (t2 - t1) + "ms");

                HashMap<String, Serializable> paramMap = recevierMessage.getParamMap();
                if (paramMap != null && !paramMap.isEmpty()) {
                    Serializable obj = paramMap.get(CacheUpdateListener.KEY);
                    if (obj instanceof NotifyInfo) {
                        ((NotifyInfo) obj).setUpdaeValue(null); // 把原先的值清掉
                        newMsg.setParamMap(paramMap);
                    }
                }
                session.write(newMsg);
            }
        }
    }

    /**
     * 把NotifyInfo的内容告诉Nisa配置中心，让Nisa配置中心通知所有相似应用
     * 
     * @param value
     * @throws Exception
     */
    public void sentNotifyInfoMessage(ActionEnum actionEnum, NotifyInfo value) throws Exception {
        MinaMessage newMsg = new MinaMessage(msg.getClientInfo());
        newMsg.setAction(actionEnum);
        newMsg.setRemark("Cache通知更新，Group=" + value.getGroup() + " key=" + value.getSourceIpKey() + " ClientKey="
                         + msg.getClientInfo().toString());
        HashMap<String, Serializable> paramMap = new HashMap<String, Serializable>();
        paramMap.put(CacheUpdateListener.KEY, value);
        newMsg.setParamMap(paramMap);
        if (ioSession != null) {
            ioSession.write(newMsg);
        }
    }

    /**
     * 发送心跳检测消息
     * 
     * @param value
     * @throws Exception
     */
    public void sentHeartbeatMessage() throws Exception {
        MinaMessage newMsg = new MinaMessage(msg.getClientInfo());
        newMsg.setAction(ActionEnum.HEARTBEAT);
        if (ioSession != null) {
            ioSession.write(newMsg);
        }
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        // session.write(strC);
        // super.messageSent(session, message);
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
        session.close(true);
    }

    /**
     * 返回最近一次接收Server端发送回来的心态时间
     * 
     * @return
     */
    public long getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }
}
