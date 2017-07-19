/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.solr;

import com.ms.commons.core.CommonServiceLocator;

/**
 * @author zxc Apr 12, 2013 9:37:43 PM
 */
@SuppressWarnings("unused")
public class SolrClientTest {

    private static final String hotwords2  = "宝暖内衣";
    private static final String hotwords1  = "波司登 羽绒服 女 长款";
    private SolrClient          solrClient = (SolrClient) CommonServiceLocator.getApplicationContext().getBean("ferrari_solr_client");
    private String              corename   = "test";
}
