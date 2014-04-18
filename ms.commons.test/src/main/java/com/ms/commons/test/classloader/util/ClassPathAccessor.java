/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.classloader.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author zxc Apr 13, 2013 11:08:33 PM
 */
public class ClassPathAccessor {

    public static String getOutputPath(File classPathFile) {
        List<?> elements = getElementsByXPath(classPathFile, "/classpath/classpathentry[@kind='output']");
        if ((elements != null) && (elements.size() > 0)) {
            return ((Element) elements.get(0)).getAttribute("path").getValue();
        }
        return null;
    }

    public static boolean isSrcConfIncluded(File classPathFile) {
        List<?> elements = getElementsByXPath(classPathFile, "/classpath/classpathentry[@path='src/conf']");
        return ((elements != null) && elements.size() > 0);
    }

    public static boolean isSrcConfTestIncluded(File classPathFile) {
        List<?> elements = getElementsByXPath(classPathFile, "/classpath/classpathentry[@path='src/conf.test']");
        return ((elements != null) && elements.size() > 0);
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    public static List<Element> getElementsByXPath(File classPathFile, String xPath) {
        if (classPathFile == null) {
            return null;
        }
        if (!classPathFile.exists()) {
            return null;
        }
        if (!classPathFile.isFile()) {
            return null;
        }
        InputStream fis = null;
        try {
            fis = classPathFile.toURL().openStream();
            SAXBuilder b = new SAXBuilder();
            Document document = b.build(fis);

            List<?> elements = XPath.selectNodes(document, xPath);

            return (List<Element>) elements;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
