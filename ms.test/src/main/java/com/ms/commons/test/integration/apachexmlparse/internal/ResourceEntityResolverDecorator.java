/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.integration.apachexmlparse.internal;

import java.io.IOException;

import mockit.Mock;
import mockit.MockClass;

import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ms.commons.test.common.ReflectUtil;

/**
 * @author zxc Apr 13, 2013 11:46:12 PM
 */
@MockClass(realClass = ResourceEntityResolver.class)
public class ResourceEntityResolverDecorator {

    public ResourceEntityResolver it;
    private ResourceLoader        resourceLoader = null;

    @Mock(reentrant = true)
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        try {
            Object oldResourceLoader = ReflectUtil.getObject(it, "resourceLoader");
            if (!oldResourceLoader.getClass().getName().equals("com.ms.commons.test.integration.apachexmlparse.internal.LocalResourceLoader")) {
                if (resourceLoader == null) {
                    resourceLoader = new LocalResourceLoader((ResourceLoader) oldResourceLoader);
                }
                if (oldResourceLoader != resourceLoader) {
                    ReflectUtil.setObject(it, "resourceLoader", resourceLoader);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return it.resolveEntity(publicId, systemId);
    }
}
