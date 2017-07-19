/*
 * Copyright 2017-2025 msun.com All right reserved. This software is the confidential and proprietary information of
 * msun.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with msun.com.
 */
package com.ms.commons.combiz.service;

import com.ms.commons.core.CommonServiceLocator;
import com.ms.commons.db.jdbc.DataSource;

/**
 * @author zxc Apr 13, 2013 9:55:49 PM
 */
public class CommonBizServiceLocator extends CommonServiceLocator {

    public static CommonBizService getCommonBizService() {
        return (CommonBizService) getBean("commonBizService");
    }

    /**
     * 获取数据源
     * 
     * @return
     */
    public static DataSource getDataSource() {
        return (DataSource) getBean("dataSource");
    }
}
