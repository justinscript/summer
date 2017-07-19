/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 1:19:40 PM
 */
public class ApplicationContextManager {

    private static ExpandLogger             log         = LoggerFactoryWrapper.getLogger(ApplicationContextManager.class);
    // 记录JVM中使用的Spring容器
    private static List<ApplicationContext> contextList = new ArrayList<ApplicationContext>();

    /**
     * 向容器注册一个
     * 
     * @param context
     */
    public static void regist(ApplicationContext context) {
        if (context == null) {
            return;
        }
        if (contextList.contains(context)) {
            return;
        }
        contextList.add(context);
    }

    /**
     * 当JVM退出时（例如Jetty关闭），调用Spring容器的Destory方法，目的是让应用完成一些后续操作。<br>
     * 该方法仅仅在JVM退出前调用，否则可能存在问题。
     */
    public static void destoryAllContext() {
        for (ApplicationContext c : contextList) {
            if (c instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext) c).destroy();
                log.info("Close ApplicationContext! class=" + c.getClass().getName());
            }
        }
    }
}
