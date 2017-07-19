/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.commons.core.ApplicationContextManager;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.nisa.interfaces.ConfigService;

/**
 * nisa配置中心的服务。 这是一个特殊的ServiceLocator，它使用单独的Spring上下文。它在所有应用之前完成Spring初始化
 * 
 * @author zxc Apr 12, 2013 6:44:47 PM
 */
public class ConfigServiceLocator {

    protected static ApplicationContext context;

    private static ExpandLogger         log = LoggerFactoryWrapper.getLogger(ConfigServiceLocator.class);

    static {
        try {
            context = new ClassPathXmlApplicationContext(
                                                         new String[] { "classpath*:/META-INF/spring/biz/nisa_spring_nisa.xml" });
            ApplicationContextManager.regist(context);
        } catch (RuntimeException e) {
            log.error("初始化 ConfigServiceLocator 失败!!!", e);
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

    public static ConfigService getCongfigService() {
        return (ConfigService) getBean("configService");
    }
}
