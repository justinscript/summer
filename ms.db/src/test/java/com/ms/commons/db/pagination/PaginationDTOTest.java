/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.db.pagination;

import junit.framework.TestCase;

/**
 * @author zxc Apr 12, 2013 5:18:55 PM
 */
public class PaginationDTOTest extends TestCase {

    public void testinit() {
        Pagination paginationDTO = new Pagination();
        paginationDTO.setNowPageIndex(0);
        paginationDTO.init(21);
        assertEquals(1, paginationDTO.getAllPageNum());
        assertEquals(1, paginationDTO.getStartRecordIndex());
        assertEquals(30, paginationDTO.getEndRecordIndex());
        assertEquals(0, paginationDTO.getFirstGotoPageIndex());
        assertEquals(0, paginationDTO.getLastGotoPageIndex());
        assertEquals(30, paginationDTO.getPageSize());
        assertEquals(0, paginationDTO.getNowPageIndex());
    }

    public void testinit2() {
        Pagination paginationDTO = new Pagination();
        paginationDTO.setNowPageIndex(1);
        paginationDTO.setPageSize(5);
        paginationDTO.init(21);
        assertEquals(5, paginationDTO.getAllPageNum());
        assertEquals(6, paginationDTO.getStartRecordIndex());
        assertEquals(10, paginationDTO.getEndRecordIndex());
        assertEquals(0, paginationDTO.getFirstGotoPageIndex());
        assertEquals(4, paginationDTO.getLastGotoPageIndex());
        assertEquals(5, paginationDTO.getPageSize());
        assertEquals(1, paginationDTO.getNowPageIndex());
        assertEquals(0, paginationDTO.getPrevPageIndex());
        assertEquals(2, paginationDTO.getNextPageIndex());
    }

    public void testinit3() {
        Pagination paginationDTO = new Pagination();
        paginationDTO.setNowPageIndex(4);
        paginationDTO.setPageSize(5);
        paginationDTO.init(21);
        assertEquals(5, paginationDTO.getAllPageNum());
        assertEquals(21, paginationDTO.getStartRecordIndex());
        assertEquals(25, paginationDTO.getEndRecordIndex());
        assertEquals(0, paginationDTO.getFirstGotoPageIndex());
        assertEquals(4, paginationDTO.getLastGotoPageIndex());
        assertEquals(5, paginationDTO.getPageSize());
        assertEquals(4, paginationDTO.getNowPageIndex());
        assertEquals(3, paginationDTO.getPrevPageIndex());
        assertEquals(4, paginationDTO.getNextPageIndex());
    }
}
