/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.core.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.w3c.dom.Element;

/**
 * 允许扫瞄过程中对重复名称bean的支持，为bean的覆盖功能提供支持
 * 
 * @author zxc Apr 12, 2013 4:08:45 PM
 */
@SuppressWarnings("all")
public class OverrideableBeanDefinitionParserDelegate extends BeanDefinitionParserDelegate {

    private final Set usedNames = new HashSet();

    public OverrideableBeanDefinitionParserDelegate(XmlReaderContext readerContext) {
        super(readerContext);
    }

    /**
     * Validate that the specified bean name and aliases have not been used already.
     */
    protected void checkNameUniqueness(String beanName, List aliases, Element beanElement) {
        // 如果当前bean已经定义，直接返回，不报错，为bean的覆盖功能提供支持
        if (this.usedNames.contains(beanName)) return;
        this.usedNames.add(beanName);
        super.checkNameUniqueness(beanName, aliases, beanElement);
    }
}
