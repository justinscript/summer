/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.integration.jmockit.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zxc Apr 13, 2013 11:46:00 PM
 */
public class JMockItUtil {

    private static boolean           isJMockItStartupRunOnce     = false;
    private static boolean           isDecoratorsMockUpOnce      = false;
    private static final Set<String> SUPPORT_JMOCKIT_VERSION_SET = new HashSet<String>(Arrays.asList("1.6", "1.7"));

    synchronized public static void startUpJMockItIfPossible() {
        if (isJMockItStartupRunOnce) {
            return;
        }
        isJMockItStartupRunOnce = true;

        String javaSpecificationVersion = System.getProperty("java.specification.version");
        if (SUPPORT_JMOCKIT_VERSION_SET.contains(javaSpecificationVersion)) {
            System.err.println("Start jmockit in start up! (jdk version: " + javaSpecificationVersion + ")");
            try {
                Class<?> startup = Class.forName("mockit.internal.startup.Startup");
                Method initializeIfNeeded = startup.getDeclaredMethod("initializeIfNeeded", new Class<?>[0]);
                initializeIfNeeded.invoke(null, new Object[0]);
            } catch (Exception e) {
                String addMessage = "";
                if (e instanceof ClassNotFoundException) {
                    addMessage = "(JMockIt not included!)";
                } else {
                    addMessage = "(" + e.getMessage() + ")";
                }
                System.err.println("CALL mockit.internal.startup.Startup.initializeIfNeeded() failed" + addMessage
                                   + ".");
            }
        }
    }

    synchronized public static void mockUpDecorators() {
        if (isDecoratorsMockUpOnce) {
            return;
        }
        isDecoratorsMockUpOnce = true;

        List<String> mockUpClazzs = Arrays.asList(// NL
        "com.ms.commons.test.integration.apachexmlparse.internal.ResourceEntityResolverDecorator", // NL
                                                  "com.ms.commons.test.integration.mysql.internal.BasicDataSourceDecorator"// NL
        );
        for (String mockUpClazz : mockUpClazzs) {
            mockUpSpecialDecorator(mockUpClazz);
        }
    }

    private static void mockUpSpecialDecorator(String mockUpClazz) {
        try {
            Class<?> startup = Class.forName("mockit.Mockit");
            Method setUpMock = startup.getDeclaredMethod("setUpMock", new Class<?>[] { Object.class });
            setUpMock.invoke(null, new Object[] { Class.forName(mockUpClazz) });

            System.err.println("Mockit.setUpMock(" + mockUpClazz + ".class) successed.");
        } catch (Throwable e) {
            System.err.println("Mockit.setUpMock(" + mockUpClazz + ".class) failed.");
        }
    }
}
