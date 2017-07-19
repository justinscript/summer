/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commmons.test;

import com.ms.commons.test.BaseTestCase;
import com.ms.commons.test.annotation.Prepare;
import com.ms.commons.test.annotation.TestCaseInfo;

/**
 * 测试使用Excel准备数据
 * 
 * @author zxc Apr 13, 2013 11:00:38 PM
 */
@TestCaseInfo(contextKey = "CommonsTestDB", classSuffix = "Data")
public class ExcelDBTest extends BaseTestCase {

    // @Test
    @Prepare(autoImport = true, autoClear = true, autoClearExistsData = true)
    public void testInit() {
        System.out.println("=======================>start");
        TestDO testdo = new TestDO();
        testdo.setId(1);
        testdo.setName("zxc");
        assertResult(testdo);
        assertResultTable("test");
    }

    private static class TestDO {

        private Integer id;
        private String  name;

        @SuppressWarnings("unused")
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
