/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.message.impl.email;

import java.io.Serializable;

/**
 * 建立会话的认证信息
 * 
 * @author zxc Apr 13, 2014 10:45:38 PM
 */
public class MsunMailEnvironment implements Serializable {

    private static final long serialVersionUID = 5650645949033431916L;

    private boolean           isAsynchronize   = false;

    private int               priority         = 0;

    private int               resendCount      = 0;

    private int               resendLimit      = 5;

    private String            hostName;

    private String            user;

    private String            password;

    private Exception         exception;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }

    public boolean isAsynchronize() {
        return isAsynchronize;
    }

    public void setAsynchronize(boolean isAsynchronize) {
        this.isAsynchronize = isAsynchronize;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setResendLimit(int resendLimit) {
        this.resendLimit = resendLimit;
    }

    public int getResendLimit() {
        return resendLimit;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
