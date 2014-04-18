/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import javax.servlet.http.HttpServletRequest;

import com.ms.commons.udas.interfaces.UdasService;

/**
 * @author zxc Apr 12, 2013 10:45:52 PM
 */
public interface PageCache {

    /**
     * 此请求是否支持缓存
     * 
     * @param request
     * @return
     */
    boolean isSupport(HttpServletRequest request);

    /**
     * 得到key
     * 
     * @param request
     * @return
     */
    String calculateKey(HttpServletRequest request);

    /**
     * 得到缓存的过期时间(单位：秒)
     * 
     * @return
     */
    int getEffectiveTime();

    /**
     * 获取缓存介质
     * 
     * @return
     */
    UdasService getUdasService();
}
