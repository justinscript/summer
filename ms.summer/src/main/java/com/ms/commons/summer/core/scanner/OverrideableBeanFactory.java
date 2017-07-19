/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.core.scanner;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 提供对bean定义的覆写机制
 * 
 * @author zxc Apr 12, 2013 4:08:27 PM
 */
@SuppressWarnings("all")
public class OverrideableBeanFactory extends DefaultListableBeanFactory {

    public OverrideableBeanFactory() {
        super();
    }

    public OverrideableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        String beanClassName = beanDefinition.getBeanClassName();
        BeanDefinition oldBeanDefinition = null;
        try {
            // 尝试查找当前的bean是否已经注册过
            oldBeanDefinition = this.getBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException e) {

        }

        // 如果当前的bean已经被定义，使用当前的新配置来覆盖原始的定义
        if (oldBeanDefinition != null && oldBeanDefinition instanceof AbstractBeanDefinition) {
            ((AbstractBeanDefinition) oldBeanDefinition).overrideFrom(beanDefinition);
            super.registerBeanDefinition(beanName, oldBeanDefinition);
        } else {
            super.registerBeanDefinition(beanName, beanDefinition);
        }
    }
}
