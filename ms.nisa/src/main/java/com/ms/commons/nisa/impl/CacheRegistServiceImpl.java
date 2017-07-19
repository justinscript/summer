/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ms.commons.nisa.info.NotifyInfo;
import com.ms.commons.nisa.interfaces.CacheRegistService;
import com.ms.commons.nisa.interfaces.ConfigService;
import com.ms.commons.nisa.listener.CacheUpdateListener;
import com.ms.commons.nisa.mina.client.MinaClient;
import com.ms.commons.nisa.mina.client.MinaClientHandler;
import com.ms.commons.nisa.service.ConfigServiceLocator;
import com.ms.commons.utilities.CoreUtilities;

/**
 * @author zxc Apr 12, 2013 6:52:37 PM
 */
public class CacheRegistServiceImpl implements CacheRegistService {

    private Map<String, CacheUpdateListener> listeners         = new HashMap<String, CacheUpdateListener>();
    private MinaClient                       minaClient;
    // 用于判断唯一ID。同一台服务器同时启动两个JVM是，IP是一致的，此时用Java的Thread的hashCode()做为唯一值。
    private static String                    jvmThreadHashCode = null;

    public void init() {
        check();
    }

    /**
     * 缓存注册
     * 
     * @param listener
     */
    public void regist(String groupName, CacheUpdateListener listener) {
        if (groupName == null) {
            throw new Error("the groupName is null !");
        }
        if (listener == null) {
            throw new Error("the listener is null !");
        }
        String newGroupName = groupName;
        if (listeners.containsKey(newGroupName)) {
            throw new Error("the groupName has exist! groupName=" + groupName);
        }
        listeners.put(newGroupName, listener);
        check();

        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setGroup(newGroupName);
        if (getMinaClientHandler() != null) {
            try {
                getMinaClientHandler().sentNotifyInfoMessage(ActionEnum.CACHE_CLIENT_REGIST, notifyInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果MinaClientHandler重新链接后就尝试再注册一下
     */
    public void registCacheUpdateListener() {
        if (listeners != null) {
            Iterator<String> ir = listeners.keySet().iterator();
            while (ir.hasNext()) {
                String key = ir.next();
                NotifyInfo notifyInfo = new NotifyInfo();
                notifyInfo.setGroup(key);
                if (getMinaClientHandler() != null) {
                    try {
                        getMinaClientHandler().sentNotifyInfoMessage(ActionEnum.CACHE_CLIENT_REGIST, notifyInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void check() {
        if (minaClient == null) {
            ConfigService configService = ConfigServiceLocator.getCongfigService();
            if (configService instanceof ConfigServiceImpl) {
                minaClient = ((ConfigServiceImpl) configService).getMinaClient();
            }
        }
    }

    public void broadcast(String groupName, Serializable updaeValue) {
        if (groupName == null || updaeValue == null) {
            return;
        }
        String newGroupName = groupName;
        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setGroup(newGroupName);
        notifyInfo.setUpdaeValue(updaeValue);
        notifyInfo.setSourceIpKey(getSourceIpKey());
        if (getMinaClientHandler() != null) {
            try {
                getMinaClientHandler().sentNotifyInfoMessage(ActionEnum.CACHE_CLIENT_NOTIFY, notifyInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deal(MinaMessage minaMessage) {
        HashMap<String, Serializable> paramMap = minaMessage.getParamMap();
        if (paramMap != null && !paramMap.isEmpty()) {
            Serializable obj = paramMap.get(CacheUpdateListener.KEY);
            if (obj instanceof NotifyInfo) {
                fireConfigListener((NotifyInfo) obj);
            }
        }
    }

    private void fireConfigListener(NotifyInfo notifyInfo) {
        if (notifyInfo != null && listeners != null) {
            Iterator<String> ir = listeners.keySet().iterator();
            while (ir.hasNext()) {
                String key = ir.next();
                if (key.equals(notifyInfo.getGroup())) {
                    CacheUpdateListener l = listeners.get(key);
                    if (l != null) {
                        l.notifyUpdate(notifyInfo);
                    }
                }
            }
        }
    }

    public void setMinaClient(MinaClient minaClient) {
        this.minaClient = minaClient;
    }

    private MinaClientHandler getMinaClientHandler() {
        if (minaClient != null) {
            return minaClient.getMinaClientHandler();
        }
        return null;
    }

    public static String getSourceIpKey() {
        if (jvmThreadHashCode == null) {
            jvmThreadHashCode = CoreUtilities.getIPAddress() + "_" + Thread.currentThread().hashCode();
        }
        return jvmThreadHashCode;
    }
}
