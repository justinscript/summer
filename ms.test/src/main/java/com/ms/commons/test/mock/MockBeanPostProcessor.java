/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author zxc Apr 14, 2013 12:10:32 AM
 */
public class MockBeanPostProcessor implements BeanPostProcessor {

    protected final Logger log = Logger.getLogger(getClass());

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            AliMock.mockObject(bean);
            Object mockBean = AliMock.createMockObject(bean);
            if (mockBean != null) {
                return mockBean;
            }
        } catch (Exception e) {
            log.error("Failed to create mock bean [" + beanName + "].", e);
        }

        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
