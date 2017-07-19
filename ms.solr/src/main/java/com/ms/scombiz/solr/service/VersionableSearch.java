/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.scombiz.solr.service;

import java.util.List;

import com.ms.commons.result.Result;

/**
 * 有版本号的Search方法
 * 
 * @author zxc Apr 12, 2013 9:35:06 PM
 */
public interface VersionableSearch<T, Q> {

    void indexWithOutDel(Integer version, List<T> fields);

    void indexWithOutDel(Integer version, T field);

    Result delAll(Integer version);

    Result del(Integer version, Q query);

    List<T> search(Integer version, Q query);

    // String getCoreName(Integer version);
}
