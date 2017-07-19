/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import com.ms.commons.result.Result;
import com.ms.scombiz.solr.pojo.SearchField;
import com.ms.scombiz.solr.query.SearchQuery;
import com.ms.scombiz.solr.solr.SolrClient;

/**
 * @author zxc Apr 12, 2013 9:36:24 PM
 */
public abstract class BaseSearch<T extends SearchField, Q extends SearchQuery> implements SearchServiceConfig, VersionableSearch<T, Q> {

    protected SolrClient solrClient;
    protected Class<T>   filedType;

    @SuppressWarnings("unchecked")
    public BaseSearch() {
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericSuperclass;
                Type[] actualTypeArguments = type.getActualTypeArguments();
                filedType = (Class<T>) actualTypeArguments[0];
            } else {
                throw new RuntimeException(String.format("没有找到【%s】的动态参数T", getClass().getSimpleName()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    protected Result beforeDel(Q query) {
        return null;
    }

    protected Result beforeSearch(Q query) {
        return null;
    }

    @Override
    public void indexWithOutDel(Integer version, List<T> fields) {
        solrClient.addBeans(getCoreName(version), fields);
    }

    @Override
    public void indexWithOutDel(Integer version, T field) {
        solrClient.addBean(getCoreName(version), field);
    }

    @Override
    public Result delAll(Integer version) {
        return solrClient.delAll(getCoreName(version)) ? Result.success() : Result.failed();
    }

    @Override
    public Result del(Integer version, Q query) {
        Result result = beforeDel(query);
        if (result != null && !result.isSuccess()) {
            return result;
        }
        SolrQuery solrQuery = convert(query);
        return solrClient.del(getCoreName(version), solrQuery) ? Result.success() : Result.failed();
    }

    @Override
    public List<T> search(Integer version, Q query) {
        Result result = beforeSearch(query);
        if (result != null && !result.isSuccess()) {
            return Collections.<T> emptyList();
        }
        SolrQuery solrQuery = convert(query);
        return solrClient.query(getCoreName(version), filedType, solrQuery);
    }

    public abstract String getCoreName(Integer version);

    public abstract SolrQuery convert(Q query);
}
