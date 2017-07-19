/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.treedb;

import java.util.List;

import com.ms.commons.test.assertion.exception.AssertException;

/**
 * @author zxc Apr 13, 2013 11:30:32 PM
 */
public class TreeObjectAssert {

    public static void assertResult(TreeDatabase treeDB, Object bean, String objectName, String[] properties,
                                    boolean exclusive) {
        assertResult(treeDB, bean, objectName, 0, properties, exclusive);
    }

    public static void assertResultList(TreeDatabase treeDB, List<?> beanList, String objectName, String[] properties,
                                        boolean exclusive) {

        List<?> objectList = getResultObjectList(treeDB, objectName);

        if (objectList == null) {
            throw new AssertException("Aspect " + messageObjectName(objectName) + " not exists.");
        }
        if (objectList.size() != beanList.size()) {
            throw new AssertException("Aspect " + messageObjectName(objectName) + " row size is " + objectList.size()
                                      + ", but actual is " + beanList.size() + ".");
        }
        assertObjectEqualsToBean(objectList, beanList, properties, exclusive);
    }

    private static void assertResult(TreeDatabase treeDB, Object bean, String objectName, int row, String[] properties,
                                     boolean exclusive) {

        List<?> objectList = getResultObjectList(treeDB, objectName);

        if (objectList == null) {
            throw new AssertException("Aspect " + messageObjectName(objectName) + " not exists.");
        }
        if (objectList.size() <= row) {
            throw new AssertException("Aspect " + messageObjectName(objectName) + " row not exists.");
        }

        assertObjectEqualsToBean(objectList.get(row), bean, properties, exclusive);
    }

    private static List<?> getResultObjectList(TreeDatabase treeDB, String objectName) {
        TreeObject treeTable = null;
        if (objectName == null) {
            treeTable = treeDB.getObject(0);
        } else {
            treeTable = treeDB.getObject(objectName);
        }

        if (treeTable == null) {
            throw new AssertException("Aspect object " + " `" + objectName + "`" + "+not exists.");
        }

        List<?> objectList = treeTable.getTreeObjectList();

        return objectList;
    }

    private static void assertObjectEqualsToBean(Object aspectResultObject, Object acutalObject, String[] properties,
                                                 boolean exclusive) {

        if (!JsonObjectUtils.isSameObject(aspectResultObject, acutalObject, properties, exclusive)) {
            throw new AssertException("Not equals!");
        }
        return;

    }

    private static String messageObjectName(String objectName) {
        return (objectName == null) ? "default object" : "object `" + objectName + "`";
    }
}
