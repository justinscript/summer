/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.test.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zxc Apr 14, 2013 12:11:24 AM
 */
public class MockClassCalledContext {

    private Class<?>                                   clazz;

    private Map<String, List<MockMethodCalledContext>> mockMethodCalledContextMap = new HashMap<String, List<MockMethodCalledContext>>();

    public MockClassCalledContext(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Map<String, List<MockMethodCalledContext>> getMockMethodCalledContextMap() {
        return mockMethodCalledContextMap;
    }

    public List<MockMethodCalledContext> getMockMethodCalledContextList(String method) {
        synchronized (mockMethodCalledContextMap) {
            return mockMethodCalledContextMap.get(method);
        }
    }

    public void addMockMethodCalledContext(MockMethodCalledContext mockMethodCalledContext) {
        synchronized (mockMethodCalledContextMap) {
            List<MockMethodCalledContext> list = this.mockMethodCalledContextMap.get(mockMethodCalledContext.getMethod().getName());
            if (list == null) {
                list = new ArrayList<MockMethodCalledContext>();
                this.mockMethodCalledContextMap.put(mockMethodCalledContext.getMethod().getName(), list);
            }
            list.add(mockMethodCalledContext);
        }
    }
}
