/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.summer.core.context;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.ms.commons.log.ExpandLogger;
import com.ms.commons.log.LoggerFactoryWrapper;
import com.ms.commons.summer.core.scanner.OverrideableBeanDefinitionDocumentReader;
import com.ms.commons.summer.core.scanner.OverrideableBeanFactory;

/**
 * 对XmlWebApplicationContext的扩展<br>
 * 保证summer.xml第一个被读取，为其他summer配置文件覆盖默认配置提供可能
 * 
 * @author zxc Apr 12, 2013 4:09:09 PM
 */
public class SummerApplicationContext extends XmlWebApplicationContext {

    public static final String             SPRING_CONFIG_PATH_PREFIX      = "/META-INF/spring/summer/";
    public static final String             SUMMER                         = "summer.xml";
    public static final String             SPRING_CONFIG_LOCATION_PATTERN = "summer.springConfigLocationPattern";

    private static ResourcePatternResolver resourcePatternResolver        = new PathMatchingResourcePatternResolver();
    private Map<String, Resource>          springConfigs                  = new HashMap<String, Resource>();
    private static Set<String>             loadedConfigLocations          = new HashSet<String>();

    private static ExpandLogger            logger                         = LoggerFactoryWrapper.getLogger(SummerApplicationContext.class);

    public SummerApplicationContext() {
        super();
        // 允许bean定义的覆写操作
        this.setAllowBeanDefinitionOverriding(true);
    }

    public void setConfigLocation(String location) {
        LinkedList<String> configs = new LinkedList<String>();
        try {
            LinkedList<Resource> rss = getLocationResources();
            for (Resource resource : rss) {
                String resourceName = SPRING_CONFIG_PATH_PREFIX + resource.getFilename();
                configs.add(resourceName);
                this.springConfigs.put(resourceName, resource);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 确保summer.xml在最先，用户自定义的配置在中间位置，自动扫瞄的文件在最后
        String summerXml = configs.get(0);
        StringBuilder stb = new StringBuilder(100);
        stb.append(summerXml);
        if (location != null) {
            stb.append(" ");
            stb.append(location);
        }

        // 删除summer的系统配置文件
        configs.remove(0);
        Collections.sort(configs);

        for (int i = 0; i < configs.size(); i++) {
            stb.append(" ");
            stb.append(configs.get(i));
        }

        super.setConfigLocation(stb.toString());
    }

    public static LinkedList<Resource> getLocationResources() throws IOException {
        LinkedList<Resource> rss = new LinkedList<Resource>();

        String summerConfigFileName = SUMMER;
        if (logger.isDebugEnabled()) {
            logger.debug("use default summer config file : " + summerConfigFileName);
        }

        // summer的缺省配置文件
        Resource[] summerResources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                                                                          + summerConfigFileName);
        if (summerResources == null || summerResources.length < 1) {
            throw new RuntimeException(summerConfigFileName + "文件丢失");
        }
        Resource summerConfig = summerResources[0];

        // 用户的配置文件路径
        String userConfigLocationPattern = System.getProperty(SPRING_CONFIG_LOCATION_PATTERN);
        if (userConfigLocationPattern == null || userConfigLocationPattern.trim().equals("")) {
            userConfigLocationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + SPRING_CONFIG_PATH_PREFIX
                                        + "*.xml";
            if (logger.isDebugEnabled()) {
                logger.debug("user default user config file pattern : " + userConfigLocationPattern);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("user custom user config file pattern : " + userConfigLocationPattern);
            }
        }

        Resource[] resources = resourcePatternResolver.getResources(userConfigLocationPattern);
        rss.add(summerConfig);
        Collections.addAll(rss, resources);
        return rss;
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
        // Create a new XmlBeanDefinitionReader for the given BeanFactory.
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        // 设置自定义的reader，提供可覆写的支持
        beanDefinitionReader.setDocumentReaderClass(OverrideableBeanDefinitionDocumentReader.class);

        // Configure the bean definition reader with this context's
        // resource loading environment.
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

        // Allow a subclass to provide custom initialization of the reader,
        // then proceed with actually loading the bean definitions.
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (int i = 0; i < configLocations.length; i++) {
                String location = configLocations[i];
                if (!loadedConfigLocations.contains(location)) {
                    reader.loadBeanDefinitions(location);
                    loadedConfigLocations.add(location);
                }
            }
        }
    }

    protected String[] getDefaultConfigLocations() {
        return new String[] {};
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new OverrideableBeanFactory(getInternalParentBeanFactory());
    }

    protected Resource getResourceByPath(String path) {
        try {
            Resource resource = super.getResourceByPath(path);
            if (resource != null && !resource.exists()) {
                Resource _resource = this.springConfigs.get(path);
                if (_resource != null) resource = _resource;
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
