/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分页迭代器
 * 
 * @author zxc Apr 12, 2013 2:34:08 PM
 */
public class IteratorWrapper<T extends Object> {

    private List<T> data;
    private int     pageSize;

    public static int getPageCount(int size, int pageSize) {
        int pageCount = size / pageSize;
        if (size % pageSize != 0) {
            pageCount++;
        }
        return pageCount;
    }

    public static <T extends Object> IteratorWrapper<T> pagination(Collection<T> data, int pageSize) {
        IteratorWrapper<T> it = new IteratorWrapper<T>();
        if (data == null) {
            return it;
        }
        if (data instanceof List) {
            it.data = (List<T>) data;
        } else {
            it.data = new ArrayList<T>(data);
        }
        it.pageSize = pageSize;
        return it;
    }

    public static <T extends Object> IteratorWrapper<T> pagination(T[] data, int pageSize) {
        IteratorWrapper<T> it = new IteratorWrapper<T>();
        if (data == null) {
            return it;
        }
        return pagination(Arrays.asList(data), pageSize);
    }

    public void iterator(Handler<T> handler, Object... params) {
        if (data == null || data.isEmpty()) {
            return;
        }
        int total = data.size(), pageCount = getPageCount(total, pageSize);
        for (int pageNum = 0; pageNum < pageCount; pageNum++) {
            int start = Math.min(pageNum * pageSize, total), end = Math.min((pageNum + 1) * pageSize, total);
            if (start >= end) {
                break;
            }
            List<T> subList = data.subList(start, end);
            try {
                if (!handler.handle(pageNum, subList, params)) {
                    return;
                }
            } catch (Exception e) {
                if (handler.onException(e, pageNum, subList, params) == false) return;
            }
        }

    }

    public static abstract class Handler<T extends Object> {

        private static Logger logger = LoggerFactory.getLogger(Handler.class);

        /**
         * return false 则迭代不再继续
         * 
         * @param pageNum
         * @param subData
         * @param params
         * @return
         */
        public abstract boolean handle(int pageNum, List<T> subData, Object... params);

        /**
         * 执行某次迭代发生异常
         * 
         * @param e
         * @param pageNum
         * @param subData
         * @param params
         * @return true则迭代继续，否则迭代退出
         */
        public boolean onException(Throwable e, int pageNum, Collection<T> subData, Object... params) {
            logger.error(e.getMessage(), e);
            return true;
        }

    }

    public static void main(String[] args) {
        Integer[] data = new Integer[] { 1, 2, 3, 4, 5, 6 };
        Handler<Integer> handler = new Handler<Integer>() {

            @Override
            public boolean handle(int pageNum, List<Integer> subData, Object... params) {
                System.out.println("第" + pageNum + "页" + subData);
                return true;
            }
        };
        IteratorWrapper.pagination(data, 2).iterator(handler);
        Set<Integer> data2 = new HashSet<Integer>(Arrays.asList(data));
        IteratorWrapper.pagination(data2, 2).iterator(handler);

    }
}
