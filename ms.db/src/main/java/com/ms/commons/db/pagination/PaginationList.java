/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.pagination;

import java.util.ArrayList;

/**
 * @author zxc Apr 12, 2013 5:02:05 PM
 */
public class PaginationList<E> extends ArrayList<E> {

    private static final long serialVersionUID = 8761383695528059074L;
    private Pagination        query;

    public PaginationList(Pagination query) {
        super();
        this.query = query;
    }

    public Pagination getQuery() {
        return query;
    }

    public void setQuery(Pagination query) {
        this.query = query;
    }

    public int getTotalItem() {
        return query.getAllRecordNum();
    }

    /**
     * 返回总页数
     * 
     * @return
     */
    public int getAllPageNum() {
        return query == null ? 0 : query.getAllPageNum();
    }

    /**
     * 返回当前页面
     * 
     * @return
     */
    public int getNowPageIndex() {
        return query == null ? 0 : query.getNowPageIndex();
    }
}
