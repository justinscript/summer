/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import com.ms.commons.udas.interfaces.UdasService;

/**
 * @author zxc Apr 12, 2013 10:46:49 PM
 */
public abstract class AbstractPageCache implements PageCache {

    public static final String PATH_SEP = "/";
    private UdasService        udasService;
    private boolean            enable;
    private int                effectiveTime;

    /**
     * 获取缓存介质
     * 
     * @return
     */
    public UdasService getUdasService() {
        return udasService;
    }

    public void setUdasService(UdasService udasService) {
        this.udasService = udasService;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(int effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
}
