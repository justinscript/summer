/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.standalone.pojo;

import com.ms.commons.standalone.cons.CronJobStatus;

/**
 * @author zxc Apr 12, 2013 9:01:07 PM
 */
public class CronJob {

    private Boolean       isStandalone = false;
    private String        identity;
    private String        fullClassName;
    private String        jvmParameter;
    private String        cronExpression;
    private CronJobStatus status       = CronJobStatus.NORMAL;

    public boolean isStandalone() {
        return isStandalone;
    }

    public void setStandalone(Boolean isStandalone) {
        this.isStandalone = isStandalone;
    }

    public void setIsStandalone(Boolean isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getJvmParameter() {
        return jvmParameter;
    }

    public void setJvmParameter(String jvmParameter) {
        this.jvmParameter = jvmParameter;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public CronJobStatus getStatus() {
        return status;
    }

    public void setStatus(CronJobStatus status) {
        this.status = status;
    }
}
