/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.scombiz.solr.query;

/**
 * @author zxc Apr 12, 2013 9:36:43 PM
 */
public enum SearchSortFields {

    GMT_CREATE("gmtCreate"),

    GMT_MODIFIED("gmtModifed"),

    ID("Id"),

    HOTWORD_CLICKCOUNT("clickCount");

    private String value;

    private SearchSortFields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
