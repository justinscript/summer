/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

import com.ms.commons.utilities.StringFormatUtils;
import com.ms.scombiz.solr.query.SearchQuery;

/**
 * @author zxc Apr 12, 2013 9:34:08 PM
 */
public class BaseSolrQueryConvert {

    public static Character[]  invalid_chars     = new Character[] { '~', '^', '*', '"', '&', '[', ']', '(', ')', '{',
            '}', ':', '?'                       };
    public static String       invalid_chars_reg = StringUtils.join(invalid_chars, "|\\");

    public static final char   SPACE             = ' ';
    public static final String REGEX             = "[^0-9a-zA-Z\u4e00-\u9fa5]*";

    public static List<String> toStrList(List<Integer> intList) {
        if (intList == null) {
            return null;
        }
        List<String> strList = new ArrayList<String>();
        for (Integer id : intList) {
            if (id == null) {
                continue;
            }
            strList.add(String.valueOf(id));
        }
        return strList;
    }

    public static SolrQuery toAll() {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        return solrQuery;
    }

    protected static String filterQuery(String value) {
        value = value == null ? value : value.replaceAll(invalid_chars_reg, " ");
        return StringFormatUtils.matcherRegex(value, REGEX);
    }

    protected static SolrQuery setQuery(List<String> params, SearchQuery searchQuery) {
        SolrQuery solrQuery = new SolrQuery();
        String query = null;
        if (params.isEmpty()) {
            query = ("*:*");
        } else {
            query = StringUtils.join(params, " AND ");
        }
        solrQuery.setQuery(query);
        solrQuery.setStart(searchQuery.getStart());
        solrQuery.setRows(searchQuery.getRows());
        if (StringUtils.isNotBlank(searchQuery.getSortFiled())) {
            solrQuery.addSort(searchQuery.getSortFiled(), searchQuery.getOrderBy());
        }
        return solrQuery;
    }
}
