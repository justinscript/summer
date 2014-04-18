/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.integration.apachexmlparse.internal;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.ms.commons.test.common.StringUtil;
import com.ms.commons.test.constants.IntlTestGlobalConstants;

/**
 * @author zxc Apr 13, 2013 11:46:20 PM
 */
public class LocalResourceLoader implements ResourceLoader {

    private static ConcurrentMap<String, String> DTD_MAP = new ConcurrentHashMap<String, String>();
    private ResourceLoader                       parent;

    public LocalResourceLoader(ResourceLoader resourceLoader) {
        this.parent = resourceLoader;
    }

    public Resource getResource(String location) {

        System.out.println("Get resource: " + location);
        try {
            boolean isFromCache = true;
            String dtdFileName = IntlTestGlobalConstants.TESTCASE_DTD_DIR + File.separator
                                 + StringUtil.replaceNoWordChars(location) + ".dtd";
            if (DTD_MAP.get(dtdFileName) == null) {
                File dtdFile = new File(dtdFileName);
                if (!dtdFile.exists()) {
                    // load dtd from net
                    isFromCache = false;
                    String dtdContent = StringUtils.join(IOUtils.readLines((new URL(location)).openStream(), "UTF-8"),
                                                         IntlTestGlobalConstants.LINE_SEPARATOR);
                    FileUtils.writeStringToFile(dtdFile, dtdContent, "UTF-8");

                    DTD_MAP.put(dtdFileName, dtdContent);
                } else {
                    DTD_MAP.put(dtdFileName, FileUtils.readFileToString(dtdFile, "UTF-8"));
                }
            }

            if (isFromCache) {
                System.out.println("Get resource from cache: " + location);
            } else {
                System.out.println("Get resource from net: " + location);
            }

            return new ByteArrayResource(DTD_MAP.get(dtdFileName).getBytes("UTF-8"));
        } catch (Exception e) {
            return parent.getResource(location);
        }
    }

    public ClassLoader getClassLoader() {
        return parent.getClassLoader();
    }
}
