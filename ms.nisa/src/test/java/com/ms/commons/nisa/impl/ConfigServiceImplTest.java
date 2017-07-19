/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.impl;

import junit.framework.TestCase;

import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * @author zxc Apr 12, 2013 6:55:40 PM
 */
public class ConfigServiceImplTest extends TestCase {

    public void testInit() {
        System.setProperty(ConfigServiceImpl.KEY_NISA_PROPERTIES, "/Users/zxc/abc.properties");
        System.setProperty(ConfigServiceImpl.KEY_START_MINA_CLIENT, "false");
        ConfigServiceImpl cs = new ConfigServiceImpl();
        try {
            cs.init();
        } catch (NisaException ee) {
            return;
        }
        fail();
    }

    public void testInit3() {
        System.setProperty(ConfigServiceImpl.KEY_START_MINA_CLIENT, "false");
        String path = ConfigServiceImplTest.class.getResource("msun.test.properties").getFile();
        System.setProperty(ConfigServiceImpl.KEY_NISA_PROPERTIES, path);
        ConfigServiceImpl cs = new ConfigServiceImpl();
        try {
            cs.init();
            int intkv = cs.getKV("I_junit.int", 400);
            assertEquals(100, intkv);
            float floatkv = cs.getKV("F_junit.float", 99.99f);
            assertEquals(30.5f, floatkv);
            boolean b = cs.getKV("B_junit.boolean", false);
            assertEquals(true, b);
            String s = cs.getKV("S_junit.string", "nono");
            assertEquals("oye", s);

            int[] kvIntArray = cs.getKVIntArray("IA_junit.intarray");
            assertEquals(2, kvIntArray.length);
            assertEquals(100, kvIntArray[0]);
            assertEquals(200, kvIntArray[1]);

            float[] kvFloatArray = cs.getKVFloatArray("FA_junit.floatarray");
            assertEquals(2, kvFloatArray.length);
            assertEquals(10.99f, kvFloatArray[0]);
            assertEquals(20.88f, kvFloatArray[1]);

            boolean[] kvBooleanArray = cs.getKVBooleanArray("BA_junit.booleanarray");
            assertEquals(3, kvBooleanArray.length);
            assertEquals(false, kvBooleanArray[0]);
            assertEquals(true, kvBooleanArray[1]);
            assertEquals(false, kvBooleanArray[2]);

            String[] kvStringArray = cs.getKVStringArray("SA_junit.stringarray");
            assertEquals(2, kvStringArray.length);
            assertEquals("abc", kvStringArray[0]);
            assertEquals("bcd", kvStringArray[1]);

        } catch (NisaException ee) {
            ee.printStackTrace();
            fail(ExceptionUtils.getFullStackTrace(ee));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testConvertValue() {

        String oldUserHome = System.getProperty("user.home");

        System.setProperty("user.home", "/home/test/");
        System.setProperty("msun.web.app.name", "bops");

        String value = "{user.home}/abc/{web.app.name}/def";

        assertEquals(value, ConfigServiceImpl.convertValue("I_key", value));
        assertEquals("/home/test//abc/bops/def", ConfigServiceImpl.convertValue("S_key", value));
        assertEquals("/home/test//abc/bops/def", ConfigServiceImpl.convertValue("SA_key", value));

        System.setProperty("user.home", oldUserHome);
    }
}
