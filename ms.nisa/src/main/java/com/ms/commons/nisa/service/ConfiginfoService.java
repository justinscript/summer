/*
 * Copyright 2011-2016 ZXC.com All right reserved. This software is the confidential and proprietary information of
 * ZXC.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with ZXC.com.
 */
package com.ms.commons.nisa.service;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author zxc Apr 12, 2013 6:45:05 PM
 */
public interface ConfiginfoService {

    /**
     * 根据应用和类型，返回对于的参数Map值
     * 
     * @param appname
     * @param type
     * @return
     */
    HashMap<String, Serializable> getConfigInfos(String project, String appname, String type);
}
