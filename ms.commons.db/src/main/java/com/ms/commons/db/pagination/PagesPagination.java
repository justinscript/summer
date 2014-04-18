/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.pagination;

import java.util.List;

/**
 * @author zxc Apr 12, 2013 5:02:42 PM
 */
public class PagesPagination extends Pagination {

    private List<PageInfo> pages;
    private PageInfo       firstPage;
    private PageInfo       lastPage;
    private PageInfo       prevPage;
    private PageInfo       nextPage;

    public List<PageInfo> getPages() {
        return pages;
    }

    public void setPages(List<PageInfo> pages) {
        this.pages = pages;
    }

    public PageInfo getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(PageInfo firstPage) {
        this.firstPage = firstPage;
    }

    public PageInfo getLastPage() {
        return lastPage;
    }

    public void setLastPage(PageInfo lastPage) {
        this.lastPage = lastPage;
    }

    public PageInfo getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(PageInfo prevPage) {
        this.prevPage = prevPage;
    }

    public PageInfo getNextPage() {
        return nextPage;
    }

    public void setNextPage(PageInfo nextPage) {
        this.nextPage = nextPage;
    }
}
