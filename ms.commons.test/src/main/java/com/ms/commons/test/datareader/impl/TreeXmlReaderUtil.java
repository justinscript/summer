/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.datareader.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.ms.commons.test.treedb.TreeDatabase;
import com.ms.commons.test.treedb.TreeObject;

/**
 * @author zxc Apr 13, 2013 11:35:50 PM
 */
public class TreeXmlReaderUtil extends BaseReaderUtil {

    static final Logger log = Logger.getLogger(TreeXmlReaderUtil.class);

    public static TreeDatabase read(String fileName) {
        String absPath = getAbsolutedPath(fileName);
        File file = new File(absPath);
        if (file.exists()) {
            checkDataFile(absPath);
        }

        TreeDatabase database = new TreeDatabase();
        SAXReader reader = new SAXReader();
        Document doc;
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            XStream xStream = new XStream();
            doc = reader.read(inputStream);

            initAlias(doc, xStream);

            List<TreeObject> objects = readObjects(doc, xStream);

            database.setTreeObjects(objects);
            inputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(inputStream);
        }
        return database;
    }

    private static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<TreeObject> readObjects(Document doc, XStream xStream) {
        List<TreeObject> objectList = new ArrayList<TreeObject>();
        List<Element> treeObjectList = doc.getRootElement().elements("TreeObject");
        for (Element objectElement : treeObjectList) {
            objectList.add(readObject(objectElement, xStream));
        }
        return objectList;
    }

    /**
     * Ϊ����xstream��׼ȷ���ض��󣬳�ʼ������
     * 
     * @param doc
     * @param xStream
     */
    private static void initAlias(Document doc, XStream xStream) {
        xStream.alias("TreeObject", TreeObject.class);
    }

    private static TreeObject readObject(Element treeObjectElement, XStream xStream) {
        String objectString = treeObjectElement.asXML();
        TreeObject treeObject = (TreeObject) xStream.fromXML(objectString);
        String name = treeObjectElement.attributeValue("name");
        treeObject.setName(name);
        return treeObject;
    }

}
