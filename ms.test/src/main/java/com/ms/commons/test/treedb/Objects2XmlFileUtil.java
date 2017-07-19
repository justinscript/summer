/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.treedb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.thoughtworks.xstream.XStream;
import com.ms.commons.test.cache.BuiltInCacheKey;
import com.ms.commons.test.datareader.DataReaderType;
import com.ms.commons.test.datareader.DataReaderUtil;
import com.ms.commons.test.datareader.impl.BaseReaderUtil;

/**
 * @author zxc Apr 13, 2013 11:31:15 PM
 */
public class Objects2XmlFileUtil extends BaseReaderUtil {

    private static final String ROOT           = "root";

    private static final String TREEOBJECT     = "TreeObject";

    private static final String TREEOBJECTLIST = "treeObjectList";

    public static void write(String testMethodName, BuiltInCacheKey prePareOrResult, List<?>... prepareData) {

        OutputStreamWriter writer = null;
        File file = getFile(testMethodName, prePareOrResult);
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            XStream xStream = new XStream();
            StringBuilder root = new StringBuilder();

            root.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            root.append("<").append(ROOT).append(">");

            for (List<?> objectList : prepareData) {
                if (objectList == null || objectList.size() <= 0) continue;

                StringBuilder objectStr = objectList2XmlString(xStream, objectList);

                root.append(objectStr);
            }

            root.append("</").append(ROOT).append(">");

            writer.write(prettyPrint(root.toString()));
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("write " + file.getAbsolutePath() + " failed", e);
        } catch (IOException e) {
            throw new RuntimeException("write " + file.getAbsolutePath() + " failed", e);
        } finally {
            close(writer);
        }
    }

    private static StringBuilder objectList2XmlString(XStream xStream, List<?> objectList) {
        getClassName(objectList);

        StringBuilder objectStr = new StringBuilder();
        objectStr.append("<").append(TREEOBJECT).append(" name=\"").append(getClassName(objectList)).append("\">");
        objectStr.append("<").append(TREEOBJECTLIST).append(">");

        for (Object object : objectList) {
            objectStr.append(xStream.toXML(object));
        }
        objectStr.append("</").append(TREEOBJECTLIST).append(">");
        objectStr.append("</").append(TREEOBJECT).append(">");
        return objectStr;
    }

    private static File getFile(String testMethodName, BuiltInCacheKey prePareOrResult) {
        // ��basePathת��srcPath
        basePath = StringUtils.replace(basePath, "target/classes.eclipse_test", "src/java.test");

        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdir();
        }

        if (testMethodName.indexOf('_') <= 0) {
            testMethodName = testMethodName + "_" + prePareOrResult.getValue();
        }

        String defaultExt = DataReaderUtil.getDefaultExt(DataReaderType.TreeXml);
        String relativePath = basePath + "/" + testMethodName;
        if (!relativePath.endsWith(defaultExt)) {
            relativePath = relativePath + defaultExt;
        }
        return new File(relativePath);
    }

    private static void close(OutputStreamWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getClassName(List<?> objectList) {
        return ClassUtils.getShortClassName(objectList.get(0).getClass());
    }

    private static String prettyPrint(final String xml) {

        if (StringUtils.isBlank(xml)) {
            throw new RuntimeException("xml was null or blank in prettyPrint()");
        }

        final StringWriter sw;

        try {
            final OutputFormat format = OutputFormat.createPrettyPrint();
            final org.dom4j.Document document = DocumentHelper.parseText(xml);
            sw = new StringWriter();
            final XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
        } catch (Exception e) {
            throw new RuntimeException("Error pretty printing xml:\n" + xml, e);
        }
        return sw.toString();
    }
}
