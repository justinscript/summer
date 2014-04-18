/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.query;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

/**
 * @author zxc Apr 12, 2013 9:36:55 PM
 */
public interface SearchQuery {

    int DEFAULT_ROWS = 200;

    int getRows();

    int getStart();

    String getSortFiled();

    ORDER getOrderBy();

    // 转换为Solr查询格式
    SolrQuery toSolrQuery();
}
