/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.service;

/**
 * Solr服务不可用,抛出SolrServerUnAvailableException
 * 
 * @author zxc Apr 12, 2013 9:35:19 PM
 */
public class SolrServerUnAvailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SolrServerUnAvailableException() {
        super();
    }

    public SolrServerUnAvailableException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SolrServerUnAvailableException(String arg0) {
        super(arg0);
    }

    public SolrServerUnAvailableException(Throwable arg0) {
        super(arg0);
    }
}
