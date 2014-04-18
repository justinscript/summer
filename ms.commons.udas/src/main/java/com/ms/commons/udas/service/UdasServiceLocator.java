/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas.service;

import java.util.Map;

import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.udas.interfaces.UdasService;

/**
 * @author zxc Apr 12, 2013 5:29:47 PM
 */
public class UdasServiceLocator extends CommonServiceLocator {

    /**
     * 返回指的name的UdasService，如果不存在就返回null
     * 
     * @param name
     * @return
     */
    public static UdasService getUdasService(String name) {
        if (context == null || name == null) {
            return null;
        }
        return (UdasService) context.getBean(name, UdasService.class);
    }

    /**
     * 引入 getCommonUdasService 的原因在于，目前 udas 使用是通过 spring 注入，每个人这么做成本太大，这是一个 common 使用的 udas，使用者需要自行维护好命名空间
     * 
     * @param name
     * @return
     */
    public static UdasService getCommonUdasService() {
        return getUdasService("commonDataSource");
    }

    @SuppressWarnings("unchecked")
    public static Map<String, UdasService> getAllUdasServiceMap() {
        if (context == null) {
            return null;
        }
        return (Map<String, UdasService>) context.getBeansOfType(UdasService.class);
    }
}
