/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.pagecache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ms.commons.core.ApplicationContextManager;
import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 10:44:34 PM
 */
public class PageCacheManagerLocator {

    protected static ApplicationContext context;

    private static ExpandLogger         log = LoggerFactoryWrapper.getLogger(CommonServiceLocator.class);

    static {
        try {
            context = new ClassPathXmlApplicationContext(
                                                         new String[] { "classpath*:/META-INF/spring/cache/spring_*.xml" });
            ApplicationContextManager.regist(context);
        } catch (RuntimeException e) {
            log.error("", e);
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

    public static PageCacheManager getPageCacheManager() {
        return (PageCacheManager) getBean("pageCacheManager");
    }
}
