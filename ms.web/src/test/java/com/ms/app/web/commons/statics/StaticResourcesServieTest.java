/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.statics;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zxc Apr 12, 2013 11:23:16 PM
 */
public class StaticResourcesServieTest extends TestCase {

    public void testgetResources() {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                                                                        new String[] { "classpath*:/META-INF/spring/biz/spring_statics_test.xml" });
        StaticResourcesServie service = (StaticResourcesServie) context.getBean("staticsService");
        StaticResources resource1 = service.getResource("/guang/美容美体/连衣裙.htm");
        StaticResources resource2 = service.getResource("/guang/美容美体/1_1_1.htm");
        StaticResources resource3 = service.getResource("/guang.htm");
        assertEquals(resource1, resource2);
        assertEquals(resource1, resource3);
        String[] css = resource1.getCss("style.uzai.com", "12345", "debug");
        assertEquals(2, css.length);
        assertEquals("style.uzai.com/css12345/build/site/amazon/guangdebug.css", css[0]);
        assertEquals("style.uzai.com/css12345/build/site/amazon/scenedebug.css", css[1]);
        String[] js = resource1.getAppJs("style.uzai.com", "12345", "debug");
        assertEquals("style.uzai.com/js12345/build/site/amazon/fe_appdebug.js", js[0]);
        assertEquals("style.uzai.com/js12345/build/site/amazon/uploaddebug.js", js[1]);

        String[] css2 = resource1.getCss("style.uzai.com", "12345", "debug");
        assertEquals(css, css2);

        StaticResources resource11 = service.getResource("/test/test.htm");
        StaticResources resource22 = service.getResource("/asdasd/test.htm");
        assertEquals(service.getDefaultResource(), resource11);
        assertEquals(service.getDefaultResource(), resource22);
    }
}
