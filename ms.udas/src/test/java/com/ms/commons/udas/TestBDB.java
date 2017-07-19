/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.udas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.DatabaseException;
import com.ms.commons.udas.impl.UdasObj;
import com.ms.commons.udas.impl.handler.BdbHandler;

/**
 * @author zxc Apr 12, 2013 6:36:03 PM
 */
public class TestBDB {

    private String path = System.getProperty("user.home") + System.getProperty("file.separator") + "testbdb";

    @Before
    public void before() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void testBDBConfig() {
        new BdbHandler(path + "test_bdb_cache");
    }

    @Test
    public void testBDBString() {
        BdbHandler mbdb = new BdbHandler(path + "#" + "test_bdb_cache");
        // 必须先在你的C盘中创建文件夹bdb
        try {
            System.out.println(mbdb.getEnv().getConfig());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        String key = "testKey1";
        String value = "testKey1_value";
        String key2 = "testKey2";
        String value2 = "testKey2_value";

        try {
            mbdb.putString(key, value);
            mbdb.putString(key2, value2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            boolean flag = mbdb.delString(key2);
            assertEquals(true, flag);
            assertEquals(value, mbdb.getString(key));
            assertNull(mbdb.getString(key2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mbdb.close();
    }

    @Test
    public void testBDBObject() {
        BdbHandler mbdb = new BdbHandler(path + "#" + "test_bdb_Object");
        // 必须先在你的C盘中创建文件夹bdb
        try {
            System.out.println(mbdb.getEnv().getConfig());
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setId(1L);
        user.setUsername("panguojun");
        user.setWebsite("www.pgj.com");

        String key = "testKeyObject";
        try {
            mbdb.put(key, new UdasObj(System.currentTimeMillis(), user));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            UdasObj obj = mbdb.get(key);
            User newUser = (User) ((UdasObj) obj).getValue();
            assertEquals(user.getUsername(), newUser.getUsername());
            System.out.println(obj);

            boolean flag = mbdb.delString(key);
            assertEquals(true, flag);
            assertNull(mbdb.get(key));

        } catch (Exception e) {
            e.printStackTrace();
        }
        mbdb.close();
    }

    public static class User implements Serializable {

        /**
		 * 
		 */
        private static final long serialVersionUID = -5797201594573354983L;
        private Long              id;
        private String            username;
        private String            website;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", username=" + username + ", website=" + website + "]";
        }
    }
}
