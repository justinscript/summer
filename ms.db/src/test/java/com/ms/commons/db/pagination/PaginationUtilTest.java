/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.pagination;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author zxc Apr 12, 2013 5:18:24 PM
 */
public class PaginationUtilTest extends TestCase {

    @Test
    public void testgetPaginationList() {
        List<String> all = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            all.add("" + i);
        }
        List<String> paginationList = PaginationUtil.getPaginationList(all, 1);
        assertEquals(30, paginationList.size());
        assertEquals("0", paginationList.get(0));
        assertEquals("9", paginationList.get(9));
    }

    @Test
    public void testgetPaginationList2() {
        List<String> all = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            all.add("" + i);
        }
        List<String> paginationList = PaginationUtil.getPaginationList(all, 1, 5);
        assertEquals(5, paginationList.size());
        assertEquals("0", paginationList.get(0));
        assertEquals("4", paginationList.get(4));
    }

    @Test
    public void testgetPaginationList3() {
        List<String> all = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            all.add("" + i);
        }
        List<String> paginationList = PaginationUtil.getPaginationList(all, 1, 5);
        assertEquals(3, paginationList.size());
        assertEquals("0", paginationList.get(0));
        assertEquals("2", paginationList.get(2));
    }

    @Test
    public void testgetPaginationList4() {
        List<String> all = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            all.add("" + i);
        }
        List<String> paginationList = PaginationUtil.getPaginationList(all, 2, 5);
        assertEquals(0, paginationList.size());
    }

    @Test
    public void testgetPaginationList5() {
        List<String> all = new ArrayList<String>();
        List<String> paginationList = PaginationUtil.getPaginationList(all, 1, 5);
        assertEquals(0, paginationList.size());
    }
}
