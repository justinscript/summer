/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.impl;

import java.io.Serializable;
import java.util.HashMap;

import com.ms.commons.nisa.info.ClientInfo;

/**
 * @author zxc Apr 12, 2013 6:50:46 PM
 */
public class MinaMessage implements Serializable {

    private static final long             serialVersionUID = 849607316078998216L;
    private ActionEnum                    action;
    private ClientInfo                    clientInfo;
    private String                        remark;
    private String                        sendIp;

    private HashMap<String, Serializable> paramMap         = new HashMap<String, Serializable>();

    public MinaMessage(ClientInfo clientInfo) {
        if (clientInfo == null) {
            throw new RuntimeException("The MinaMessage is Error! clientInfo is null !");
        }
        this.clientInfo = clientInfo;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public HashMap<String, Serializable> getParamMap() {
        return paramMap;
    }

    public void setParamMap(HashMap<String, Serializable> paramMap) {
        this.paramMap = paramMap;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSendIp() {
        return sendIp;
    }

    public void setSendIp(String sendIp) {
        this.sendIp = sendIp;
    }

    // public String getIPAndAppName()
    // {
    // return clientKey.getIp()+"_"+clientKey.getAppName();
    // }

    public void putKV(String key, Serializable value) {
        paramMap.put(key, value);
    }

    public String getKV(String key, String defaultValue) {
        Serializable value = paramMap.get(key);
        return value == null ? defaultValue : (String) value;
    }

    public int getKV(String key, int defaultValue) {
        Serializable value = paramMap.get(key);
        return value == null ? defaultValue : (Integer) value;
    }

    public float getKV(String key, float defaultValue) {
        Serializable value = paramMap.get(key);
        return value == null ? defaultValue : (Float) value;
    }
}
