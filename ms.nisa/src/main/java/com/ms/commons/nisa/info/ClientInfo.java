/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.info;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 用于记录注册的客户端应用的IP、应用名、配置类型
 * 
 * @author zxc Apr 12, 2013 6:49:50 PM
 */
public class ClientInfo implements Serializable {

    private static final long serialVersionUID = -6291257527806358194L;
    private String            ip;
    private String            project;
    private String            appName;
    private String            configType;                              // 配置类型

    public ClientInfo(String ip, String project, String appName, String configType) {
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(project) || StringUtils.isEmpty(appName)
            || StringUtils.isEmpty(configType)) {
            throw new RuntimeException("The MinaMessage is Error! ip or appName is empty! ip=" + ip + " appName="
                                       + appName);
        }
        this.ip = ip;
        this.project = project;
        this.appName = appName;
        this.configType = configType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getProject() {
        return project;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ip=").append(ip).append(",");
        sb.append("project=").append(project).append(",");
        sb.append("appName=").append(appName).append(",");
        sb.append("configType=").append(configType);
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ClientInfo) {
            return this.toString().equals(obj.toString());
        }
        return false;
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
