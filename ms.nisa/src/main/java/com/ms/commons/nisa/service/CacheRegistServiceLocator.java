/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.nisa.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.commons.core.ApplicationContextManager;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.nisa.interfaces.CacheRegistService;

/**
 * 此个CacheRegistService的初始化，没有放入ConfigServiceLocator中是有含义的。<br>
 * 而且意义重大。CacheRegistServiceImpl时会调用init()方法，该方法中会初始化ConfigService，这样导致Spring初始化失败。<br>
 * 所以在CacheRegistServiceImpl初始化之前ConfigServiceImpl已经完成初始化。
 * 
 * @author zxc Apr 12, 2013 6:45:17 PM
 */
public class CacheRegistServiceLocator {

    protected static ApplicationContext context;

    private static ExpandLogger         log = LoggerFactoryWrapper.getLogger(CacheRegistServiceLocator.class);

    static {
        try {
            context = new ClassPathXmlApplicationContext(
                                                         new String[] { "classpath*:/META-INF/spring/biz/nisa_cacheregist_service.xml" });
            ApplicationContextManager.regist(context);
        } catch (RuntimeException e) {
            log.error("初始化 CacheRegistServiceLocator 失败!!!", e);
            throw e;
        }
    }

    public static boolean hasInitFinish() {
        return context != null;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static Object getBean(String beanId) {
        return context.getBean(beanId);
    }

    public static CacheRegistService getCacheRegistService() {
        return (CacheRegistService) getBean("cacheRegistService");
    }
}
