/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.info;

import java.io.Serializable;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.utilities.CoreUtilities;
import com.ms.commons.utilities.ZipUtilities;

/**
 * @author zxc Apr 12, 2013 6:49:35 PM
 */
public class NotifyInfo implements Serializable {

    protected static ExpandLogger  logger           = LoggerFactoryWrapper.getLogger(NotifyInfo.class);

    private static final long      serialVersionUID = -1615946139684474576L;
    private String                 group;                                                              // 需要成组的名字
    private String                 sourceIpKey;                                                        // 缓存被更新的最初始IP的Key
    private transient Serializable updaeValue;                                                         // 更新的值
    private byte[]                 bytes;                                                              // 更新的值

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSourceIpKey() {
        return sourceIpKey;
    }

    public void setSourceIpKey(String sourceIpKey) {
        this.sourceIpKey = sourceIpKey;
    }

    public Serializable getUpdaeValue() {
        if (updaeValue == null) {
            if (bytes != null) {
                try {
                    Object obj = ZipUtilities.ungZipObject(bytes);
                    updaeValue = (Serializable) obj;
                } catch (Exception e) {
                    logger.error("ip=" + CoreUtilities.getIPAddress() + " " + e.getMessage(), e);
                }
            }
        }
        return updaeValue;
    }

    public void setUpdaeValue(Serializable updaeValue) {
        try {
            bytes = ZipUtilities.gZipObject(updaeValue);
        } catch (Exception e) {
            logger.error("ip=" + CoreUtilities.getIPAddress() + " " + e.getMessage(), e);
        }
    }
}
