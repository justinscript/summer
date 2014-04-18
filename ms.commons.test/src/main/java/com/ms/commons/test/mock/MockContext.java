/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.test.mock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zxc Apr 14, 2013 12:11:03 AM
 */
public class MockContext {

    private Class<?>       clazz;
    private String         method;
    private boolean        isLinked;
    private List<MockPair> mockPairList = new ArrayList<MockPair>();

    public MockContext(Class<?> clazz, String method, boolean isLinked) {
        this.clazz = clazz;
        this.method = method;
        this.isLinked = isLinked;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public void setLinked(boolean isLinked) {
        this.isLinked = isLinked;
    }

    public List<MockPair> getMockPairList() {
        return mockPairList;
    }

    public void addMockPair(MockPair mockPair) {
        this.mockPairList.add(mockPair);
    }
}
