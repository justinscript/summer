/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.listener;

import com.ms.commons.nisa.info.NotifyInfo;

/**
 * 应用想监听Cache是否更新，请实现找个接口，并到CacheRegistService中注册
 * 
 * @author zxc Apr 12, 2013 6:48:41 PM
 */
public interface CacheUpdateListener {

    /**
     * 对外通知Cache更新信息。具体需要修改什么东西，一切均被封装在NotifyInfo中
     */
    boolean notifyUpdate(NotifyInfo info);

    // Server端返回时把NotifyInfo数据放入map中，此时就使用这个特殊的Key
    String KEY = CacheUpdateListener.class.getName() + "_CACHE_KEY";
}
