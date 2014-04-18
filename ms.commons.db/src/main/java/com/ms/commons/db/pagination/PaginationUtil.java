/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.db.pagination;

import java.util.List;

/**
 * 提供一些分页的方法,主要是针对使用缓存时的分页
 * 
 * @author zxc Apr 12, 2013 5:01:52 PM
 */
public class PaginationUtil {

    /**
     * 对所有数据进行分页,每页默认10个
     * 
     * @param <T>
     * @param all 所有数据
     * @param nowPageIndex 第几页(1,2,3,4......)
     * @return
     */
    public static <T> PaginationList<T> getPaginationList(List<T> all, int nowPageIndex) {
        return getPaginationList(all, nowPageIndex, Pagination.DEFAULT_PAGESIZE);
    }

    /**
     * 对所有数据进行分页
     * 
     * @param <T>
     * @param all 所有数据
     * @param nowPageIndex 第几页(1,2,3,4......)
     * @param pageSize 每页数量
     * @return
     */
    public static <T> PaginationList<T> getPaginationList(List<T> all, int nowPageIndex, int pageSize) {
        return getPaginationList(all, new Pagination(), nowPageIndex, pageSize);
    }

    /**
     * 对所有数据进行分页
     * 
     * @param <T>
     * @param all 所有数据
     * @param nowPageIndex 第几页(1,2,3,4......)
     * @param pageSize 每页数量
     * @return
     */
    public static <T> PaginationList<T> getPaginationList(List<T> all, Pagination pagination, int nowPageIndex,
                                                          int pageSize) {
        if (all == null || pagination == null) {
            return null;
        }
        int size = all.size();
        nowPageIndex = nowPageIndex >= 1 ? nowPageIndex - 1 : 0;
        pageSize = pageSize > 0 ? pageSize : Pagination.DEFAULT_PAGESIZE;
        pagination.setNowPageIndex(nowPageIndex);
        pagination.setPageSize(pageSize);
        pagination.init(size);
        int start = pagination.getStartRecordIndex();
        int end = pagination.getEndRecordIndex();
        end = end > size ? size : end;
        start = start - 1 >= 0 ? start - 1 : 0;
        end = end - 1;
        PaginationList<T> page = new PaginationList<T>(pagination);
        for (int i = start; i <= end; i++) {
            page.add(all.get(i));
        }
        return page;
    }
}
