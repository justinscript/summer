/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.summer.web.util.json;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 4:07:20 PM
 */
public class JsonUtilTest extends TestCase {

    public void testobject2Json() {
        StringWriter sw = new StringWriter();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "xiongcaizhang");
        map.put("name2", "zxc");
        try {
            JsonUtils.object2Json(map, sw);
        } catch (Exception e) {
            fail();
        }
        String result = "{\"name\":\"xc_zhang\",\"name2\":\"zxc\"}";
        assertEquals(result, sw.getBuffer().toString());
        // System.out.println(sw.getBuffer());
    }

    public void testobject2Json2() {
        StringWriter sw = new StringWriter();
        User user = new User();
        user.setId(10);
        user.setName("zxc");
        try {
            JsonUtils.object2Json(user, sw);
        } catch (Exception e) {
            // e.printStackTrace();
            fail();
        }
        // System.out.println(sw.getBuffer());
        String result = "{\"id\":10,\"name\":\"zxc\"}";
        assertEquals(result, sw.getBuffer().toString());
    }

    public void testobject2Json3() {
        StringWriter sw = new StringWriter();
        User user = new User();
        user.setId(10);
        user.setName("zxc");
        User user2 = new User();
        user2.setId(11);
        user2.setName("xczhang");
        List<User> list = new ArrayList<User>();
        list.add(user);
        list.add(user2);
        try {
            JsonUtils.object2Json(list, sw);
        } catch (Exception e) {
            // e.printStackTrace();
            fail();
        }
        // System.out.println(sw.getBuffer());
        String result = "[{\"id\":10,\"name\":\"zxc\"},{\"id\":11,\"name\":\"xczhang\"}]";
        assertEquals(result, sw.getBuffer().toString());
    }

    public void testobject2Json4() {
        StringWriter sw = new StringWriter();
        try {
            JsonUtils.object2Json("abc", sw);
        } catch (Exception e) {
            return;
        }
        fail();
    }

    public void testobject2Json5() {
        StringWriter sw = new StringWriter();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "xiongc<br>aizhang");
        try {
            JsonUtils.object2Json(map, sw);
        } catch (Exception e) {
            fail();
        }
        // System.out.println(sw.getBuffer());
        String result = "{\"name\":\"xiongc&lt;br&gt;aizhang\"}";
        assertEquals(result, sw.getBuffer().toString());
    }

    public void testobject2Json6() {
        StringWriter sw = new StringWriter();
        User user = new User();
        user.setId(10);
        user.setName("zxc");
        HashMap<String, User> map = new HashMap<String, User>();
        map.put("java", user);
        try {
            JsonUtils.object2Json(map, sw);
        } catch (Exception e) {
            fail();
        }
        // System.out.println(sw.getBuffer());
        String result = "{\"java\":{\"id\":10,\"name\":\"zxc\"}}";
        assertEquals(result, sw.getBuffer().toString());
    }
}
