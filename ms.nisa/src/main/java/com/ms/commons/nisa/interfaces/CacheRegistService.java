/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.interfaces;

import java.io.Serializable;

import com.ms.commons.nisa.listener.CacheUpdateListener;

/**
 * @author zxc Apr 12, 2013 6:49:24 PM
 */
public interface CacheRegistService {

    /**
     * 缓存注册
     * 
     * @param listener
     */
    void regist(String groupName, CacheUpdateListener listener);

    /**
     * 广播更新的值
     * 
     * @param groupName
     * @param updaeValue
     */
    void broadcast(String groupName, Serializable updaeValue);
}
