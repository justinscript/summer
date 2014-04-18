/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.scombiz.solr.solr;

import org.slf4j.Logger;

import com.ms.commons.log.LoggerFactoryWrapper;

/**
 * @author zxc Apr 12, 2013 9:34:27 PM
 */
public interface SolrConfig {

    Logger logger              = LoggerFactoryWrapper.getLogger(SolrConfig.class);
    String KEY_ROOT_SERVER_URL = "S_spiderman.root_solr_server_url";
    String KEY_ROOT_INDEX_DIR  = "S_spiderman.root_index_dir";
}
