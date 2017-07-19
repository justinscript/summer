/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.mina.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ms.commons.nisa.impl.ActionEnum;
import com.ms.commons.nisa.impl.MinaMessage;
import com.ms.commons.nisa.info.CacheGroupInfo;
import com.ms.commons.nisa.info.CacheGroupInfo.EachClient;
import com.ms.commons.nisa.info.ClientInfo;
import com.ms.commons.nisa.info.NotifyInfo;
import com.ms.commons.nisa.listener.CacheUpdateListener;
import com.ms.commons.nisa.service.ConfiginfoService;
import com.ms.commons.utilities.CoreUtilities;

/**
 * 处理Mina客户端发来的请求
 * 
 * @author zxc Apr 12, 2013 6:45:54 PM
 */
public class MinaServerHandler extends IoHandlerAdapter {

    /**
     * 保持每个Client过来的连接
     */
    private ConcurrentHashMap<ClientInfo, IoSessionInfo> sessionMap    = new ConcurrentHashMap<ClientInfo, IoSessionInfo>();
    /**
     * 保存Cache的Group组
     */
    private ConcurrentHashMap<String, CacheGroupInfo>    cacheGroupMap = new ConcurrentHashMap<String, CacheGroupInfo>();
    private ConfiginfoService                            configinfoService;

    private MinaCallback                                 callBack;

    public MinaServerHandler(MinaCallback callBack, ConfiginfoService configinfoService) {
        this.callBack = callBack;
        this.configinfoService = configinfoService;
    }

    public ConcurrentHashMap<String, CacheGroupInfo> getCacheGroupMap() {
        return cacheGroupMap;
    }

    /**
     * 返回所有已经在Server端注册的Client
     * 
     * @return
     */
    public List<IoSessionInfo> getRegistClientList() {
        List<IoSessionInfo> list = new ArrayList<IoSessionInfo>();
        if (sessionMap != null) {
            Iterator<ClientInfo> ir = sessionMap.keySet().iterator();
            while (ir.hasNext()) {
                ClientInfo key = ir.next();
                IoSessionInfo value = sessionMap.get(key);
                if (value != null) {
                    IoSessionInfo tmp = new IoSessionInfo(value.getClientInfo(), null, value.getRegistTime(),
                                                          value.getLastHeartbeatTime());
                    list.add(tmp);
                }
            }
        }
        return list;
    }

    private void log(String msg) {
        System.out.println(msg);
        if (callBack != null) {
            callBack.notifyAction(msg);
        }
    }

    /****
     * session打开时,调用
     */
    public void sessionOpened(IoSession session) throws Exception {
        log(session.getRemoteAddress() + " session打开时,调用");
    }

    /***
     * 连接关才时调用
     */
    public void sessionClosed(IoSession session) throws Exception {
        log(session.getRemoteAddress() + " 关闭client");
        if (sessionMap != null) {
            Iterator<ClientInfo> ir = sessionMap.keySet().iterator();
            while (ir.hasNext()) {
                ClientInfo key = ir.next();
                IoSessionInfo value = sessionMap.get(key);
                if (value != null && value.getRegistSession() != null && value.getRegistSession() == session) {
                    sessionMap.remove(key);
                    removeClientFromCacheGroupMap(key);
                    return;
                }
            }
        }
    }

    /**
     * 如果出异常,就关闭session
     */
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
        log(CoreUtilities.getExceptionText(cause));
    }

    /**
     * 收到客户端信息时调用
     */
    public void messageReceived(IoSession session, Object message) throws Exception {
        MinaMessage receiverMessage = (MinaMessage) message;
        if (receiverMessage != null) {
            ActionEnum action = receiverMessage.getAction();
            if (action == ActionEnum.CLIENT_REGIST) {
                clientRegist(receiverMessage, session);
            } else if (action == ActionEnum.CLIENT_UNREGIST) {
                clientUnRegist(receiverMessage);
            } else if (action == ActionEnum.CLIENT_RETURN_RESULT) {
                clientReturnResult(receiverMessage);
            } else if (action == ActionEnum.CACHE_CLIENT_REGIST) {
                cacheClientRegist(receiverMessage);
            } else if (action == ActionEnum.CACHE_CLIENT_NOTIFY) {
                cacheClientNotify(receiverMessage);
            } else if (action == ActionEnum.CACHE_CLIENT_RETURN_RESULT) {
                cacheClientReturnResult(receiverMessage);
            } else if (action == ActionEnum.HEARTBEAT) {
                clientHeartbeat(receiverMessage, session);
            }

        }
    }

    /**
     * 处理客户端发送过来检查心跳的消息
     * 
     * @param receiverMessage
     * @param session
     */
    private void clientHeartbeat(MinaMessage receiverMessage, IoSession session) {
        ClientInfo key = receiverMessage.getClientInfo();
        IoSessionInfo tmpSessionInfo = sessionMap.get(key);
        if (tmpSessionInfo != null) {
            tmpSessionInfo.setLastHeartbeatTime(System.currentTimeMillis());
        }

        MinaMessage mm = new MinaMessage(key);
        mm.setAction(ActionEnum.HEARTBEAT);
        push(mm);
        // log("心跳："+key+"  date="+new Date());
    }

    /**
     * 客户端更新后返回结果
     * 
     * @param receiverMessage
     */
    private void cacheClientReturnResult(MinaMessage receiverMessage) {
        if (cacheGroupMap == null) {
            return;
        }
        ClientInfo clientInfo = receiverMessage.getClientInfo();
        HashMap<String, Serializable> paramMap = receiverMessage.getParamMap();
        if (paramMap != null && !paramMap.isEmpty()) {
            Serializable obj = paramMap.get(CacheUpdateListener.KEY);
            if (obj instanceof NotifyInfo) {
                NotifyInfo notifyInfo = (NotifyInfo) obj;
                String groupName = notifyInfo.getGroup();
                CacheGroupInfo cacheGroupInfo = cacheGroupMap.get(groupName);
                if (cacheGroupInfo != null) {
                    List<EachClient> groupList = cacheGroupInfo.getGroupList();
                    if (groupList != null) {
                        for (EachClient c : groupList) {
                            if (c.getClient().equals(clientInfo)) {
                                c.setUpdateTime(System.currentTimeMillis());
                                String msg = clientInfo + " groupName=" + groupName + "已经更新完毕啦!";
                                log(msg);
                            }
                        }
                    }
                }
            }
        }
    }

    private void cacheClientRegist(MinaMessage receiverMessage) {
        receiverMessage.getParamMap();
        ClientInfo key = receiverMessage.getClientInfo();
        HashMap<String, Serializable> paramMap = receiverMessage.getParamMap();
        if (paramMap != null && !paramMap.isEmpty()) {
            Serializable obj = paramMap.get(CacheUpdateListener.KEY);
            if (obj instanceof NotifyInfo) {
                NotifyInfo notifyInfo = (NotifyInfo) obj;
                String groupName = notifyInfo.getGroup();
                CacheGroupInfo cacheGroupInfo = cacheGroupMap.get(groupName);
                if (cacheGroupInfo == null) {
                    cacheGroupInfo = new CacheGroupInfo();
                    cacheGroupMap.put(groupName, cacheGroupInfo);
                }
                EachClient eachClient = new EachClient(key);
                eachClient.setUpdateTime(System.currentTimeMillis());
                cacheGroupInfo.add(eachClient);
                String msg = "注册一个CacheGroup。 group=" + groupName + " ClientInfo=" + key;
                log(msg);
            }
        }
    }

    private void cacheClientNotify(MinaMessage receiverMessage) {
        if (cacheGroupMap == null) {
            return;
        }
        HashMap<String, Serializable> paramMap = receiverMessage.getParamMap();
        if (paramMap != null && !paramMap.isEmpty()) {
            Serializable obj = paramMap.get(CacheUpdateListener.KEY);
            if (obj instanceof NotifyInfo) {
                NotifyInfo notifyInfo = (NotifyInfo) obj;
                String groupName = notifyInfo.getGroup();
                CacheGroupInfo cacheGroupInfo = cacheGroupMap.get(groupName);
                if (cacheGroupInfo != null) {
                    cacheGroupInfo.addHistory(notifyInfo);
                    cacheGroupInfo.setUpdateTime(System.currentTimeMillis());
                    List<EachClient> groupList = cacheGroupInfo.getGroupList();
                    if (groupList != null) {
                        for (EachClient c : groupList) {
                            notifyAllClientUpdateCache(c, notifyInfo);
                        }
                        String msg = notifyInfo.getSourceIpKey() + "通知大家更新。group=" + groupName + " 总共有"
                                     + groupList.size() + "需要更新!";
                        log(msg);
                    }
                }
            }
        }
    }

    private void notifyAllClientUpdateCache(EachClient eachClient, NotifyInfo notifyInfo) {
        if (sessionMap == null || eachClient == null) {
            return;
        }
        eachClient.setUpdateTime(null);
        IoSessionInfo iso = sessionMap.get(eachClient.getClient());
        if (iso != null) {
            MinaMessage newMsg = new MinaMessage(eachClient.getClient());
            newMsg.setAction(ActionEnum.CACHE_SERVER_BROADCAST);
            newMsg.setRemark("Nisa通知Client端Cache更新。Group=" + notifyInfo.getGroup() + " key="
                             + notifyInfo.getSourceIpKey());
            HashMap<String, Serializable> paramMap = new HashMap<String, Serializable>();
            paramMap.put(CacheUpdateListener.KEY, notifyInfo);
            newMsg.setParamMap(paramMap);
            iso.getRegistSession().write(newMsg);
        }
    }

    private void clientReturnResult(MinaMessage receiverMessage) {
        log("服务器收到" + receiverMessage.getClientInfo() + "的处理结果，内容是" + receiverMessage.getRemark());
    }

    private void clientUnRegist(MinaMessage receiverMessage) {
        ClientInfo key = receiverMessage.getClientInfo();
        String msg = "";
        if (sessionMap.containsKey(key)) {
            sessionMap.remove(key);
            removeClientFromCacheGroupMap(key);
        } else {
            msg = "取消注册成功! 不存在" + receiverMessage.getClientInfo() + "的IoSession";
        }
        log(msg);
    }

    /**
     * 当某个客户端关闭时，需要把CacheGroupList中也删除掉
     * 
     * @param clientInfo
     */
    private void removeClientFromCacheGroupMap(ClientInfo clientInfo) {
        if (cacheGroupMap == null) {
            return;
        }
        Iterator<String> ir = cacheGroupMap.keySet().iterator();
        while (ir.hasNext()) {
            String groupName = ir.next();
            CacheGroupInfo cacheGroupInfo = cacheGroupMap.get(groupName);
            cacheGroupInfo.removeByClientInfo(clientInfo);
        }
    }

    private void clientRegist(MinaMessage receiverMessage, IoSession session) {
        ClientInfo key = receiverMessage.getClientInfo();
        String msg = "";
        if (sessionMap.containsKey(key)) {
            msg = "注册失败! 已经存在" + receiverMessage.getClientInfo() + "的IoSession";
            log(msg);
        } else {
            msg = "注册成功! IP和应用名是" + receiverMessage.getClientInfo();
            IoSessionInfo tmpSessionInfo = new IoSessionInfo(receiverMessage.getClientInfo(), session,
                                                             System.currentTimeMillis(), System.currentTimeMillis());
            sessionMap.put(key, tmpSessionInfo);
            log(msg);
            if (configinfoService != null) {
                HashMap<String, Serializable> configItems = configinfoService.getConfigInfos(receiverMessage.getClientInfo().getProject(),
                                                                                             receiverMessage.getClientInfo().getAppName(),
                                                                                             receiverMessage.getClientInfo().getConfigType());
                MinaMessage mm = new MinaMessage(receiverMessage.getClientInfo());
                mm.setAction(ActionEnum.SERVER_SEND_MESSAGE);
                if (configItems != null && configItems.size() != 0) {
                    Set<String> keySet = configItems.keySet();
                    Iterator<String> iterator = keySet.iterator();
                    while (iterator.hasNext()) {
                        String k = iterator.next();
                        mm.putKV(k, configItems.get(k));
                    }
                }
                push(mm);
            }
        }
    }

    /**
     * 外界主动要求Server端通知所有Client
     * 
     * @param minaMessage
     * @return
     */
    public boolean push(MinaMessage minaMessage) {
        if (sessionMap != null) {
            Iterator<ClientInfo> ir = sessionMap.keySet().iterator();
            while (ir.hasNext()) {
                ClientInfo key = ir.next();
                IoSessionInfo value = sessionMap.get(key);
                if (value != null) {
                    if (minaMessage.getClientInfo().getIp().equals("all")
                        || value.getClientInfo().equals(minaMessage.getClientInfo())) {
                        // log("服务器找到ClientInfo=" + value.getClientInfo() + "的IoSession,准备广播!");
                        value.getRegistSession().write(minaMessage);
                    }
                }
            }
        }
        return true;
    }

    /***
     * 空闲时调用
     */
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log("IDLE " + session.getIdleCount(status));
    }

    /**
     * 检查Client端的是否存在
     */
    void checkClient() {
        if (sessionMap != null) {
            Iterator<ClientInfo> ir = sessionMap.keySet().iterator();
            while (ir.hasNext()) {
                ClientInfo key = ir.next();
                IoSessionInfo value = sessionMap.get(key);
                if (value != null) {
                    if (System.currentTimeMillis() - value.getLastHeartbeatTime() >= 1000 * 60 * 3) // 3分钟没有反应就直接把Client端剔除掉
                    {
                        log("3分钟没有反应就直接把Client端剔除掉。client=" + key);
                        sessionMap.remove(key);
                        removeClientFromCacheGroupMap(key);
                    }
                }
            }
        }
    }
}
