/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.app.web.commons.utils;

import java.util.ArrayList;
import java.util.List;

import com.ms.commons.db.pagination.PageInfo;
import com.ms.commons.db.pagination.PagesPagination;

/**
 * @author zxc Apr 12, 2013 10:51:38 PM
 */
public class PagesPaginationUtil {

    public static final String LASTPAGE  = "尾页";
    public static final String NEXTPAGE  = "下一页";
    public static final String PREPAGE   = "上一页";
    public static final String FIRSTPAGE = "首页";

    /**
     * 初始化分页的页码URL
     * 
     * @param pagination
     * @param url
     */
    public static void initPages(PagesPagination pagination, IPageUrl url) {
        initPages(-1, pagination, url);
    }

    /**
     * 初始化分页的页码URL
     * 
     * @param pagination
     * @param url
     * @param data
     */
    public static void initPages(PagesPagination pagination, IPageUrl url, Object... data) {
        initPages(-1, pagination, url, data);
    }

    /**
     * 初始化分页的页码URL
     * 
     * @param action
     * @param pagination
     * @param url
     * @param data
     */
    public static void initPages(int action, PagesPagination pagination, IPageUrl url, Object... data) {
        if (pagination == null || url == null) {
            return;
        }
        // 首页
        int index = pagination.getFirstPageIndex();
        String pageUrl = url.getPageUrl(action, index, data);
        PageInfo firstpage = new PageInfo(FIRSTPAGE, index + 1, pagination.isFirstPage(), pageUrl);
        pagination.setFirstPage(firstpage);
        // 上一页
        index = pagination.getPrevPageIndex();
        pageUrl = url.getPageUrl(action, index, data);
        PageInfo prevpage = new PageInfo(PREPAGE, index + 1, pagination.isFirstPage(), pageUrl);
        pagination.setPrevPage(prevpage);
        // 下一页
        index = pagination.getNextPageIndex();
        pageUrl = url.getPageUrl(action, index, data);
        PageInfo nextpage = new PageInfo(NEXTPAGE, index + 1, pagination.isLastPage(), pageUrl);
        pagination.setNextPage(nextpage);
        // 尾页
        index = pagination.getLastPageIndex();
        pageUrl = url.getPageUrl(action, index, data);
        PageInfo lastpage = new PageInfo(LASTPAGE, index + 1, pagination.isLastPage(), pageUrl);
        pagination.setLastPage(lastpage);
        //
        int nowPageIndex = pagination.getNowPageIndex();
        List<PageInfo> pages = new ArrayList<PageInfo>();
        List<Integer> skipPageIndexs = pagination.getSkipPageIndex();
        for (Integer integer : skipPageIndexs) {
            pageUrl = url.getPageUrl(action, integer, data);
            pages.add(new PageInfo(String.valueOf(integer + 1), integer + 1, nowPageIndex == integer, pageUrl));
        }
        pagination.setPages(pages);
    }
}
